package com.emi.wac.ui.components.category_details

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.unit.dp
import com.emi.wac.ui.components.category_details.overview.CircuitInfo
import com.emi.wac.ui.components.category_details.overview.ConstructorLeaderCard
import com.emi.wac.ui.components.category_details.overview.LeaderDriverCard
import com.emi.wac.ui.components.category_details.overview.schedule.RaceWeekendSchedule
import com.emi.wac.ui.components.category_details.weather.WeatherRow
import com.emi.wac.viewmodel.DataState
import com.emi.wac.viewmodel.OverviewViewModel

@Composable
fun OverViewComponent(
    category: String,
    viewModel: OverviewViewModel,
    modifier: Modifier = Modifier
) {
    val leaderInfo by viewModel.leaderInfo.collectAsState()
    val constructorLeaderInfo by viewModel.constructorLeaderInfo.collectAsState()
    val circuitInfo by viewModel.circuitInfo.collectAsState()
    val weatherInfo by viewModel.weatherInfo.collectAsState()

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        when (val state = leaderInfo) {
            is DataState.Success -> {
                val (standing, driver) = state.data
                Log.d(
                    "OverViewComponent",
                    "Rendering LeaderDriverCard: standing=$standing, driver=$driver"
                )
                LeaderDriverCard(
                    modifier = Modifier.padding(top = 16.dp),
                    driverStanding = standing,
                    driverLogo = driver?.portrait ?: "",
                    imageScale = if (category == "f1") 1f else 2f,
                    offsetX = if (category == "f1") 60.dp else 60.dp,
                    offsetY = if (category == "f1") 0.dp else 24.dp,
                    category = category
                )
            }
            is DataState.Error -> {
                Log.d("OverViewComponent", "LeaderInfo Error: ${state.message}")
                Text(
                    text = "Error: ${state.message}",
                    modifier = Modifier.padding(top = 16.dp),
                    color = Red
                )
            }
            is DataState.Loading -> {
                Log.d("OverViewComponent", "LeaderInfo Loading")
                // No renderizar nada en Loading
            }
        }

        when (val state = constructorLeaderInfo) {
            is DataState.Success -> {
                val (standing, constructor) = state.data
                ConstructorLeaderCard(
                    modifier = Modifier.padding(top = 16.dp),
                    constructorStanding = standing,
                    car = constructor?.car ?: "",
                    imageScale = if (category == "f1") 1f else 1.2f,
                    offsetX = if (category == "f1") (-20).dp else 20.dp,
                    offsetY = if (category == "f1") 20.dp else 20.dp,
                    rotation = if (category == "f1") 0f else 25f,
                    category = category
                )
            }
            is DataState.Error -> {
                Text(
                    text = "Error: ${state.message}",
                    modifier = Modifier.padding(top = 16.dp),
                    color = Red
                )
            }
            is DataState.Loading -> {
                // No renderizar nada en Loading
            }
        }

        RaceWeekendSchedule(
            modifier = Modifier.padding(top = 16.dp, start = 32.dp, end = 32.dp),
            category = category
        )

        when (val state = weatherInfo) {
            is DataState.Success -> {
                val weatherData = state.data
                Column(
                    modifier = Modifier
                        .padding(top = 8.dp, start = 32.dp, end = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Solo mostrar las sesiones que tienen datos de clima disponibles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        weatherData.race?.let { (temperature, weatherCode) ->
                            WeatherRow(
                                sessionName = "RACE",
                                temperature = temperature,
                                weatherCode = weatherCode,
                                category = category,
                                modifier = Modifier.width(160.dp)
                            )
                        } ?: Text(
                            text = "Carrera: No hay datos disponibles",
                            modifier = Modifier
                                .width(120.dp)
                                .padding(vertical = 4.dp),
                            color = Red
                        )

                        weatherData.qualifying?.let { (temperature, weatherCode) ->
                            WeatherRow(
                                sessionName = "QUALY",
                                temperature = temperature,
                                weatherCode = weatherCode,
                                category = category,
                                modifier = Modifier.width(160.dp)
                            )
                        } ?: Text(
                            text = "Clasificación: No hay datos disponibles",
                            modifier = Modifier
                                .width(120.dp)
                                .padding(vertical = 4.dp),
                            color = Red
                        )
                    }

                    // Solo mostrar el sprint si existe para este fin de semana
                    weatherData.sprint?.let { (temperature, weatherCode) ->
                        WeatherRow(
                            sessionName = "SPRINT",
                            temperature = temperature,
                            weatherCode = weatherCode,
                            category = category,
                            modifier = Modifier
                                .width(160.dp)
                                .padding(top = 8.dp)
                        )
                    }
                }
            }
            is DataState.Error -> {
                // No mostrar nada en caso de error
            }
            is DataState.Loading -> {
                // No renderizar nada en Loading
            }
        }

        when (val state = circuitInfo) {
            is DataState.Success -> {
                CircuitInfo(
                    modifier = Modifier.padding(
                        top = 16.dp,
                        start = 32.dp,
                        end = 32.dp,
                        bottom = 24.dp
                    ),
                    category = category,
                    circuit = state.data
                )
            }
            is DataState.Error -> {
                Text(
                    text = "Error: ${state.message}",
                    modifier = Modifier.padding(top = 16.dp),
                    color = Red
                )
            }
            is DataState.Loading -> {
                // No renderizar nada en Loading
            }
        }
    }
}