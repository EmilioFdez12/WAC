package com.emi.wac.ui.components.category_details.standings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.emi.wac.data.model.contructor.Constructor
import com.emi.wac.data.model.drivers.Driver
import com.emi.wac.data.repository.StandingsRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

/**
 * Composable function to display the top three drivers in the standings with adaptive sizing.
 */
@Composable
fun TopThreeDrivers(
    standings: List<Driver>,
    category: String
) {
    var constructorsList by remember { mutableStateOf<List<Constructor>?>(null) }
    val db = Firebase.firestore
    val standingsRepository = remember { StandingsRepository(db) }
    val firstPlaceColor = Color(0xFFFFD700)
    val secondPlaceColor = Color(0xFFC0C0C0)
    val thirdPlaceColor = Color(0xFFE28E19)

    // Get screen width for adaptive sizing
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // Adaptive horizontal padding for the Row (space to screen edges)
    val rowHorizontalPadding = when {
        screenWidth < 360.dp -> 12.dp
        screenWidth < 400.dp -> 10.dp
        screenWidth < 600.dp -> 8.dp
        else -> 4.dp
    }

    val verticalPadding = when {
        screenWidth < 360.dp -> 4.dp
        screenWidth < 400.dp -> 6.dp
        screenWidth < 600.dp -> 8.dp
        else -> 12.dp
    }


    LaunchedEffect(category) {
        val constructorsResult = standingsRepository.getConstructorStandings(category)
        if (constructorsResult.isSuccess) {
            constructorsList = constructorsResult.getOrNull()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = rowHorizontalPadding, vertical = verticalPadding),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Second place (left)
        if (standings.size > 1) {
            TopDriverCard(
                standing = standings[1],
                position = "2°",
                color = secondPlaceColor,
                constructorList = constructorsList,
                category = category,
                modifier = Modifier.weight(1f)
            )
        }

        // First place (center)
        if (standings.isNotEmpty()) {
            TopDriverCard(
                standing = standings[0],
                position = "1°",
                color = firstPlaceColor,
                constructorList = constructorsList,
                category = category,
                modifier = Modifier.weight(1f)
            )
        }

        // Third place (right)
        if (standings.size > 2) {
            TopDriverCard(
                standing = standings[2],
                position = "3°",
                color = thirdPlaceColor,
                constructorList = constructorsList,
                category = category,
                modifier = Modifier.weight(1f)
            )
        }
    }
}