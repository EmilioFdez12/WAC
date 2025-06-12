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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.emi.wac.ui.components.category_details.overview.CircuitInfo
import com.emi.wac.ui.components.category_details.overview.leader.ConstructorLeaderCard
import com.emi.wac.ui.components.category_details.overview.leader.LeaderDriverCard
import com.emi.wac.ui.components.category_details.overview.schedule.RaceWeekendSchedule
import com.emi.wac.ui.components.category_details.weather.WeatherRow
import com.emi.wac.viewmodel.DataState
import com.emi.wac.viewmodel.OverviewViewModel

/**
 * Composable function to display the overview component in the standings.
 */
@Composable
fun OverViewComponent(
    category: String,
    viewModel: OverviewViewModel,
    modifier: Modifier = Modifier
) {
    // Collects the data from the view models
    val leaderInfo by viewModel.leaderInfo.collectAsState()
    val constructorLeaderInfo by viewModel.constructorLeaderInfo.collectAsState()
    val circuitInfo by viewModel.circuitInfo.collectAsState()
    val weatherInfo by viewModel.weatherInfo.collectAsState()
    val imageScale = if (category == "indycar") 1.5f else if (category == "motogp") 2f else 1f
    // Get screen width for adaptive font sizing
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val weatherWidth = when {
        screenWidth < 360.dp -> 120.dp
        screenWidth < 400.dp -> 140.dp
        screenWidth < 600.dp -> 160.dp
        else -> 180.dp
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        when (val state = leaderInfo) {
            is DataState.Success -> {
                val driverStanding = state.data
                LeaderDriverCard(
                    modifier = Modifier.padding(top = 16.dp),
                    driver = driverStanding,
                    imageScale = imageScale,
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
            }
        }

        when (val state = constructorLeaderInfo) {
            is DataState.Success -> {
                val constructorStanding = state.data
                ConstructorLeaderCard(
                    modifier = Modifier.padding(top = 16.dp),
                    constructor = constructorStanding,
                    car = constructorStanding.car,
                    imageScale = if (category == "f1") 1f else 1.2f,
                    offsetX = if (category == "f1") (-10).dp else 20.dp,
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
                                modifier = Modifier.width(weatherWidth)
                            )
                        } ?: ""

                        weatherData.qualifying?.let { (temperature, weatherCode) ->
                            WeatherRow(
                                sessionName = "QUALY",
                                temperature = temperature,
                                weatherCode = weatherCode,
                                category = category,
                                modifier = Modifier.width(weatherWidth)
                            )
                        } ?: ""
                    }

                    // Only show the sprint if it exists for this weekend
                    weatherData.sprint?.let { (temperature, weatherCode) ->
                        WeatherRow(
                            sessionName = "SPRINT",
                            temperature = temperature,
                            weatherCode = weatherCode,
                            category = category,
                            modifier = Modifier
                                .width(weatherWidth)
                                .padding(top = 8.dp)
                        )
                    }
                }
            }
            is DataState.Error -> {
            }
            is DataState.Loading -> {
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
            }
            is DataState.Loading -> {
            }
        }
    }
}