package com.emi.wac.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil3.compose.rememberAsyncImagePainter
import com.emi.wac.common.Constants
import com.emi.wac.data.model.sessions.GrandPrix
import com.emi.wac.data.repository.RacingRepository
import com.emi.wac.ui.components.BottomBar
import com.emi.wac.utils.TransitionsUtils
import com.emi.wac.viewmodel.DataState
import com.emi.wac.viewmodel.OverviewViewModel
import com.emi.wac.viewmodel.StandingsViewModel

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val backgroundPainter = rememberAsyncImagePainter(model = Constants.BCKG_IMG)

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
                BottomBar()
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Constants.HOME,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(
                    route = Constants.HOME,
                    enterTransition = { TransitionsUtils.enterTransition() },
                    exitTransition = { TransitionsUtils.exitTransition() },
                    popEnterTransition = { TransitionsUtils.popEnterTransition() },
                    popExitTransition = { TransitionsUtils.popExitTransition() }
                ) {
                    HomeScreen(navController = navController)
                }
                composable(
                    route = "${Constants.CAT_DETAILS}/{category}",
                    arguments = listOf(navArgument("category") { type = NavType.StringType })
                ) { backStackEntry ->
                    val category = backStackEntry.arguments?.getString("category") ?: ""
                    val overviewViewModel: OverviewViewModel = viewModel()
                    val standingsViewModel: StandingsViewModel = viewModel()
                    val context = LocalContext.current
                    val racingRepository = RacingRepository(context)

                    // Estados de los datos
                    val circuitInfo by overviewViewModel.circuitInfo.collectAsState()
                    val leaderInfo by overviewViewModel.leaderInfo.collectAsState()
                    val constructorLeaderInfo by overviewViewModel.constructorLeaderInfo.collectAsState()
                    val weatherInfo by overviewViewModel.weatherInfo.collectAsState()
                    val standingsState by standingsViewModel.driversStandings.collectAsState()
                    var schedule: List<GrandPrix>? by remember { mutableStateOf(null) }

                    // Cargar datos para la categoría seleccionada
                    LaunchedEffect(category) {
                        overviewViewModel.loadCategoryDetails(category)
                        standingsViewModel.loadDriverStandings(category)
                        schedule = racingRepository.getSchedule(category)?.schedule
                    }

                    // Verificar si todos los datos están listos
                    val isDataReady = circuitInfo is DataState.Success &&
                        leaderInfo is DataState.Success &&
                        constructorLeaderInfo is DataState.Success &&
                        weatherInfo is DataState.Success &&
                        standingsState is StandingsViewModel.StandingsState.Success &&
                        schedule != null

                    // Animaciones para LoadingScreen y CategoryDetailsScreen
                    AnimatedVisibility(
                        visible = !isDataReady,
                        enter = fadeIn(animationSpec = tween(300)),
                        exit = fadeOut(animationSpec = tween(300))
                    ) {
                        LoadingScreen()
                    }
                    AnimatedVisibility(
                        visible = isDataReady,
                        enter = fadeIn(animationSpec = tween(300)),
                        exit = fadeOut(animationSpec = tween(300))
                    ) {
                        CategoryDetailsScreen(
                            category = category,
                            viewModelOverview = overviewViewModel,
                            viewModelStanding = standingsViewModel,
                            racingRepository = racingRepository,
                            schedule = schedule
                        )
                    }
                }
            }
        }
    }
}