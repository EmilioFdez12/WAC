package com.emi.wac.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil3.compose.rememberAsyncImagePainter
import com.emi.wac.common.Constants.CAT_DETAILS
import com.emi.wac.common.Constants.HOME
import com.emi.wac.ui.components.BottomBar

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val backgroundPainter =
        rememberAsyncImagePainter(model = "file:///android_asset/background.webp")

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = backgroundPainter,
            contentDescription = "App Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            bottomBar = {
                // Always show the bottom bar without animation
                BottomBar()
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = HOME,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(HOME) {
                    HomeScreen(navController = navController)
                }
                composable(
                    route = "$CAT_DETAILS/{category}",
                    arguments = listOf(navArgument("category") { type = NavType.StringType })
                ) { backStackEntry ->
                    CategoryDetailsScreen(
                        category = backStackEntry.arguments?.getString("category") ?: ""
                    )
                }
            }
        }
    }
}