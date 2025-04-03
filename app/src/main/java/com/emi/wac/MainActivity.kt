package com.emi.wac

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import coil3.compose.rememberAsyncImagePainter
import com.emi.wac.ui.components.BottomBar
import com.emi.wac.ui.screens.CategoryDetailsScreen
import com.emi.wac.ui.screens.HomeScreen
import com.emi.wac.ui.theme.WACTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WACTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val backgroundPainter = rememberAsyncImagePainter(model = "file:///android_asset/background.webp")

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
            bottomBar = { BottomBar() }
        ) { innerPadding ->
            NavHost(
                navController = rememberNavController(),
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("home") {
                    HomeScreen(navController = rememberNavController())
                }
                composable(
                    route = "caregory_details/{category}",
                    arguments = listOf(navArgument("category") { type = NavType.StringType })
                ) { backStackEntry ->
                    CategoryDetailsScreen(category = backStackEntry.arguments?.getString("category") ?: "")
                }
            }
        }
    }
}