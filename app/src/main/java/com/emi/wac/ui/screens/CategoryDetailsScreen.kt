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
import com.emi.wac.ui.components.category_details.overview.CategoryTabs
import com.emi.wac.ui.components.category_details.OverViewComponent
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
    val scrollState = rememberScrollState()

    // Load all category details when the screen is created or category changes
    LaunchedEffect(category) {
        viewModel.loadCategoryDetails(category)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = backgroundPainter,
            contentDescription = "App Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
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
                0 -> OverViewComponent(
                    category = category,
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxWidth()
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