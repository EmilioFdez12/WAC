package com.emi.wac.ui.screens

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil3.compose.rememberAsyncImagePainter
import com.emi.wac.common.Constants.BCKG_IMG
import com.emi.wac.common.Constants.CATEGORY_F1
import com.emi.wac.common.Constants.CATEGORY_MOTOGP
import com.emi.wac.common.Constants.CAT_DETAILS
import com.emi.wac.ui.components.home.RaceCard
import com.emi.wac.ui.theme.PrimaryOrange
import com.emi.wac.ui.theme.WACTheme
import com.emi.wac.viewmodel.DataState
import com.emi.wac.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
    navController: NavHostController
) {
    val nextF1Race by viewModel.nextF1Race.collectAsState()
    val nextMotoGPRace by viewModel.nextMotoGPRace.collectAsState()
    val backgroundPainter: Painter = rememberAsyncImagePainter(model = BCKG_IMG)

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = backgroundPainter,
            contentDescription = "App Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = nextF1Race is DataState.Success,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                when (val state = nextF1Race) {
                    is DataState.Success -> {
                        RaceCard(
                            logo = CATEGORY_F1,
                            raceInfo = state.data.grandPrix,
                            onCardClick = { navController.navigate("$CAT_DETAILS/$CATEGORY_F1") },
                            imageOffset = Offset(-24f, 0f),
                            category = CATEGORY_F1
                        )
                    }
                    is DataState.Error -> {
                        Text(
                            text = "Error F1: ${state.message}",
                            color = Red,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                        )
                    }
                    else -> {} // No renderizar nada en Loading
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(
                visible = nextMotoGPRace is DataState.Success,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                when (val state = nextMotoGPRace) {
                    is DataState.Success -> {
                        RaceCard(
                            logo = CATEGORY_MOTOGP,
                            raceInfo = state.data.grandPrix,
                            countdownColor = PrimaryOrange,
                            imageOffset = Offset(-24f, 36f),
                            imageScale = 1.6f,
                            onCardClick = { navController.navigate("$CAT_DETAILS/$CATEGORY_MOTOGP") },
                            category = CATEGORY_MOTOGP
                        )
                    }
                    is DataState.Error -> {
                        Text(
                            text = "Error MotoGP: ${state.message}",
                            color = Red,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                    else -> {} // No renderizar nada en Loading
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    WACTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            HomeScreen(
                viewModel = HomeViewModel(LocalContext.current.applicationContext as Application),
                navController = rememberNavController()
            )
        }
    }
}