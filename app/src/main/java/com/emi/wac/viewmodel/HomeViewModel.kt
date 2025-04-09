package com.emi.wac.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.emi.wac.common.Constants.LOADING_RACE_INFO
import com.emi.wac.data.model.RaceInfo
import com.emi.wac.data.repository.RacingRepository
import com.emi.wac.data.repository.StandingsRepository
import com.emi.wac.data.utils.DateUtils
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

/**
 * ViewModel for the home screen, responsible for managing and updating data related to upcoming races.
 *
 * @param application The Android application context, to initialize repositories.
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {
    // Repositories for accessing race and standings data
    private val repository = RacingRepository(application)
    private val standingsRepository = StandingsRepository(Firebase.firestore)

    // Data class to store both static and dynamic race information for each category
    data class RaceData(
        // Initial race data that doesn't change usually
        val grandPrix: RaceInfo,
        // The parsed race date and time, to recalculate the remaining time dynamically
        val raceDateTime: Date
    )

    // MutableStateFlow to hold the race data
    // Public StateFlow to expose the race info to the UI, initialized with a loading state
    private val _f1RaceData = MutableStateFlow<RaceData?>(null)
    val nextF1Race = MutableStateFlow(LOADING_RACE_INFO)

    private val _motoGPRaceData = MutableStateFlow<RaceData?>(null)
    val nextMotoGPRace = MutableStateFlow(LOADING_RACE_INFO)

    private var updateJob: Job? = null


    // Initialize the ViewModel by starting the race data updates
    init {
        startRaceUpdates()
    }

    /**
     * Launches the initial race data update and starts the timer for real-time updates.
     */
    private fun startRaceUpdates() {
        viewModelScope.launch {
            // Perform an initial update to fetch static race data for F1 and MotoGP
            updateRaceData("f1", _f1RaceData, nextF1Race)
            updateRaceData("motogp", _motoGPRaceData, nextMotoGPRace)
            // Start the timer to continuously update the time remaining
            startRaceUpdateTimer()
        }
    }

    /**
     * Starts a timer that updates the time remaining every 800 milliseconds.
     */
    private fun startRaceUpdateTimer() {
        // Cancel any previous timer job to prevent duplicate timers
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            // Continuously loop while the coroutine scope is active
            while (isActive) {
                // Wait between updates
                delay(800)
                updateTimeRemaining()
            }
        }
    }

    /**
     * Fetches initial race data for a specific category and updates the corresponding flows.
     *
     * @param category The racing category (e.g., "f1", "motogp")
     * @param raceDataFlow Flow to store the full race data (static + race date)
     * @param raceInfoFlow Flow to expose the RaceInfo to the UI
     */
    private suspend fun updateRaceData(
        category: String,
        raceDataFlow: MutableStateFlow<RaceData?>,
        raceInfoFlow: MutableStateFlow<RaceInfo>
    ) {
        try {
            // Get the championship leader's name for the category
            val leaderName = getLeaderDriverName(category)
            // Fetch the full race info from the repository
            val raceInfo = repository.getNextGrandPrix(category, leaderName)
            val raceDateTime = DateUtils.parseDate(
                repository.getNextGrandPrixObject(category)?.sessions?.race?.day ?: "",
                repository.getNextGrandPrixObject(category)?.sessions?.race?.time ?: "",
                DateUtils.getCurrentYear()
            ) ?: throw IllegalStateException("No valid race date")
            // Store the race data (static info + date) in the internal flow
            raceDataFlow.value = RaceData(raceInfo, raceDateTime)
            // Update the UI flow with the initial race info
            raceInfoFlow.value = raceInfo
        } catch (_: Exception) {
            raceInfoFlow.value = LOADING_RACE_INFO
        }
    }

    /**
     * Updates the timeRemaining field based on the current time.
     * Uses the stored raceDateTime to avoid recalculating static data.
     */
    private fun updateTimeRemaining() {
        val currentDate = Calendar.getInstance()

        // Update F1 race time remaining if data is available
        _f1RaceData.value?.let { data ->
            // Create a new RaceInfo with updated timeRemaining
            val updatedRaceInfo = data.grandPrix.copy(
                timeRemaining = DateUtils.calculateTimeRemaining(data.raceDateTime, currentDate)
            )
            // Update the UI
            nextF1Race.value = updatedRaceInfo
        }

        // Update MotoGP race time remaining if data is available
        _motoGPRaceData.value?.let { data ->
            // Create a new RaceInfo with updated timeRemaining
            val updatedRaceInfo = data.grandPrix.copy(
                timeRemaining = DateUtils.calculateTimeRemaining(data.raceDateTime, currentDate)
            )
            // Update the UI
            nextMotoGPRace.value = updatedRaceInfo
        }
    }

    /**
     * Retrieves the name of the current championship leader for a specific category.
     *
     * @param category The racing category (e.g., "f1", "motogp")
     * @return The driver's name, an empty string if not found
     */
    private suspend fun getLeaderDriverName(category: String): String {
        return standingsRepository.getLeaderDriver(category)
            .getOrNull()
            ?.driver ?: "" // Return driver name or empty string if retrieval fails
    }

    /**
     * Cleans up resources when the ViewModel is cleared
     */
    override fun onCleared() {
        super.onCleared()
        updateJob?.cancel()
    }
}