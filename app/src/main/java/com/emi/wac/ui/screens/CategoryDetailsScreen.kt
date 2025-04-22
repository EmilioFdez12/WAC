package com.emi.wac.ui.screens

import android.util.Log
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
import com.emi.wac.common.Constants.BCKG_IMG
import com.emi.wac.ui.components.category_details.OverViewComponent
import com.emi.wac.ui.components.category_details.ScheduleComponent
import com.emi.wac.ui.components.category_details.StandingsComponent
import com.emi.wac.ui.components.category_details.overview.CategoryTabs
import com.emi.wac.ui.theme.WACTheme
import com.emi.wac.viewmodel.OverviewViewModel
import com.emi.wac.viewmodel.StandingsViewModel

@Composable
fun CategoryDetailsScreen(
    modifier: Modifier = Modifier,
    category: String,
    viewModelOverview: OverviewViewModel = viewModel(),
    viewModelStanding: StandingsViewModel = viewModel()
) {
    val backgroundPainter = rememberAsyncImagePainter(model = BCKG_IMG)
    var selectedTab by remember { mutableIntStateOf(0) }
    val scrollState = rememberScrollState()

    LaunchedEffect(category) {
        viewModelOverview.loadCategoryDetails(category)
    }

    Log.d("CategoryDetailsScreen", "Category: $category")

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
                    category = category
                )
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