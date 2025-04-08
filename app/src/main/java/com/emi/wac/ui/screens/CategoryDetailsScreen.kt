package com.emi.wac.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter
import com.emi.wac.ui.components.category_details.CategoryTabs
import com.emi.wac.ui.components.category_details.CircuitInfo
import com.emi.wac.ui.components.category_details.ConstructorLeaderCard
import com.emi.wac.ui.components.category_details.LeaderDriverCard
import com.emi.wac.ui.components.category_details.schedule.RaceWeekendSchedule
import com.emi.wac.ui.theme.WACTheme
import com.emi.wac.viewmodel.CategoryDetailsViewModel

@Composable
fun CategoryDetailsScreen(
    modifier: Modifier = Modifier,
    category: String,
    viewModel: CategoryDetailsViewModel = viewModel()
) {
    val backgroundPainter =
        rememberAsyncImagePainter(model = "file:///android_asset/background.webp")
    var selectedTab by remember { mutableIntStateOf(0) }
    val leaderInfo by viewModel.leaderInfo.collectAsState()
    val constructorLeaderInfo by viewModel.constructorLeaderInfo.collectAsState()
    val circuitInfo by viewModel.circuitInfo.collectAsState()

    // Create a scroll state to track scrolling
    val scrollState = rememberScrollState()

    // Specific configuration for each category
    val offsetX = if (category == "f1") 60.dp else 60.dp
    val offsetY = if (category == "f1") 0.dp else (24).dp
    val scale = if (category == "f1") 1f else 2f

    val constructorOffsetX = if (category == "f1") (-20).dp else 20.dp
    val constructorOffsetY = if (category == "f1") (20).dp else 20.dp
    val constructorScale = if (category == "f1") 1f else 1.2f
    val constructorRotation = if (category == "f1") 0f else 25f

    // Load leader info when screen is created
    LaunchedEffect(category) {
        viewModel.loadLeaderInfo(category)
        viewModel.loadConstructorLeaderInfo(category)
        viewModel.loadNextCircuitInfo(category)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = backgroundPainter,
            contentDescription = "App Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Make the entire content scrollable, including tabs
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Category tabs now inside the scrollable column
            CategoryTabs(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, end = 32.dp, top = 24.dp),
                category = category,
            )

            when (selectedTab) {
                0 -> {
                    // Content for Overview tab
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        leaderInfo?.let { (standing, driver) ->
                            LeaderDriverCard(
                                modifier = Modifier.padding(top = 16.dp),
                                driverStanding = standing,
                                driverLogo = driver?.portrait ?: "",
                                imageScale = scale,
                                offsetX = offsetX,
                                offsetY = offsetY,
                                category = category,
                            )
                        }
                        constructorLeaderInfo?.let { (standing, constructor) ->
                            ConstructorLeaderCard(
                                modifier = Modifier.padding(top = 16.dp),
                                constructorStanding = standing,
                                car = constructor?.car ?: "",
                                imageScale = constructorScale,
                                offsetX = constructorOffsetX,
                                offsetY = constructorOffsetY,
                                rotation = constructorRotation,
                                category = category,
                            )
                        }

                        // Race Weekend Schedule
                        RaceWeekendSchedule(
                            modifier = Modifier.padding(top = 16.dp, start = 32.dp, end = 32.dp),
                            category = category
                        )

                        CircuitInfo(
                            modifier = Modifier.padding(top = 16.dp, start = 32.dp, end = 32.dp, bottom = 24.dp),
                            category = category,
                            circuit = circuitInfo
                        )
                    }
                }
                // Add other tabs as needed
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryDetailsScreenPreview() {
    WACTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            CategoryDetailsScreen(category = "f1")
        }
    }
}