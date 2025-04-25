package com.emi.wac.ui.components.category_details.standings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.emi.wac.common.Constants.ASSETS
import com.emi.wac.common.Constants.CATEGORY_MOTOGP
import com.emi.wac.data.model.contructor.Constructor
import com.emi.wac.data.model.contructor.Constructors
import com.emi.wac.data.model.drivers.Driver
import com.emi.wac.data.model.drivers.DriverStanding
import com.emi.wac.data.model.drivers.Drivers
import com.emi.wac.data.repository.RacingRepository
import com.emi.wac.ui.theme.AlataTypography
import com.emi.wac.ui.theme.PrimaryBlack

@Composable
fun TopThreeDrivers(
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

    val firstPlaceColor = Color(0xFFFFD700)
    val secondPlaceColor = Color(0xFFC0C0C0)
    val thirdPlaceColor = Color(0xFFE28E19)


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Second place (left)
        if (standings.size > 1) {
            TopDriverCard(
                standing = standings[1],
                position = "2°",
                color = secondPlaceColor,
                driversList = driversData?.drivers,
                constructorList = constructorsData?.constructors,
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
                driversList = driversData?.drivers,
                constructorList = constructorsData?.constructors,
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
                driversList = driversData?.drivers,
                constructorList = constructorsData?.constructors,
                category = category,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TopDriverCard(
    standing: DriverStanding,
    position: String,
    color: Color,
    driversList: List<Driver>?,
    constructorList: List<Constructor>?,
    category: String,
    modifier: Modifier = Modifier
) {
    // Find the driver in the drivers list to get the portrait
    val driver = driversList?.find { it.name.contains(standing.driver, ignoreCase = true) }
    val portraitPath = driver?.portrait ?: ""
    // Find constructor logo
    val teamLogo = driver?.teamId?.let { teamId ->
        constructorList?.find { it.teamId == teamId }?.logo
    } ?: ""

    Column(
        modifier = modifier.padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Team logo above the card
        if (teamLogo.isNotEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("$ASSETS$teamLogo")
                    .crossfade(true)
                    .build(),
                contentDescription = "${driver?.team} logo",
                modifier = Modifier
                    .size(width = 80.dp, height = 40.dp)
                    .background(PrimaryBlack, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.size(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF202020)
            ),
            border = BorderStroke(3.dp, color),
            shape = RoundedCornerShape(8.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Driver portrait
                if (portraitPath.isNotEmpty()) {
                    val imagePath = "$ASSETS$portraitPath"
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(imagePath)
                                .build()
                        ),
                        contentDescription = "Driver ${standing.driver}",
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(if (category == CATEGORY_MOTOGP) 2f else 1f)
                            .padding(start = 4.dp, end = 4.dp, top = 4.dp)
                            .offset(0.dp, if (category == CATEGORY_MOTOGP) 8.dp else 0.dp),
                        contentScale = ContentScale.Fit,

                        )
                }

                // Driver number
                Text(
                    text = driver?.number?.toString() ?: "",
                    style = AlataTypography.titleLarge,
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 8.dp, bottom = 8.dp)
                        .background(color, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = position,
                        style = AlataTypography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }

        // Points text below the card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .background(color, RoundedCornerShape(4.dp))
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${standing.points} pts",
                style = AlataTypography.bodyLarge,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}