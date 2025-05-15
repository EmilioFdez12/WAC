package com.emi.wac.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.emi.wac.common.Constants.CATEGORY_F1
import com.emi.wac.common.Constants.CATEGORY_MOTOGP
import com.emi.wac.common.Constants.DATE_FORMAT
import com.emi.wac.common.Constants.RACE_DURATION
import com.emi.wac.common.Constants.SESSION_DURATION
import com.emi.wac.common.Constants.SESSION_TYPES
import com.emi.wac.data.model.RaceInfo
import com.emi.wac.data.repository.RacingRepository
import com.emi.wac.data.repository.StandingsRepository
import com.emi.wac.utils.DateUtils
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

/**
 * ViewModel responsible for managing race data and session timing for F1 and MotoGP.
 * Provides live updates for upcoming races and sessions, including countdowns and LIVE status.
 *
 * @param application The application context used to initialize repositories.
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    // Repositories for fetching race and standings data
    private val repository = RacingRepository(application)
    private val db = Firebase.firestore
    private val standingsRepository = StandingsRepository(db)

    /**
     * Data class holding race information and its associated sessions.
     *
     * @property grandPrix The race details including name, leader, and timing.
     * @property sessions List of sessions for the race (e.g., FP1, Qualy, Race).
     */
    data class RaceData(val grandPrix: RaceInfo, val sessions: List<SessionData>)

    /**
     * Data class representing a single race session with its timing and duration.
     *
     * @property name The name of the session (e.g., "FP 1", "Race").
     * @property dateTime The start time of the session.
     * @property duration Duration in milliseconds (2 hours for Race, 1 hour otherwise).
     */
    data class SessionData(
        val name: String,
        val dateTime: Date,
        val duration: Long = if (name == "Race") RACE_DURATION else SESSION_DURATION
    )

    // State flows for F1 and MotoGP race data
    private val _f1RaceData = MutableStateFlow<DataState<RaceData>>(DataState.Loading)
    private val _motoGPRaceData = MutableStateFlow<DataState<RaceData>>(DataState.Loading)

    /** Public flow exposing the next race data. */
    val nextMotoGPRace = _motoGPRaceData.asStateFlow()
    val nextF1Race = _f1RaceData.asStateFlow()

    // Job to manage the update timer
    private var updateJob: Job? = null

    init {
        // Start fetching race data and updating timer on initialization
        viewModelScope.launch {
            listOf(CATEGORY_F1 to _f1RaceData, CATEGORY_MOTOGP to _motoGPRaceData).forEach { (category, flow) ->
                updateRaceData(category, flow)
            }
            startRaceUpdateTimer()
        }
    }

    /**
     * Starts a coroutine to update the remaining time for races every second.
     */
    private fun startRaceUpdateTimer() {
        // Cancel any existing job
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                updateTimeRemaining()
            }
        }
    }

    /**
     * Updates race data for a given category and emits it to the provided flow.
     *
     * @param category The race category (e.g., "f1", "motogp").
     * @param raceDataFlow The MutableStateFlow to emit the race data state.
     */
    private suspend fun updateRaceData(
        category: String,
        raceDataFlow: MutableStateFlow<DataState<RaceData>>
    ) {
        // Set loading state
        raceDataFlow.value = DataState.Loading
        try {
            // Fetch leader name
            val leaderName = standingsRepository.getLeaderDriver(category).getOrNull()?.driver ?: ""
            // Find next GP
            val grandPrixObject = repository.getNextGrandPrixObject(category)
                ?: throw IllegalStateException("No Grand Prix found")
            val raceInfo = repository.getNextGrandPrix(category, leaderName)

            // Map session types to SessionData objects and sort by date
            val sessions: List<SessionData> = SESSION_TYPES.mapNotNull { (key, name) ->
                val session = grandPrixObject.sessions.run {
                    when (key) {
                        "practice1" -> practice1
                        "practice2" -> practice2
                        "practice3" -> practice3
                        "sprintQualifying" -> sprintQualifying
                        "sprint" -> sprint
                        "qualifying" -> qualifying
                        "race" -> race
                        else -> null
                    }
                }
                // Filter valid sessions
                session?.takeIf { it.day.isNotEmpty() && it.time.isNotEmpty() }
                    ?.let { DateUtils.parseDate(it.day, it.time, DateUtils.getCurrentYear()) }
                    ?.let {
                        SessionData(name, it)
                    }
            }.sortedBy { it.dateTime }

            if (sessions.isEmpty()) throw IllegalStateException("No valid sessions found")

            // Calculate initial session and time remaining
            val (sessionName, timeRemaining) = calculateSessionAndTimeRemaining(sessions)
            raceDataFlow.value = DataState.Success(
                RaceData(
                    grandPrix = raceInfo.copy(
                        sessionName = sessionName
                            ?: sessions.first().name,
                        timeRemaining = timeRemaining
                    ),
                    sessions = sessions
                )
            )
        } catch (e: Exception) {
            raceDataFlow.value =
                DataState.Error("Error loading race data: ${e.message}")
        }
    }

    /**
     * Updates the time remaining for both F1 and MotoGP races.
     */
    private fun updateTimeRemaining() {
        val currentTime =
            Calendar.getInstance(TimeZone.getTimeZone("UTC")).time // Current time in UTC
        listOf(_f1RaceData to "f1", _motoGPRaceData to "motogp").forEach { (flow, category) ->
            (flow.value as? DataState.Success)?.let { state ->
                val (sessionName, timeRemaining) = calculateSessionAndTimeRemaining(
                    state.data.sessions,
                    currentTime
                )
                if (sessionName == null) {
                    // No next session, refresh race data
                    viewModelScope.launch { updateRaceData(category, flow) }
                } else {
                    // Update existing race data with new time remaining
                    flow.value = DataState.Success(
                        state.data.copy(
                            grandPrix = state.data.grandPrix.copy(
                                timeRemaining = timeRemaining,
                                sessionName = sessionName
                            )
                        )
                    )
                }
            }
        }
    }

    /**
     * Calculates the current session and time remaining based on the session list.
     *
     * @param sessions List of session data sorted by date.
     * @param currentTime The current time to compare against (defaults to current UTC time).
     * @return Pair of session name and time remaining (e.g., "FP 1" to "● LIVE" or "25 Apr").
     */
    private fun calculateSessionAndTimeRemaining(
        sessions: List<SessionData>,
        currentTime: Date = Calendar.getInstance(TimeZone.getTimeZone("UTC")).time
    ): Pair<String?, String> {
        // Check if any session is currently live
        val currentSession =
            sessions.find { currentTime in it.dateTime..Date(it.dateTime.time + it.duration) }
        if (currentSession != null) return currentSession.name to "● LIVE"

        // Find the next upcoming session
        val nextSession = sessions.find { it.dateTime > currentTime }
        return nextSession?.let {
            // Hours until next session
            val timeToNext =
                (it.dateTime.time - currentTime.time) / (1000 * 60 * 60)
            // Show date if > 24 hours
            it.name to if (timeToNext >= 24) DATE_FORMAT.format(it.dateTime)
            // Show countdown
            else DateUtils.calculateTimeRemaining(
                it.dateTime,
                Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            )
        } ?: (null to "No sessions")
    }

    /**
     * Cancels the update timer when the ViewModel is cleared.
     */
    override fun onCleared() {
        super.onCleared()
        updateJob?.cancel()
    }
}