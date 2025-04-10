package com.emi.wac.ui.components.category_details.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.emi.wac.data.model.circuit.Circuit
import com.emi.wac.data.repository.RacingRepository
import com.emi.wac.ui.theme.AlataTypography
import com.emi.wac.ui.theme.PrimaryWhite
import com.emi.wac.ui.theme.getPrimaryColorForCategory

@Composable
fun CircuitInfo(
    modifier: Modifier = Modifier,
    category: String,
    circuit: Circuit? = null
) {
    val context = LocalContext.current
    val racingRepository = remember { RacingRepository(context) }
    var nextRace by remember { mutableStateOf(racingRepository.getNextGrandPrixObject(category)) }
    var circuitInfo by remember { mutableStateOf<Circuit?>(circuit) }
    val primaryColor = getPrimaryColorForCategory(category)
    val imgBackground = if (category == "f1") Color.Transparent else Color(0xFF151515)
    val imgPadding = if (category == "f1") 0.dp else 8.dp


    // Load circuit info if not provided
    LaunchedEffect(category, nextRace) {
        if (circuitInfo == null && nextRace != null) {
            val circuits = racingRepository.getCircuits(category)
            circuitInfo = circuits?.circuits?.find { it.gp == nextRace?.gp }
        }
    }

    circuitInfo?.let { circuit ->
        Column(modifier = modifier.fillMaxWidth()) {
            // Circuit name at the top
            Text(
                text = circuit.name,
                style = AlataTypography.titleLarge,
                color = PrimaryWhite,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .background(primaryColor, shape = RoundedCornerShape(4.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )

            // Large circuit image - removed padding and extended to full width
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("file:///android_asset${circuit.image}")
                    .crossfade(true)
                    .build(),
                contentDescription = "Circuit layout for ${circuit.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .background(imgBackground, shape = RoundedCornerShape(8.dp))
                    .padding(imgPadding),
                contentScale = ContentScale.Fit
            )

            // Card with circuit details
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF151515)
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Circuit details in a single column
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Length
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "LENGTH",
                                style = AlataTypography.titleMedium,
                                color = primaryColor
                            )
                            Text(
                                text = "${circuit.length} km",
                                style = AlataTypography.titleLarge,
                                color = PrimaryWhite,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        // Laps
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "LAPS",
                                style = AlataTypography.titleMedium,
                                color = primaryColor
                            )
                            Text(
                                text = "${circuit.raceLaps}",
                                style = AlataTypography.titleLarge,
                                color = PrimaryWhite,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    // Lap Record
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "LAP RECORD",
                            style = AlataTypography.titleMedium,
                            color = primaryColor
                        )

                        Text(
                            text = circuit.lapRecord,
                            style = AlataTypography.titleSmall,
                            color = PrimaryWhite,
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .background(primaryColor, shape = RoundedCornerShape(4.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                        )
                    }
                }
            }
        }
    }
}