package com.emi.wac.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.emi.wac.common.Constants.CATEGORY_F1
import com.emi.wac.common.Constants.CATEGORY_INDYCAR
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
 * @param application The application context used to initialize repositories.
 */
class HomeViewModel(application: Application) :
    AndroidViewModel(application) { // Repositories for fetching race and standings data
    private val db = Firebase.firestore
    private val standingsRepository = StandingsRepository(db)
    private val repository = RacingRepository(standingsRepository, application)

    /**
     * Data class holding race information and its associated sessions.
     * @property grandPrix The race details including name, leader, and timing.
     * @property sessions List of sessions for the race (e.g., FP1, Qualy, Race).
     */
    data class RaceData(val grandPrix: RaceInfo, val sessions: List<SessionData>)

    /**
     * Data class representing a single race session with its timing and duration.
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
    private val _indycarRaceData = MutableStateFlow<DataState<RaceData>>(DataState.Loading)

    /**
     * Public flow exposing the next race data.
     */
    val nextMotoGPRace = _motoGPRaceData.asStateFlow()
    val nextF1Race = _f1RaceData.asStateFlow()
    val nextIndycarRace = _indycarRaceData.asStateFlow()

    // Job to manage the update timer
    private var updateJob: Job? = null

    init {
        // Start fetching race data and updating timer on initialization
        viewModelScope.launch {
            listOf(
                CATEGORY_F1 to _f1RaceData,
                CATEGORY_MOTOGP to _motoGPRaceData,
                CATEGORY_INDYCAR to _indycarRaceData
            ).forEach { (category, flow) ->
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
     * @param category The race category (e.g., "f1", "motogp").
     * @param raceDataFlow The MutableStateFlow to emit the race data state.
     */
    private suspend fun updateRaceData(
        category: String,
        raceDataFlow: MutableStateFlow<DataState<RaceData>>
    ) {
        raceDataFlow.value = DataState.Loading
        try {
            val leaderName = standingsRepository.getLeaderDriver(category).getOrNull()?.name ?: ""
            val grandPrixObject = repository.getNextGrandPrixObject(category)

            if (grandPrixObject == null) {
                Log.w("HomeViewModel", "No upcoming Grand Prix found for category: $category")
                raceDataFlow.value = DataState.Error("No upcoming races found for $category")
                return
            }

            val raceInfo = repository.getNextGrandPrix(category, leaderName)

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
                session?.takeIf { it.day.isNotEmpty() && it.time.isNotEmpty() }
                    ?.let { DateUtils.parseDate(it.day, it.time, DateUtils.getCurrentYear()) }
                    ?.let {
                        SessionData(name, it)
                    }
            }.sortedBy { it.dateTime }

            if (sessions.isEmpty()) {
                Log.w(
                    "HomeViewModel",
                    "No valid sessions found for Grand Prix ${grandPrixObject.gp} in category: $category"
                )
                raceDataFlow.value =
                    DataState.Error("No valid sessions found for upcoming race in $category")
                return
            }

            val (sessionName, timeRemaining) = calculateSessionAndTimeRemaining(sessions)
            raceDataFlow.value = DataState.Success(
                RaceData(
                    grandPrix = raceInfo.copy(
                        sessionName = sessionName ?: sessions.first().name,
                        timeRemaining = timeRemaining
                    ),
                    sessions = sessions
                )
            )
        } catch (e: Exception) {
            val errorMessage = "Error loading race data for category $category: ${e.message}"
            Log.e("HomeViewModel", errorMessage, e)
            raceDataFlow.value = DataState.Error(errorMessage)
        }
    }

    /**
     * Updates the time remaining for all race categories (F1, MotoGP, IndyCar).
     * This function is called periodically to provide live countdowns or status.
     */
    private fun updateTimeRemaining() {
        val currentTime =
            Calendar.getInstance(TimeZone.getTimeZone("UTC")).time
        listOf(
            _f1RaceData to CATEGORY_F1,
            _motoGPRaceData to CATEGORY_MOTOGP,
            _indycarRaceData to CATEGORY_INDYCAR
        ).forEach { (flow, category) ->
            // If data is already loaded, update the time remaining
            (flow.value as? DataState.Success)?.let { state ->
                val (sessionName, timeRemaining) = calculateSessionAndTimeRemaining(
                    state.data.sessions,
                    currentTime
                )
                if (sessionName == null) {
                    // If no next session is found, it means the race might be over or data needs refresh
                    viewModelScope.launch { updateRaceData(category, flow) }
                } else {
                    // Update existing race data with new time remaining and session name
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
     * Calculates the current session and time remaining based on the provided list of sessions.
     * It determines if a session is currently live, or calculates the time until the next upcoming session.
     * @param sessions List of session data sorted by date and time.
     * @param currentTime The current time to compare against (defaults to current UTC time).
     * @return A Pair where the first element is the name of the current/next session (or null if none) and the second is the formatted time remaining string.
     */
    private fun calculateSessionAndTimeRemaining(
        sessions: List<SessionData>,
        currentTime: Date = Calendar.getInstance(TimeZone.getTimeZone("UTC")).time
    ): Pair<String?, String> {
        // Checks if any session is currently live
        val currentSession =
            sessions.find { currentTime in it.dateTime..Date(it.dateTime.time + it.duration) }
        if (currentSession != null) return currentSession.name to "● LIVE"

        // Find the next upcoming session
        val nextSession = sessions.find { it.dateTime > currentTime }
        return nextSession?.let {
            // Calculate hours until next session
            val timeToNextHours = (it.dateTime.time - currentTime.time) / (1000 * 60 * 60)
            // Show date if more than 24 hours away, otherwise show countdown
            it.name to if (timeToNextHours >= 24) DATE_FORMAT.format(it.dateTime)
            else DateUtils.calculateTimeRemaining(
                it.dateTime,
                Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            )
            // If no sessions are found or all are in the past
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
