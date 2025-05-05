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
import com.emi.wac.data.model.weather.WeatherData
import com.emi.wac.data.repository.RacingRepository
import com.emi.wac.data.repository.StandingsRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OverviewViewModel(application: Application) : AndroidViewModel(application) {
    private val racingRepository = RacingRepository(application)
    private val standingsRepository = StandingsRepository(Firebase.firestore)

    private val _leaderInfo =
        MutableStateFlow<DataState<Pair<DriverStanding, Driver?>>>(DataState.Loading)
    val leaderInfo = _leaderInfo.asStateFlow()

    private val _constructorLeaderInfo =
        MutableStateFlow<DataState<Pair<ConstructorStanding, Constructor?>>>(DataState.Loading)
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
                        val drivers = racingRepository.getDrivers(category)
                        val leaderDriver = findDriver(drivers?.drivers, leaderStanding.driver)
                        _leaderInfo.value = DataState.Success(Pair(leaderStanding, leaderDriver))
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
                val constructorStandingResult = standingsRepository.getLeaderConstructor(category)
                if (constructorStandingResult.isSuccess) {
                    val constructorStanding = constructorStandingResult.getOrNull()
                    if (constructorStanding != null) {
                        val constructors = racingRepository.getConstructors(category)
                        val leaderConstructor =
                            findConstructor(constructors?.constructors, constructorStanding.team)
                        _constructorLeaderInfo.value =
                            DataState.Success(Pair(constructorStanding, leaderConstructor))
                    } else {
                        _constructorLeaderInfo.value =
                            DataState.Error("No constructor standing found")
                    }
                } else {
                    _constructorLeaderInfo.value =
                        DataState.Error("Failed to load constructor: ${constructorStandingResult.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                Log.e("OverviewViewModel", "Error loading constructor leader", e)
                _constructorLeaderInfo.value =
                    DataState.Error("Error loading constructor: ${e.message}")
            }

            // Cargar información del circuito y clima en bloques separados para evitar que un error en uno afecte al otro
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
                Log.e("OverviewViewModel", "Error loading circuit info", e)
                _circuitInfo.value = DataState.Error("Error loading circuit: ${e.message}")
            }
            
            // Cargar información del clima en un bloque separado
            try {
                val nextRace = racingRepository.getNextGrandPrixObject(category)
                if (nextRace != null) {
                    // Verificar primero si las sesiones existen en el objeto nextRace antes de intentar cargar el clima
                    
                    // Cargar clima para la carrera (siempre existe según el modelo)
                    val weatherRace = if (nextRace.sessions.race.day.isNotEmpty() && 
                                         nextRace.sessions.race.time.isNotEmpty()) {
                        try {
                            racingRepository.getWeatherForSession(category, "race")
                        } catch (e: Exception) {
                            Log.e("OverviewViewModel", "Error loading race weather", e)
                            null
                        }
                    } else {
                        Log.d("OverviewViewModel", "Race session does not exist for this weekend")
                        null
                    }
                    
                    // Cargar clima para la clasificación solo si existe la sesión
                    val weatherQualy = if (nextRace.sessions.qualifying?.day?.isNotEmpty() == true && 
                                          nextRace.sessions.qualifying.time.isNotEmpty() == true) {
                        try {
                            racingRepository.getWeatherForSession(category, "qualifying")
                        } catch (e: Exception) {
                            Log.e("OverviewViewModel", "Error loading qualifying weather", e)
                            null
                        }
                    } else {
                        Log.d("OverviewViewModel", "Qualifying session does not exist for this weekend")
                        null
                    }
                    
                    // Cargar clima para el sprint solo si existe la sesión
                    val weatherSprint = if (nextRace.sessions.sprint?.day?.isNotEmpty() == true && 
                                           nextRace.sessions.sprint.time.isNotEmpty() == true) {
                        try {
                            racingRepository.getWeatherForSession(category, "sprint")
                        } catch (e: Exception) {
                            Log.e("OverviewViewModel", "Error loading sprint weather", e)
                            null
                        }
                    } else {
                        Log.d("OverviewViewModel", "Sprint session does not exist for this weekend")
                        null
                    }

                    _weatherInfo.value = DataState.Success(
                        WeatherData(
                            qualifying = weatherQualy,
                            race = weatherRace,
                            sprint = weatherSprint
                        )
                    )
                } else {
                    _weatherInfo.value = DataState.Error("No next race found")
                }
            } catch (e: Exception) {
                Log.e("OverviewViewModel", "Error loading weather info", e)
                _weatherInfo.value = DataState.Error("Error loading weather: ${e.message}")
            }
        }
    }

    private fun findDriver(drivers: List<Driver>?, driverName: String): Driver? {
        return drivers?.find { it.name == driverName }
    }

    private fun findConstructor(constructors: List<Constructor>?, teamName: String): Constructor? {
        return constructors?.find {
            it.team.contains(teamName, ignoreCase = true) || teamName.contains(
                it.team,
                ignoreCase = true
            )
        }
    }

    private fun findCircuit(circuits: List<Circuit>?, gpName: String): Circuit? {
        return circuits?.find { it.gp == gpName }
    }
}