package com.emi.wac

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.emi.wac.common.Constants.LOGIN_ROUTE
import com.emi.wac.common.Constants.REGISTER_ROUTE
import com.emi.wac.data.repository.AuthRepository
import com.emi.wac.ui.screens.LoginScreen
import com.emi.wac.ui.screens.MainScreen
import com.emi.wac.ui.screens.RegisterScreen
import com.emi.wac.ui.theme.WACTheme
import com.emi.wac.utils.TransitionsUtils.enterTransition
import com.emi.wac.utils.TransitionsUtils.exitTransition
import com.emi.wac.viewmodel.DataState
import com.emi.wac.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val homeViewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var keepSplashScreen = true

        // Only observe data if user is signed in
        if (authRepository.getCurrentUser() != null) {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    combine(
                        homeViewModel.nextF1Race,
                        homeViewModel.nextMotoGPRace
                    ) { f1State, motoState ->
                        f1State is DataState.Success && motoState is DataState.Success
                    }.collect { isDataReady ->
                        keepSplashScreen = !isDataReady
                    }
                }
            }
        } else {
            // No data to load for LoginScreen
            keepSplashScreen = false
        }

        splashScreen.setKeepOnScreenCondition { keepSplashScreen }

        setContent {
            WACTheme {
                // Check if user is logged in
                if (authRepository.getCurrentUser() == null) {
                    // Use NavController to navigate between login and register
                    val navController = rememberNavController()


                    NavHost(navController = navController, startDestination = LOGIN_ROUTE) {
                        composable(
                            route = LOGIN_ROUTE,
                            enterTransition = { enterTransition() },
                            exitTransition = { exitTransition() },
                        ) {
                            LoginScreen(
                                authRepository = authRepository,
                                onLoginSuccess = { recreate() },
                                onNavigateToRegister = { navController.navigate("register") }
                            )
                        }
                        composable(
                            route = REGISTER_ROUTE,
                            enterTransition = { enterTransition() },
                            exitTransition = { exitTransition() },
                        ) {
                            RegisterScreen(
                                authRepository = authRepository,
                                onRegisterSuccess = { recreate() },
                                onNavigateToLogin = { navController.navigate("login") }
                            )
                        }
                    }
                } else {
                    // User is logged in, show MainScreen
                    MainScreen(
                        authRepository = authRepository,
                        onLogout = {
                            authRepository.signOut()
                            recreate() }
                    )
                }
            }
        }
    }
}