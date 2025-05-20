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
import androidx.compose.runtime.mutableIntStateOf
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
import com.emi.wac.data.repository.AuthRepository
import com.emi.wac.data.repository.RacingRepository
import com.emi.wac.data.repository.StandingsRepository
import com.emi.wac.ui.components.BottomBar
import com.emi.wac.ui.screens.app.CategoryDetailsScreen
import com.emi.wac.ui.screens.app.HomeScreen
import com.emi.wac.ui.screens.app.NewsScreen
import com.emi.wac.ui.screens.app.ProfileScreen
import com.emi.wac.utils.TransitionsUtils
import com.emi.wac.viewmodel.DataState
import com.emi.wac.viewmodel.OverviewViewModel
import com.emi.wac.viewmodel.StandingsViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@Composable
fun MainScreen(
    authRepository: AuthRepository,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val backgroundPainter = rememberAsyncImagePainter(model = Constants.BCKG_IMG)
    var selectedTab by remember { mutableIntStateOf(0) }

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
                BottomBar(
                    selectedItem = selectedTab,
                    onItemSelected = { index ->
                        selectedTab = index
                        when (index) {
                            0 -> navController.navigate(Constants.HOME) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }

                            1 -> navController.navigate("news") {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }

                            2 -> navController.navigate("profile") {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    }
                )
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
                    selectedTab = 0
                    HomeScreen(navController = navController)
                }

                composable(
                    route = "news",
                    enterTransition = { TransitionsUtils.enterTransition() },
                    exitTransition = { TransitionsUtils.exitTransition() },
                    popEnterTransition = { TransitionsUtils.popEnterTransition() },
                    popExitTransition = { TransitionsUtils.popExitTransition() }
                ) {
                    selectedTab = 1
                    NewsScreen()
                }

                composable(
                    route = "profile",
                    enterTransition = { TransitionsUtils.enterTransition() },
                    exitTransition = { TransitionsUtils.exitTransition() },
                    popEnterTransition = { TransitionsUtils.popEnterTransition() },
                    popExitTransition = { TransitionsUtils.popExitTransition() }
                ) {
                    selectedTab = 2
                    ProfileScreen(authRepository = authRepository, onLogout = onLogout)
                }

                composable(
                    route = "${Constants.CAT_DETAILS}/{category}",
                    arguments = listOf(navArgument("category") { type = NavType.StringType })
                ) { backStackEntry ->
                    val category = backStackEntry.arguments?.getString("category") ?: ""
                    val overviewViewModel: OverviewViewModel = viewModel()
                    val standingsViewModel: StandingsViewModel = viewModel()
                    val context = LocalContext.current
                    val db = Firebase.firestore
                    val standingRepository = StandingsRepository(db)
                    val racingRepository = RacingRepository(standingRepository, context)

                    val circuitInfo by overviewViewModel.circuitInfo.collectAsState()
                    val leaderInfo by overviewViewModel.leaderInfo.collectAsState()
                    val constructorLeaderInfo by overviewViewModel.constructorLeaderInfo.collectAsState()
                    val weatherInfo by overviewViewModel.weatherInfo.collectAsState()
                    val standingsState by standingsViewModel.driversStandings.collectAsState()
                    var schedule: List<GrandPrix>? by remember { mutableStateOf(null) }

                    LaunchedEffect(category) {
                        overviewViewModel.loadCategoryDetails(category)
                        standingsViewModel.loadDriverStandings(category)
                        schedule = racingRepository.getSchedule(category)?.schedule
                    }

                    val isDataReady = circuitInfo is DataState.Success &&
                        leaderInfo is DataState.Success &&
                        constructorLeaderInfo is DataState.Success &&
                        weatherInfo is DataState.Success &&
                        standingsState is StandingsViewModel.StandingsState.Success &&
                        schedule != null


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