package com.emi.wac.ui.screens.app

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.rememberAsyncImagePainter
import com.emi.wac.R
import com.emi.wac.common.Constants.BCKG_IMG
import com.emi.wac.common.Constants.CATEGORY_F1
import com.emi.wac.common.Constants.CATEGORY_INDYCAR
import com.emi.wac.common.Constants.CATEGORY_MOTOGP
import com.emi.wac.common.Constants.CAT_DETAILS
import com.emi.wac.common.Constants.LEXENDBOLD
import com.emi.wac.ui.components.home.RaceCard
import com.emi.wac.ui.theme.PrimaryOrange
import com.emi.wac.viewmodel.DataState
import com.emi.wac.viewmodel.HomeViewModel

/**
 * Data class to hold category-specific configuration for the home screen.
 *
 * @param category The category identifier (e.g., CATEGORY_F1)
 * @param dataState The state of the race data for this category
 * @param countdownColor Optional color for the countdown timer
 * @param imageOffset Offset for the race card image
 * @param imageScale Scale for the race card image
 */
private data class CategoryConfig<T>(
    val category: String,
    val dataState: DataState<T>,
    val countdownColor: Color? = null,
    val imageOffset: Offset,
    val imageScale: Float = 1f
)

/**
 * Composable function to display the home screen.
 * Shows upcoming races for each category (F1, MotoGP, IndyCar) if a race is scheduled.
 *
 * @param modifier Modifier for the composable layout
 * @param viewModel ViewModel providing race data
 * @param navController Navigation controller for handling navigation to category details
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
    navController: NavHostController
) {
    val nextF1Race by viewModel.nextF1Race.collectAsState()
    val nextMotoGPRace by viewModel.nextMotoGPRace.collectAsState()
    val nextIndycarRace by viewModel.nextIndycarRace.collectAsState()
    val backgroundPainter: Painter = rememberAsyncImagePainter(model = BCKG_IMG)

    // List of category configurations
    val categories = listOf(
        CategoryConfig(
            category = CATEGORY_F1,
            dataState = nextF1Race,
            imageOffset = Offset(-24f, 0f)
        ),
        CategoryConfig(
            category = CATEGORY_MOTOGP,
            dataState = nextMotoGPRace,
            countdownColor = PrimaryOrange,
            imageOffset = Offset(-24f, 36f),
            imageScale = 1.6f
        ),
        CategoryConfig(
            category = CATEGORY_INDYCAR,
            dataState = nextIndycarRace,
            imageOffset = Offset(-40f, 0f)
        )
    )

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = backgroundPainter,
            contentDescription = "App Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            item {
                // WAC and WAC Text logos
                Row(
                    horizontalArrangement = Arrangement.spacedBy((-60).dp),
                    modifier = Modifier.padding(end = 36.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.wac_logo),
                        contentDescription = "WAC Logo",
                        modifier = Modifier
                            .size(132.dp)
                            .padding(end = 40.dp),
                        contentScale = ContentScale.Fit
                    )
                    Image(
                        painter = painterResource(id = R.drawable.wac_text),
                        contentDescription = "WAC Text",
                        modifier = Modifier.size(132.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Text(
                    text = "UPCOMING RACES",
                    fontFamily = LEXENDBOLD,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .background(Color(0xFFACFF86), shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color(0xFF151515)
                )

                Spacer(modifier = Modifier.height(32.dp))
            }

            // Display race cards for each category
            items(categories.size) { index ->
                val config = categories[index]
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(300))
                ) {
                    when (val state = config.dataState) {
                        is DataState.Success -> {
                            RaceCard(
                                logo = config.category,
                                raceInfo = state.data.grandPrix,
                                countdownColor = config.countdownColor,
                                imageOffset = config.imageOffset,
                                imageScale = config.imageScale,
                                onCardClick = { navController.navigate("$CAT_DETAILS/${config.category}") },
                                category = config.category
                            )
                            if (config.category == CATEGORY_INDYCAR) {
                                Log.d("HomeScreen", "RaceCard: ${state.data.grandPrix}")
                            }
                        }

                        is DataState.Error -> {
                            // Optionally handle error state (e.g., show error message)
                        }

                        is DataState.Loading -> {
                            // Optionally handle loading state (e.g., show loading indicator)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}