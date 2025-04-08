package com.emi.wac.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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

class CategoryDetailsViewModel(application: Application) : AndroidViewModel(application) {
    private val racingRepository = RacingRepository(application)
    private val standingsRepository = StandingsRepository(Firebase.firestore)

    private val _leaderInfo = MutableStateFlow<Pair<DriverStanding, Driver?>?>(null)
    val leaderInfo = _leaderInfo.asStateFlow()

    private val _constructorLeaderInfo =
        MutableStateFlow<Pair<ConstructorStanding, Constructor?>?>(null)
    val constructorLeaderInfo = _constructorLeaderInfo.asStateFlow()

    fun loadLeaderInfo(category: String) {
        viewModelScope.launch {
            try {
                // Get leader standing from Firebase
                val leaderStandingResult = standingsRepository.getLeaderDriver(category)

                if (leaderStandingResult.isSuccess) {
                    val leaderStanding = leaderStandingResult.getOrNull()
                    if (leaderStanding != null) {
                        // Get all drivers from local JSON
                        val drivers = racingRepository.getDrivers(category)

                        // Find matching driver for additional info
                        val leaderDriver = drivers?.drivers?.find {
                            it.name == leaderStanding.driver
                        }

                        _leaderInfo.value = Pair(leaderStanding, leaderDriver)
                    }
                }
            } catch (_: Exception) {
                _leaderInfo.value = null
            }
        }
    }

    fun loadConstructorLeaderInfo(category: String) {
        viewModelScope.launch {
            try {
                val leaderConstructorStandingResult =
                    standingsRepository.getLeaderConstructor(category)
    
                if (leaderConstructorStandingResult.isSuccess) {
                    val constructorLeaderStanding = leaderConstructorStandingResult.getOrNull()
                    if (constructorLeaderStanding != null) {
                        val teams = racingRepository.getConstructors(category)
    
                        val leaderTeam = teams?.constructors?.find {
                            // Usar contains para una comparación más flexible
                            it.team.contains(constructorLeaderStanding.team, ignoreCase = true) ||
                            constructorLeaderStanding.team.contains(it.team, ignoreCase = true)
                        }
    
                        _constructorLeaderInfo.value = Pair(constructorLeaderStanding, leaderTeam)
                    }
                } else {
                    Log.e(
                        "CategoryDetailsViewModel",
                        "Failed to load constructor leader: ${leaderConstructorStandingResult.exceptionOrNull()?.message}"
                    )
                }
            } catch (e: Exception) {
                Log.e("CategoryDetailsViewModel", "Error loading constructor leader", e)
                _constructorLeaderInfo.value = null
            }
        }
    }
}