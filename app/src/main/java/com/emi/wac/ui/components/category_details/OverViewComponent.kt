package com.emi.wac.ui.components.category_details

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.unit.dp
import com.emi.wac.ui.components.category_details.overview.CircuitInfo
import com.emi.wac.ui.components.category_details.overview.ConstructorLeaderCard
import com.emi.wac.ui.components.category_details.overview.LeaderDriverCard
import com.emi.wac.ui.components.category_details.overview.schedule.RaceWeekendSchedule
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

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        when (val state = leaderInfo) {
            is OverviewViewModel.DataState.Success -> {
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

            is OverviewViewModel.DataState.Loading -> {
                Log.d("OverViewComponent", "LeaderInfo Loading")
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(CenterHorizontally)
                )
            }

            is OverviewViewModel.DataState.Error -> {
                Log.d("OverViewComponent", "LeaderInfo Error: ${state.message}")
                Text(
                    text = "Error: ${state.message}",
                    modifier = Modifier.padding(top = 16.dp),
                    color = Red
                )
            }
        }

        when (val state = constructorLeaderInfo) {
            is OverviewViewModel.DataState.Success -> {
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

            is OverviewViewModel.DataState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(CenterHorizontally)
                )
            }

            is OverviewViewModel.DataState.Error -> {
                Text(
                    text = "Error: ${state.message}",
                    modifier = Modifier.padding(top = 16.dp),
                    color = Red
                )
            }
        }

        RaceWeekendSchedule(
            modifier = Modifier.padding(top = 16.dp, start = 32.dp, end = 32.dp),
            category = category
        )

        when (val state = circuitInfo) {
            is OverviewViewModel.DataState.Success -> {
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

            is OverviewViewModel.DataState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(CenterHorizontally)
                )
            }

            is OverviewViewModel.DataState.Error -> {
                Text(
                    text = "Error: ${state.message}",
                    modifier = Modifier.padding(top = 16.dp),
                    color = Red
                )
            }
        }
    }
}