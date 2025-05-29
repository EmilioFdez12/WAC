package com.emi.wac.ui.components.category_details.standings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import com.emi.wac.data.model.contructor.Constructor
import com.emi.wac.data.model.drivers.Driver
import com.emi.wac.data.repository.StandingsRepository
import com.emi.wac.ui.theme.getPrimaryColorForCategory
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

/**
 * Composable function to display a list of driver standings items.
 */
@Composable
fun DriverStandingsList(
    standings: List<Driver>,
    category: String
) {
    val db = Firebase.firestore
    val standingsRepository = remember { StandingsRepository(db) }
    var constructorsList by remember { mutableStateOf<List<Constructor>?>(null) }

    LaunchedEffect(category) {
        val constructorsResult = standingsRepository.getConstructorStandings(category)
        if (constructorsResult.isSuccess) {
            constructorsList = constructorsResult.getOrNull()
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
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
                .fillMaxWidth()
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
                    .fillMaxWidth()
                    .height(332.dp)
                    .padding(16.dp)
            ) {
                items(standings) { standing ->
                    DriverStandingItem(
                        standing = standing,
                        category = category,
                        drivers = standings,
                        constructors = constructorsList
                    )
                    if (standings.indexOf(standing) < standings.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            thickness = 0.5.dp,
                            color = getPrimaryColorForCategory(category).copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}