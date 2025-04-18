package com.emi.wac.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.emi.wac.data.model.drivers.DriverStanding
import com.emi.wac.data.repository.StandingsRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing driver standings data
 */
class StandingsViewModel(application: Application) : AndroidViewModel(application) {
    private val standingsRepository = StandingsRepository(Firebase.firestore)
    
    // Sealed class to represent the state of the standings data
    sealed class StandingsState<out T> {
        object Loading : StandingsState<Nothing>()
        data class Success<T>(val data: T) : StandingsState<T>()
        data class Error(val message: String) : StandingsState<Nothing>()
    }
    
    // StateFlow for driver standings
    private val _driversStandings = MutableStateFlow<StandingsState<List<DriverStanding>>>(StandingsState.Loading)
    val driversStandings = _driversStandings.asStateFlow()
    
    /**
     * Loads driver standings for the specified category
     * 
     * @param category The racing category (e.g., "f1", "motogp")
     */
    fun loadDriverStandings(category: String) {
        viewModelScope.launch {
            _driversStandings.value = StandingsState.Loading
            
            try {
                val result = standingsRepository.getDriverStandings(category)
                if (result.isSuccess) {
                    val standings = result.getOrNull()
                    if (!standings.isNullOrEmpty()) {
                        Log.d("StandingsViewModel", "Loaded ${standings.size} driver standings")
                        _driversStandings.value = StandingsState.Success(standings)
                    } else {
                        _driversStandings.value = StandingsState.Error("No standings data available")
                    }
                } else {
                    _driversStandings.value = StandingsState.Error(
                        result.exceptionOrNull()?.message ?: "Failed to load standings"
                    )
                }
            } catch (e: Exception) {
                Log.e("StandingsViewModel", "Error loading driver standings", e)
                _driversStandings.value = StandingsState.Error("Error: ${e.message}")
            }
        }
    }
}