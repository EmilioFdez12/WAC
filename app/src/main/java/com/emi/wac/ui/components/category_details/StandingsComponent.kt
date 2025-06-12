package com.emi.wac.ui.components.category_details

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emi.wac.ui.components.category_details.standings.ConstructorStandingsList
import com.emi.wac.ui.components.category_details.standings.DriverStandingsList
import com.emi.wac.ui.components.category_details.standings.TopThreeDrivers
import com.emi.wac.ui.theme.AlataTypography
import com.emi.wac.ui.theme.PrimaryRed
import com.emi.wac.ui.theme.getPrimaryColorForCategory
import com.emi.wac.viewmodel.StandingsViewModel

/**
 * Composable function to display the standings component.
 */
@Composable
fun StandingsComponent(
    category: String,
    viewModel: StandingsViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val driversStandingsState by viewModel.driversStandings.collectAsState()
    val constructorsStandingsState by viewModel.constructorsStandings.collectAsState()

    LaunchedEffect(category) {
        viewModel.loadAllStandings(category)
    }

    // Get screen width for adaptive sizing
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val buttonHeight = when {
        screenWidth < 360.dp -> 20.dp
        screenWidth < 400.dp -> 32.dp
        screenWidth < 600.dp -> 48.dp
        else -> 64.dp
    }

    val verticalContentPadding = when {
        screenWidth < 360.dp -> 0.dp
        screenWidth < 400.dp -> 0.dp
        screenWidth < 600.dp -> 12.dp
        else -> 20.dp
    }

    val horizontalContentPadding = when {
        screenWidth < 360.dp -> 2.dp
        screenWidth < 400.dp -> 4.dp
        screenWidth < 600.dp -> 8.dp
        else -> 12.dp
    }

    val titleMediumFont = when {
        screenWidth < 360.dp -> 10.sp
        screenWidth < 400.dp -> 14.sp
        screenWidth < 600.dp -> 16.sp
        else -> 18.sp
    }

    val tabsHorizontalPadding = when {
        screenWidth < 360.dp -> 12.dp
        screenWidth < 400.dp -> 10.dp
        screenWidth < 600.dp -> 8.dp
        else -> 4.dp
    }

    val spacer = when {
        screenWidth < 360.dp -> 10.dp
        screenWidth < 400.dp -> 6.dp
        screenWidth < 600.dp -> 0.dp
        else -> 0.dp
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // Only the content of the standings is animated with Crossfade
            Crossfade(targetState = selectedTabIndex) { tabIndex ->
                when (tabIndex) {
                    0 -> {
                        when (val state = driversStandingsState) {
                            is StandingsViewModel.StandingsState.Loading -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Loading drivers...", color = PrimaryRed)
                                }
                            }

                            is StandingsViewModel.StandingsState.Success -> {
                                val standings = state.data
                                Column(
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    if (standings.size >= 3) {
                                        TopThreeDrivers(
                                            standings = standings.take(3),
                                            category = category
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(100.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "Not enough standings: ${standings.size}",
                                                color = Color.Yellow
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(spacer))

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)
                                    ) {
                                        DriverStandingsList(
                                            standings = if (standings.size > 3) standings.drop(3) else emptyList(),
                                            category = category
                                        )
                                    }
                                }
                            }

                            is StandingsViewModel.StandingsState.Error -> {
                            }
                        }
                    }

                    1 -> {
                        when (val state = constructorsStandingsState) {
                            is StandingsViewModel.StandingsState.Loading -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Loading constructors...", color = Color.White)
                                }
                            }

                            is StandingsViewModel.StandingsState.Success -> {
                                Box(
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    ConstructorStandingsList(
                                        standings = state.data,
                                        category = category
                                    )
                                }
                            }

                            is StandingsViewModel.StandingsState.Error -> {
                            }
                        }
                    }
                }
            }
        }

        // Tabs always visible at the bottom
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = tabsHorizontalPadding)
        ) {
            listOf("DRIVERS", "CONSTRUCTORS").forEachIndexed { index, title ->
                Button(
                    onClick = { selectedTabIndex = index },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTabIndex == index) getPrimaryColorForCategory(
                            category
                        ) else Color(0xFF303030),
                        contentColor = Color.White
                    ),
                    // Rounded corners for the tabs
                    shape = when (index) {
                        0 -> RoundedCornerShape(bottomStart = 8.dp)
                        1 -> RoundedCornerShape(bottomEnd = 8.dp)
                        else -> RoundedCornerShape(0.dp)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(buttonHeight),
                    contentPadding = PaddingValues(verticalContentPadding, horizontalContentPadding)
                ) {
                    Text(
                        text = title,
                        style = AlataTypography.titleMedium.copy(fontSize = titleMediumFont)
                    )
                }
            }
        }
    }
}