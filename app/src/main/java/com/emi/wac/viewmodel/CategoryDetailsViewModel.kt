package com.emi.wac.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.emi.wac.data.model.circuit.Circuit
import com.emi.wac.data.model.contructor.Constructor
import com.emi.wac.data.model.contructor.ConstructorStanding
import com.emi.wac.data.model.drivers.Driver
import com.emi.wac.data.model.drivers.DriverStanding
import com.emi.wac.data.repository.RacingRepository
import com.emi.wac.data.repository.StandingsRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the category details screen, managing leader, constructor, and circuit data.
 */
class CategoryDetailsViewModel(application: Application) : AndroidViewModel(application) {
    private val racingRepository = RacingRepository(application)
    private val standingsRepository = StandingsRepository(Firebase.firestore)

    // Sealed class to represent the state of the data
    sealed class DataState<out T> {
        object Loading : DataState<Nothing>()
        data class Success<T>(val data: T) : DataState<T>()
        data class Error(val message: String) : DataState<Nothing>()
    }

    // State flows for leader, constructor, and circuit info
    private val _leaderInfo = MutableStateFlow<DataState<Pair<DriverStanding, Driver?>>>(DataState.Loading)
    val leaderInfo = _leaderInfo.asStateFlow()
    private val _constructorLeaderInfo = MutableStateFlow<DataState<Pair<ConstructorStanding, Constructor?>>>(DataState.Loading)
    val constructorLeaderInfo = _constructorLeaderInfo.asStateFlow()
    private val _circuitInfo = MutableStateFlow<DataState<Circuit?>>(DataState.Loading)
    val circuitInfo = _circuitInfo.asStateFlow()

    /**
     * Loads all category details (leader, constructor, and circuit) for the given category.
     */
    fun loadCategoryDetails(category: String) {
        viewModelScope.launch {
            // Set initial loading state
            _leaderInfo.value = DataState.Loading
            _constructorLeaderInfo.value = DataState.Loading
            _circuitInfo.value = DataState.Loading

            // Load leader info
            try {
                val leaderStandingResult = standingsRepository.getLeaderDriver(category)
                if (leaderStandingResult.isSuccess) {
                    val leaderStanding = leaderStandingResult.getOrNull()
                    if (leaderStanding != null) {
                        val drivers = racingRepository.getDrivers(category)
                        val leaderDriver = findDriver(drivers?.drivers, leaderStanding.driver)
                        _leaderInfo.value = DataState.Success(Pair(leaderStanding, leaderDriver))
                    } else {
                        _leaderInfo.value = DataState.Error("No leader standing found")
                    }
                } else {
                    _leaderInfo.value = DataState.Error("Failed to load leader: ${leaderStandingResult.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                Log.e("CategoryDetailsViewModel", "Error loading leader info", e)
                _leaderInfo.value = DataState.Error("Error loading leader: ${e.message}")
            }

            // Load constructor leader info
            try {
                val constructorStandingResult = standingsRepository.getLeaderConstructor(category)
                if (constructorStandingResult.isSuccess) {
                    val constructorStanding = constructorStandingResult.getOrNull()
                    if (constructorStanding != null) {
                        val constructors = racingRepository.getConstructors(category)
                        val leaderConstructor = findConstructor(constructors?.constructors, constructorStanding.team)
                        _constructorLeaderInfo.value = DataState.Success(Pair(constructorStanding, leaderConstructor))
                    } else {
                        _constructorLeaderInfo.value = DataState.Error("No constructor standing found")
                    }
                } else {
                    _constructorLeaderInfo.value = DataState.Error("Failed to load constructor: ${constructorStandingResult.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                Log.e("CategoryDetailsViewModel", "Error loading constructor leader", e)
                _constructorLeaderInfo.value = DataState.Error("Error loading constructor: ${e.message}")
            }

            // Load next circuit info
            try {
                val nextRace = racingRepository.getNextGrandPrixObject(category)
                if (nextRace != null) {
                    val circuits = racingRepository.getCircuits(category)
                    val nextCircuit = findCircuit(circuits?.circuits, nextRace.gp)
                    _circuitInfo.value = DataState.Success(nextCircuit)
                } else {
                    _circuitInfo.value = DataState.Error("No next race found")
                }
            } catch (e: Exception) {
                Log.e("CategoryDetailsViewModel", "Error loading circuit info", e)
                _circuitInfo.value = DataState.Error("Error loading circuit: ${e.message}")
            }
        }
    }

    // Finds a driver by name in the list
    private fun findDriver(drivers: List<Driver>?, driverName: String): Driver? {
        return drivers?.find { it.name == driverName }
    }

    // Finds a constructor by team name in the list
    private fun findConstructor(constructors: List<Constructor>?, teamName: String): Constructor? {
        return constructors?.find {
            it.team.contains(teamName, ignoreCase = true) || teamName.contains(it.team, ignoreCase = true)
        }
    }

    // Fids a circuit by Grand Prix name in the list
    private fun findCircuit(circuits: List<Circuit>?, gpName: String): Circuit? {
        return circuits?.find { it.gp == gpName }
    }
}