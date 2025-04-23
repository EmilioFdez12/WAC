package com.emi.wac.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.emi.wac.data.model.RaceInfo
import com.emi.wac.data.repository.RacingRepository
import com.emi.wac.data.repository.StandingsRepository
import com.emi.wac.utils.DateUtils
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RacingRepository(application)
    private val standingsRepository = StandingsRepository(Firebase.firestore)

    data class RaceData(
        val grandPrix: RaceInfo,
        val sessions: List<SessionData>
    )

    data class SessionData(
        val name: String,
        val dateTime: Date,
        val duration: Long = 2 * 60 * 60 * 1000 // 2 hours in milliseconds
    )

    private val _f1RaceData = MutableStateFlow<DataState<RaceData>>(DataState.Loading)
    val nextF1Race = _f1RaceData.asStateFlow()

    private val _motoGPRaceData = MutableStateFlow<DataState<RaceData>>(DataState.Loading)
    val nextMotoGPRace = _motoGPRaceData.asStateFlow()

    private var updateJob: Job? = null

    init {
        startRaceUpdates()
    }

    private fun startRaceUpdates() {
        viewModelScope.launch {
            updateRaceData("f1", _f1RaceData)
            updateRaceData("motogp", _motoGPRaceData)
            startRaceUpdateTimer()
        }
    }

    private fun startRaceUpdateTimer() {
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                updateTimeRemaining()
            }
        }
    }

    private suspend fun updateRaceData(
        category: String,
        raceDataFlow: MutableStateFlow<DataState<RaceData>>
    ) {
        raceDataFlow.value = DataState.Loading
        try {
            val leaderName = getLeaderDriverName(category)
            val grandPrixObject = repository.getNextGrandPrixObject(category)
                ?: throw IllegalStateException("No Grand Prix found")
            val raceInfo = repository.getNextGrandPrix(category, leaderName)

            // Obtain all sessions
            val sessions = mutableListOf<SessionData>()
            val sessionMap = grandPrixObject.sessions
            val currentYear = DateUtils.getCurrentYear()

            // List of sessions ordered by date
            val sessionTypes = listOf(
                "practice1" to "FP 1",
                "practice2" to "FP 2",
                "practice3" to "FP 3",
                "sprintQualifying" to "Sprint Qualy",
                "sprint" to "Sprint",
                "qualifying" to "Qualy",
                "race" to "Race"
            )

            // Add sessions to the list
            sessionTypes.forEach { (key, name) ->
                val session = when (key) {
                    "practice1" -> sessionMap.practice1
                    "practice2" -> sessionMap.practice2
                    "practice3" -> sessionMap.practice3
                    "sprintQualifying" -> sessionMap.sprintQualifying
                    "sprint" -> sessionMap.sprint
                    "qualifying" -> sessionMap.qualifying
                    "race" -> sessionMap.race
                    else -> null
                }
                if (session != null && session.day.isNotEmpty() && session.time.isNotEmpty()) {
                    val dateTime = DateUtils.parseDate(session.day, session.time, currentYear)
                    if (dateTime != null) {
                        sessions.add(SessionData(name = name, dateTime = dateTime))
                    }
                }
            }

            if (sessions.isEmpty()) {
                throw IllegalStateException("No valid sessions found")
            }

            // Order by date
            sessions.sortBy { it.dateTime }

            // Calculate current or next session and time remaining
            val currentTime = Calendar.getInstance().time
            val (sessionName, timeRemaining) = calculateSessionAndTimeRemaining(sessions, currentTime)

            // Initialize with the correct session
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
            raceDataFlow.value = DataState.Error("Error loading race data: ${e.message}")
        }
    }

    private fun updateTimeRemaining() {
        val currentTime = Calendar.getInstance().time

        _f1RaceData.value.let { state ->
            if (state is DataState.Success) {
                updateRaceDataTimeRemaining(state, currentTime, _f1RaceData, "f1")
            }
        }

        _motoGPRaceData.value.let { state ->
            if (state is DataState.Success) {
                updateRaceDataTimeRemaining(state, currentTime, _motoGPRaceData, "motogp")
            }
        }
    }

    private fun updateRaceDataTimeRemaining(
        state: DataState.Success<RaceData>,
        currentTime: Date,
        raceDataFlow: MutableStateFlow<DataState<RaceData>>,
        category: String
    ) {
        val sessions = state.data.sessions
        val (sessionName, timeRemaining) = calculateSessionAndTimeRemaining(sessions, currentTime)

        if (sessionName == null) {
            // If there is no next session, load the next Grand Prix
            viewModelScope.launch {
                updateRaceData(category, raceDataFlow)
            }
            return
        }

        // Update with the session and time remaining
        val updatedRaceInfo = state.data.grandPrix.copy(
            timeRemaining = timeRemaining,
            sessionName = sessionName
        )
        raceDataFlow.value = DataState.Success(
            state.data.copy(grandPrix = updatedRaceInfo)
        )
    }

    private fun calculateSessionAndTimeRemaining(
        sessions: List<SessionData>,
        currentTime: Date
    ): Pair<String?, String> {
        // Search ongoing session
        val currentSession = sessions.find { session ->
            val sessionEnd = Date(session.dateTime.time + session.duration)
            session.dateTime <= currentTime && currentTime < sessionEnd
        }

        if (currentSession != null) {
            // Show LIVE for ongoing session
            return currentSession.name to "\u25CF LIVE"
        }

        // Search next session
        val nextSession = sessions.find { it.dateTime > currentTime }

        if (nextSession == null) {
            // No future sessions
            return null to "No sessions"
        }

        // Calculate time remaining for the next session
        val timeRemaining = DateUtils.calculateTimeRemaining(
            nextSession.dateTime,
            Calendar.getInstance()
        )
        return nextSession.name to timeRemaining
    }

    private suspend fun getLeaderDriverName(category: String): String {
        return standingsRepository.getLeaderDriver(category)
            .getOrNull()
            ?.driver ?: ""
    }

    override fun onCleared() {
        super.onCleared()
        updateJob?.cancel()
    }
}