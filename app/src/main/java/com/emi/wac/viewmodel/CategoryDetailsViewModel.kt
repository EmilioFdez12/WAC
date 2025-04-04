package com.emi.wac.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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
            } catch (e: Exception) {
                _leaderInfo.value = null
            }
        }
    }
}