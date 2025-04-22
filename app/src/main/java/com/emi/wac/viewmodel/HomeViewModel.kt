package com.emi.wac.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.emi.wac.data.model.RaceInfo
import com.emi.wac.data.repository.RacingRepository
import com.emi.wac.data.repository.StandingsRepository
import com.emi.wac.data.utils.DateUtils
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
        val raceDateTime: Date
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
                delay(999)
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
            val raceInfo = repository.getNextGrandPrix(category, leaderName)
            val raceDateTime = DateUtils.parseDate(
                repository.getNextGrandPrixObject(category)?.sessions?.race?.day ?: "",
                repository.getNextGrandPrixObject(category)?.sessions?.race?.time ?: "",
                DateUtils.getCurrentYear()
            ) ?: throw IllegalStateException("No valid race date")
            raceDataFlow.value = DataState.Success(RaceData(raceInfo, raceDateTime))
        } catch (e: Exception) {
            raceDataFlow.value = DataState.Error("Error loading race data: ${e.message}")
        }
    }

    private fun updateTimeRemaining() {
        val currentDate = Calendar.getInstance()

        _f1RaceData.value.let { state ->
            if (state is DataState.Success) {
                val updatedRaceInfo = state.data.grandPrix.copy(
                    timeRemaining = DateUtils.calculateTimeRemaining(
                        state.data.raceDateTime,
                        currentDate
                    )
                )
                _f1RaceData.value = DataState.Success(state.data.copy(grandPrix = updatedRaceInfo))
            }
        }

        _motoGPRaceData.value.let { state ->
            if (state is DataState.Success) {
                val updatedRaceInfo = state.data.grandPrix.copy(
                    timeRemaining = DateUtils.calculateTimeRemaining(
                        state.data.raceDateTime,
                        currentDate
                    )
                )
                _motoGPRaceData.value =
                    DataState.Success(state.data.copy(grandPrix = updatedRaceInfo))
            }
        }
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