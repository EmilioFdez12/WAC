package com.emi.wac.ui.components.category_details

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
import com.emi.wac.viewmodel.CategoryDetailsViewModel

@Composable
fun OverViewComponent(
    category: String,
    viewModel: CategoryDetailsViewModel,
    modifier: Modifier = Modifier
) {
    val leaderInfo by viewModel.leaderInfo.collectAsState()
    val constructorLeaderInfo by viewModel.constructorLeaderInfo.collectAsState()
    val circuitInfo by viewModel.circuitInfo.collectAsState()

    // Configuration specific to each category
    val offsetX = if (category == "f1") 60.dp else 60.dp
    val offsetY = if (category == "f1") 0.dp else 24.dp
    val scale = if (category == "f1") 1f else 2f

    val constructorOffsetX = if (category == "f1") (-20).dp else 20.dp
    val constructorOffsetY = if (category == "f1") 20.dp else 20.dp
    val constructorScale = if (category == "f1") 1f else 1.2f
    val constructorRotation = if (category == "f1") 0f else 25f

    Column(modifier = modifier.fillMaxWidth()) {
        // Handle leader info state
        when (val state = leaderInfo) {
            is CategoryDetailsViewModel.DataState.Success -> {
                val (standing, driver) = state.data
                LeaderDriverCard(
                    modifier = Modifier.padding(top = 16.dp),
                    driverStanding = standing,
                    driverLogo = driver?.portrait ?: "",
                    imageScale = scale,
                    offsetX = offsetX,
                    offsetY = offsetY,
                    category = category
                )
            }
            is CategoryDetailsViewModel.DataState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(CenterHorizontally)
                )
            }
            is CategoryDetailsViewModel.DataState.Error -> {
                Text(
                    text = "Error: ${state.message}",
                    modifier = Modifier.padding(top = 16.dp),
                    color = Red
                )
            }
        }

        // Handle constructor leader info state
        when (val state = constructorLeaderInfo) {
            is CategoryDetailsViewModel.DataState.Success -> {
                val (standing, constructor) = state.data
                ConstructorLeaderCard(
                    modifier = Modifier.padding(top = 16.dp),
                    constructorStanding = standing,
                    car = constructor?.car ?: "",
                    imageScale = constructorScale,
                    offsetX = constructorOffsetX,
                    offsetY = constructorOffsetY,
                    rotation = constructorRotation,
                    category = category
                )
            }
            is CategoryDetailsViewModel.DataState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(CenterHorizontally)
                )
            }
            is CategoryDetailsViewModel.DataState.Error -> {
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

        // Handle circuit info state
        when (val state = circuitInfo) {
            is CategoryDetailsViewModel.DataState.Success -> {
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
            is CategoryDetailsViewModel.DataState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(CenterHorizontally)
                )
            }
            is CategoryDetailsViewModel.DataState.Error -> {
                Text(
                    text = "Error: ${state.message}",
                    modifier = Modifier.padding(top = 16.dp),
                    color = Red
                )
            }
        }
    }
}