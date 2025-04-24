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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.emi.wac.data.model.contructor.Constructors
import com.emi.wac.data.model.drivers.DriverStanding
import com.emi.wac.data.model.drivers.Drivers
import com.emi.wac.data.repository.RacingRepository
import com.emi.wac.ui.theme.getPrimaryColorForCategory

@Composable
fun DriverStandingsList(
    standings: List<DriverStanding>,
    category: String
) {
    val context = LocalContext.current
    val racingRepository = remember { RacingRepository(context) }
    var driversData by remember { mutableStateOf<Drivers?>(null) }
    var constructorsData by remember { mutableStateOf<Constructors?>(null) }

    LaunchedEffect(category) {
        driversData = racingRepository.getDrivers(category)
        constructorsData = racingRepository.getConstructors(category)
    }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
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
                .clip(RoundedCornerShape(8.dp))
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(460.dp)
                    .padding(16.dp)
            ) {
                items(standings) { standing ->
                    DriverStandingItem(
                        standing = standing,
                        category = category,
                        drivers = driversData?.drivers,
                        constructors = constructorsData?.constructors
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