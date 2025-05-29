package com.emi.wac.ui.screens.app

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.emi.wac.common.Constants
import com.emi.wac.data.model.sessions.GrandPrix
import com.emi.wac.data.repository.RacingRepository
import com.emi.wac.ui.components.category_details.OverViewComponent
import com.emi.wac.ui.components.category_details.ScheduleComponent
import com.emi.wac.ui.components.category_details.StandingsComponent
import com.emi.wac.ui.components.category_details.overview.CategoryTabs
import com.emi.wac.viewmodel.OverviewViewModel
import com.emi.wac.viewmodel.StandingsViewModel

/**
 * Composable function to display the details of a category
 * Screen that includes tabs for overview, standings, and schedule
 */
@Composable
fun CategoryDetailsScreen(
    modifier: Modifier = Modifier,
    category: String,
    viewModelOverview: OverviewViewModel,
    viewModelStanding: StandingsViewModel,
    racingRepository: RacingRepository,
    schedule: List<GrandPrix>?
) {
    val backgroundPainter = rememberAsyncImagePainter(model = Constants.BCKG_IMG)
    var selectedTab by remember { mutableIntStateOf(0) }
    val scrollState = rememberScrollState()

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = backgroundPainter,
            contentDescription = "App Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            CategoryTabs(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, end = 32.dp, top = 24.dp),
                category = category
            )

            Crossfade(
                targetState = selectedTab,
                animationSpec = tween(500),
                modifier = Modifier.fillMaxSize()
            ) { selectedTab ->
                when (selectedTab) {
                    0 -> Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    ) {
                        OverViewComponent(
                            category = category,
                            viewModel = viewModelOverview,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    1 -> StandingsComponent(
                        category = category,
                        viewModel = viewModelStanding,
                        modifier = Modifier.fillMaxWidth()
                    )

                    2 -> ScheduleComponent(
                        category = category,
                        racingRepository = racingRepository,
                        schedule = schedule,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}