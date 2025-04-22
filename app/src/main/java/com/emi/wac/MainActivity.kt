package com.emi.wac

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.emi.wac.ui.theme.WACTheme
import com.emi.wac.ui.screens.MainScreen
import com.emi.wac.viewmodel.DataState
import com.emi.wac.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Instala la splash screen antes de super.onCreate
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        // Variable para controlar si los datos están listos
        var keepSplashScreen = true

        // Observa los estados de nextF1Race y nextMotoGPRace
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    homeViewModel.nextF1Race,
                    homeViewModel.nextMotoGPRace
                ) { f1State, motoState ->
                    // Solo quitamos la splash screen si ambos están en Success
                    f1State is DataState.Success && motoState is DataState.Success
                }.collect { isDataReady ->
                    keepSplashScreen = !isDataReady
                }
            }
        }

        // Configura la condición para mantener la splash screen
        splashScreen.setKeepOnScreenCondition { keepSplashScreen }

        // Configura el contenido de Compose
        setContent {
            WACTheme {
                MainScreen()
            }
        }
    }
}