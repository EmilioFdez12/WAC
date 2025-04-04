package com.emi.wac.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.emi.wac.ui.components.CategoryTabs
import com.emi.wac.ui.theme.WACTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.emi.wac.ui.components.LeaderDriverCard
import com.emi.wac.viewmodel.CategoryDetailsViewModel

@Composable
fun CategoryDetailsScreen(
    modifier: Modifier = Modifier,
    category: String,
    viewModel: CategoryDetailsViewModel = viewModel()
) {
    val backgroundPainter = rememberAsyncImagePainter(model = "file:///android_asset/background.webp")
    var selectedTab by remember { mutableIntStateOf(0) }
    val leaderInfo by viewModel.leaderInfo.collectAsState()

    // Configuración específica para cada categoría
    val offsetX = if (category == "f1") 60.dp else 60.dp
    val offsetY = if (category == "f1") 0.dp else (24).dp
    val scale = if (category == "f1") 1f else 2f

    // Load leader info when screen is created
    LaunchedEffect(category) {
        viewModel.loadLeaderInfo(category)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = backgroundPainter,
            contentDescription = "App Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.fillMaxSize()) {
            CategoryTabs(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, end = 32.dp, top = 64.dp)
            )

            when (selectedTab) {
                0 -> {
                    leaderInfo?.let { (standing, driver) ->
                        LeaderDriverCard(
                            modifier = Modifier.padding(top = 16.dp),
                            driverStanding = standing,
                            driverLogo = driver?.portrait ?: "",
                            imageScale = scale,
                            offsetX = offsetX,
                            offsetY = offsetY
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryDetailsPreview() {
    WACTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            CategoryDetailsScreen(category = "motogp")
        }
    }
}