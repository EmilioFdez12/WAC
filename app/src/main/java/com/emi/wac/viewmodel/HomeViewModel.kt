package com.emi.wac.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.emi.wac.common.Constants
import com.emi.wac.data.repository.RacingRepository
import com.emi.wac.data.repository.StandingsRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

/**
 * ViewModel for the home screen, managing data related to upcoming races.
 *
 * @property application The Android application context
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {
    // Repositories for accessing race and standings data
    private val repository = RacingRepository(application)
    private val standingsRepository = StandingsRepository(Firebase.firestore)
    // State flows for upcoming races
    private val _nextF1Race = MutableStateFlow(Constants.LOADING_RACE_INFO)
    val nextF1Race = _nextF1Race.asStateFlow()
    private val _nextMotoGPRace = MutableStateFlow(Constants.LOADING_RACE_INFO)
    val nextMotoGPRace = _nextMotoGPRace.asStateFlow()

    init {
        // Start race data updates when ViewModel is created
        startRaceUpdates()
    }

    /**
     * Starts the race data update process including initial update and timer setup.
     */
    private fun startRaceUpdates() {
        viewModelScope.launch {
            updateRaces()
            startRaceUpdateTimer()
        }
    }

    /**
     * Starts a timer that updates race data every second.
     */
    private fun startRaceUpdateTimer() {
        viewModelScope.launch {
            flow {
                while (true) {
                    emit(Unit)
                    delay(1000)
                }
            }.collect {
                updateRaces()
            }
        }
    }

    /**
     * Updates the state with the latest race information for both F1 and MotoGP.
     */
    private suspend fun updateRaces() {
        val f1LeaderName = getLeaderDriverName("f1")
        val motoGPLeaderName= getLeaderDriverName("motogp")

        try {
            _nextF1Race.value = repository.getNextGrandPrix("f1", f1LeaderName)
            _nextMotoGPRace.value = repository.getNextGrandPrix("motogp", motoGPLeaderName)
        } catch (_: Exception) {
            _nextF1Race.value = Constants.LOADING_RACE_INFO
            _nextMotoGPRace.value = Constants.LOADING_RACE_INFO
        }

    }

    /**
     * Retrieves the current championship leader for a specific racing category.
     *
     * @param category The racing category (e.g., "f1", "motogp")
     * @return The driver's name or empty string if not found
     */
    private suspend fun getLeaderDriverName(category: String): String {
        return standingsRepository.getLeaderDriver(category)
            .getOrNull()
            ?.driver ?: ""
    }
}