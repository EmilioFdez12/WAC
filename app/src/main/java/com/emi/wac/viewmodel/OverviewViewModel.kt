package com.emi.wac.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.emi.wac.data.model.circuit.Circuit
import com.emi.wac.data.model.contructor.Constructor
import com.emi.wac.data.model.drivers.Driver
import com.emi.wac.data.model.weather.WeatherData
import com.emi.wac.data.repository.RacingRepository
import com.emi.wac.data.repository.StandingsRepository
import com.emi.wac.data.repository.WeatherRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OverviewViewModel(application: Application) : AndroidViewModel(application) {
    private val standingsRepository = StandingsRepository(Firebase.firestore)
    private val racingRepository = RacingRepository(standingsRepository, application)
    private val weatherRepository = WeatherRepository(racingRepository)

    private val _leaderInfo =
        MutableStateFlow<DataState<Driver>>(DataState.Loading)
    val leaderInfo = _leaderInfo.asStateFlow()

    private val _constructorLeaderInfo =
        MutableStateFlow<DataState<Constructor>>(DataState.Loading)
    val constructorLeaderInfo = _constructorLeaderInfo.asStateFlow()

    private val _circuitInfo = MutableStateFlow<DataState<Circuit?>>(DataState.Loading)
    val circuitInfo = _circuitInfo.asStateFlow()

    private val _weatherInfo =
        MutableStateFlow<DataState<WeatherData>>(DataState.Loading)
    val weatherInfo = _weatherInfo.asStateFlow()

    fun loadCategoryDetails(category: String) {
        viewModelScope.launch {
            _leaderInfo.value = DataState.Loading
            _constructorLeaderInfo.value = DataState.Loading
            _circuitInfo.value = DataState.Loading
            _weatherInfo.value = DataState.Loading

            try {
                val leaderStandingResult = standingsRepository.getLeaderDriver(category)
                if (leaderStandingResult.isSuccess) {
                    val leaderStanding = leaderStandingResult.getOrNull()
                    if (leaderStanding != null) {
                        // Ya no necesitamos buscar el piloto en otra lista, toda la info está en leaderStanding
                        _leaderInfo.value = DataState.Success(leaderStanding)
                    } else {
                        _leaderInfo.value = DataState.Error("No leader standing found")
                    }
                } else {
                    _leaderInfo.value =
                        DataState.Error("Failed to load leader: ${leaderStandingResult.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                Log.e("OverviewViewModel", "Error loading leader info", e)
                _leaderInfo.value = DataState.Error("Error loading leader: ${e.message}")
            }
            
            try {
                val constructorLeaderStandingResult = standingsRepository.getLeaderConstructor(category)
                if (constructorLeaderStandingResult.isSuccess) {
                    val constructorLeaderStanding = constructorLeaderStandingResult.getOrNull()
                    if (constructorLeaderStanding != null) {
                        // Ya no necesitamos buscar el piloto en otra lista, toda la info está en leaderStanding
                        _constructorLeaderInfo.value = DataState.Success(constructorLeaderStanding)
                    } else {
                        _constructorLeaderInfo.value = DataState.Error("No leader standing found")
                    }
                } else {
                    _constructorLeaderInfo.value =
                        DataState.Error("Failed to load leader: ${constructorLeaderStandingResult.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                Log.e("OverviewViewModel", "Error loading leader info", e)
                _constructorLeaderInfo.value = DataState.Error("Error loading leader: ${e.message}")
            }

            // Circuit info
            try {
                Log.d(
                    "OverviewViewModel",
                    "Fetching next race for circuit info, category=$category"
                )
                val nextRace = racingRepository.getNextGrandPrixObject(category)
                if (nextRace != null) {
                    val circuits = racingRepository.getCircuits(category)
                    val nextCircuit = findCircuit(circuits?.circuits, nextRace.gp)
                    _circuitInfo.value = DataState.Success(nextCircuit)
                } else {
                    Log.e("OverviewViewModel", "No next race found for circuit info")
                    _circuitInfo.value = DataState.Error("No next race found")
                }
            } catch (e: Exception) {
                Log.e("OverviewViewModel", "Error loading circuit info: ${e.message}", e)
                _circuitInfo.value = DataState.Error("Error loading circuit: ${e.message}")
            }

            // Weather info
            try {
                Log.d(
                    "OverviewViewModel",
                    "Fetching next race for weather info, category=$category"
                )
                val nextRace = racingRepository.getNextGrandPrixObject(category)
                if (nextRace != null) {
                    // Race weather
                    val weatherRace = if (nextRace.sessions.race?.day?.isNotEmpty() == true &&
                        nextRace.sessions.race.time.isNotEmpty()
                    ) {
                        Log.d("OverviewViewModel", "Fetching weather for race session")
                        try {
                            val result = weatherRepository.getWeatherForSession(category, "race")
                            Log.d("OverviewViewModel", "Race weather result: $result")
                            result
                        } catch (e: Exception) {
                            Log.e(
                                "OverviewViewModel",
                                "Error loading race weather: ${e.message}",
                                e
                            )
                            null
                        }
                    } else {
                        null
                    }

                    // Qualifying weather
                    val weatherQualy =
                        if (nextRace.sessions.qualifying?.day?.isNotEmpty() == true &&
                            nextRace.sessions.qualifying.time.isNotEmpty()
                        ) {
                            Log.d("OverviewViewModel", "Fetching weather for qualifying session")
                            try {
                                val result =
                                    weatherRepository.getWeatherForSession(category, "qualifying")
                                Log.d("OverviewViewModel", "Qualifying weather result: $result")
                                result
                            } catch (e: Exception) {
                                Log.e(
                                    "OverviewViewModel",
                                    "Error loading qualifying weather: ${e.message}",
                                    e
                                )
                                null
                            }
                        } else {
                            Log.e(
                                "OverviewViewModel",
                                "Qualifying session invalid or missing: day=${nextRace.sessions.qualifying?.day}, time=${nextRace.sessions.qualifying?.time}"
                            )
                            null
                        }

                    // Sprint weather
                    val weatherSprint = if (nextRace.sessions.sprint?.day?.isNotEmpty() == true &&
                        nextRace.sessions.sprint.time.isNotEmpty()
                    ) {
                        Log.d("OverviewViewModel", "Fetching weather for sprint session")
                        try {
                            val result = weatherRepository.getWeatherForSession(category, "sprint")
                            Log.d("OverviewViewModel", "Sprint weather result: $result")
                            result
                        } catch (e: Exception) {
                            Log.e(
                                "OverviewViewModel",
                                "Error loading sprint weather: ${e.message}",
                                e
                            )
                            null
                        }
                    } else {
                        Log.e(
                            "OverviewViewModel",
                            "Sprint session invalid or missing: day=${nextRace.sessions.sprint?.day}, time=${nextRace.sessions.sprint?.time}"
                        )
                        null
                    }

                    Log.d(
                        "OverviewViewModel",
                        "Weather data compiled: race=$weatherRace, qualy=$weatherQualy, sprint=$weatherSprint"
                    )
                    _weatherInfo.value = DataState.Success(
                        WeatherData(
                            qualifying = weatherQualy,
                            race = weatherRace,
                            sprint = weatherSprint
                        )
                    )
                } else {
                    Log.e("OverviewViewModel", "No next race found for weather info")
                    _weatherInfo.value = DataState.Error("No next race found")
                }
            } catch (e: Exception) {
                Log.e("OverviewViewModel", "Error loading weather info: ${e.message}", e)
                _weatherInfo.value = DataState.Error("Error loading weather: ${e.message}")
            }
        }
    }

    private fun findCircuit(circuits: List<Circuit>?, gpName: String): Circuit? {
        return circuits?.find {
            it.gp.contains(gpName, ignoreCase = true) ||
                gpName.contains(it.gp, ignoreCase = true)
        }
    }
}