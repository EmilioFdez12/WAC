package com.emi.wac.ui.components.category_details.standings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.emi.wac.data.model.contructor.Constructor
import com.emi.wac.data.model.drivers.Driver
import com.emi.wac.data.repository.StandingsRepository
import com.emi.wac.ui.theme.getPrimaryColorForCategory
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

/**
 * Composable function to display a list of driver standings items with adaptive sizing.
 */
@Composable
fun DriverStandingsList(
    standings: List<Driver>,
    category: String
) {
    val dividerColor = getPrimaryColorForCategory(category)

    val db = Firebase.firestore
    val standingsRepository = remember { StandingsRepository(db) }
    var constructorsList by remember { mutableStateOf<List<Constructor>?>(null) }

    // Get screen width for adaptive sizing
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // Adaptive padding
    val listPadding = when {
        screenWidth < 360.dp -> 8.dp
        screenWidth < 400.dp -> 12.dp
        screenWidth < 600.dp -> 16.dp
        else -> 20.dp
    }

    val horizontalPadding = when {
        screenWidth < 360.dp -> 12.dp
        screenWidth < 400.dp -> 10.dp
        screenWidth < 600.dp -> 8.dp
        else -> 4.dp
    }

    // Adaptive padding
    val verticalPadding = when {
        screenWidth < 360.dp -> 2.dp
        screenWidth < 400.dp -> 4.dp
        screenWidth < 600.dp -> 8.dp
        else -> 10.dp
    }

    // Adaptive divider thickness
    val dividerThickness = when {
        screenWidth < 360.dp -> 0.8.dp
        screenWidth < 400.dp -> 0.9.dp
        screenWidth < 600.dp -> 1.dp
        else -> 1.1.dp
    }

    LaunchedEffect(category) {
        val constructorsResult = standingsRepository.getConstructorStandings(category)
        if (constructorsResult.isSuccess) {
            constructorsList = constructorsResult.getOrNull()
        }
    }

    Card(
        modifier = Modifier
            .fillMaxSize() // Use fillMaxSize instead of fixed height
            .padding(horizontal = horizontalPadding),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        shape = RoundedCornerShape(
            topStart = 8.dp,
            topEnd = 8.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF404040),
                            Color(0xFF151515),
                            Color(0xFF151515)
                        )
                    )
                )
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(listPadding)
            ) {
                items(standings) { standing ->
                    DriverStandingItem(
                        standing = standing,
                        category = category,
                        drivers = standings,
                        constructors = constructorsList
                    )
                    if (standing != standings.last()) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = verticalPadding),
                            thickness = dividerThickness,
                            color = dividerColor
                        )
                    }
                }
            }
        }
    }
}