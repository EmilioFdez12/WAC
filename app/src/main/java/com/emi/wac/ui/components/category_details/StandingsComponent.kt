package com.emi.wac.ui.components.category_details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.emi.wac.ui.components.category_details.standings.DriverStandingsList
import com.emi.wac.ui.components.category_details.standings.TopThreeDrivers
import com.emi.wac.ui.theme.AlataTypography
import com.emi.wac.ui.theme.getPrimaryColorForCategory
import com.emi.wac.viewmodel.StandingsViewModel

@Composable
fun StandingsComponent(
    category: String,
    viewModel: StandingsViewModel,
    modifier: Modifier = Modifier
) {
    val standingsState by viewModel.driversStandings.collectAsState()
    val primaryColor = getPrimaryColorForCategory(category)

    LaunchedEffect(category) {
        viewModel.loadDriverStandings(category)
    }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        when (val state = standingsState) {
            is StandingsViewModel.StandingsState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = primaryColor)
                }
            }
            
            is StandingsViewModel.StandingsState.Success -> {
                val standings = state.data
                
                // Display top 3 drivers in a special way
                if (standings.size >= 3) {
                    TopThreeDrivers(
                        standings = standings.take(3),
                        category = category
                    )
                }
                
                // Display the rest of the drivers in a list
                DriverStandingsList(
                    standings = if (standings.size > 3) standings.drop(3) else emptyList(),
                    category = category
                )
            }
            
            is StandingsViewModel.StandingsState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: ${state.message}",
                        color = Color.Red,
                        style = AlataTypography.bodyLarge
                    )
                }
            }
        }
    }
}


