package com.emi.wac.ui.components.category_details.standings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.emi.wac.common.Constants.ASSETS
import com.emi.wac.common.Constants.CATEGORY_INDYCAR
import com.emi.wac.common.Constants.CATEGORY_MOTOGP
import com.emi.wac.data.model.contructor.Constructor
import com.emi.wac.data.model.drivers.Driver
import com.emi.wac.ui.theme.AlataTypography
import com.emi.wac.ui.theme.PrimaryBlack

/**
 * Composable function to display a driver card in the standings with adaptive sizing.
 */
@Composable
fun TopDriverCard(
    standing: Driver,
    position: String,
    color: Color,
    constructorList: List<Constructor>?,
    category: String,
    modifier: Modifier = Modifier
) {
    val portraitPath = standing.portrait
    // Find constructor logo
    val teamLogo = standing.teamId.let { teamId ->
        constructorList?.find { it.teamId == teamId }?.logo ?: ""
    }

    // Get screen width for adaptive sizing
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // Adaptive sizing for team logo
    val teamLogoWidth = when {
        screenWidth < 360.dp -> 72.dp
        screenWidth < 400.dp -> 86.dp
        screenWidth < 600.dp -> 124.dp
        else -> 148.dp
    }

    val teamLogoHeight = when {
        screenWidth < 360.dp -> 20.dp
        screenWidth < 400.dp -> 28.dp
        screenWidth < 600.dp -> 56.dp
        else -> 72.dp
    }

    val teamHorizontalPadding = when {
        screenWidth < 360.dp -> 2.dp
        screenWidth < 400.dp -> 4.dp
        screenWidth < 600.dp -> 16.dp
        else -> 20.dp
    }

    val teamVerticalPadding = when {
        screenWidth < 360.dp -> 2.dp
        screenWidth < 400.dp -> 4.dp
        screenWidth < 600.dp -> 12.dp
        else -> 16.dp
    }

    // Adaptive font size for driver number
    val driverNumberFontSize = when {
        screenWidth < 360.dp -> 16.sp
        screenWidth < 400.dp -> 20.sp
        screenWidth < 600.dp -> 24.sp
        else -> 28.sp
    }

    // Adaptive font size for position
    val positionFontSize = when {
        screenWidth < 360.dp -> 8.sp
        screenWidth < 400.dp -> 12.sp
        screenWidth < 600.dp -> 16.sp
        else -> 18.sp
    }

    // Adaptive font size for points text
    val pointsFontSize = when {
        screenWidth < 360.dp -> 8.sp
        screenWidth < 400.dp -> 12.sp
        screenWidth < 600.dp -> 16.sp
        else -> 18.sp
    }

    val portraitScale = when {
        screenWidth < 360.dp -> if (category == CATEGORY_MOTOGP) 1.4f else 0.7f
        screenWidth < 400.dp -> if (category == CATEGORY_MOTOGP) 1.6f else 0.8f
        screenWidth < 600.dp -> if (category == CATEGORY_MOTOGP) 2f else 1f
        else -> if (category == CATEGORY_MOTOGP) 2.2f else 1.2f
    }

    val portraitOffsetY = when {
        screenWidth < 360.dp -> if (category == CATEGORY_MOTOGP || category == CATEGORY_INDYCAR) 8.dp else 14.dp
        screenWidth < 400.dp -> if (category == CATEGORY_MOTOGP || category == CATEGORY_INDYCAR) 10.dp else 12.dp
        screenWidth < 600.dp -> if (category == CATEGORY_MOTOGP || category == CATEGORY_INDYCAR) 10.dp else 0.dp
        else -> if (category == CATEGORY_MOTOGP) 12.dp else 0.dp
    }

    val driverPadding = when {
        screenWidth < 360.dp -> 0.dp
        screenWidth < 400.dp -> 0.dp
        screenWidth < 600.dp -> 4.dp
        else -> 6.dp
    }

    val maxWidthFraction = when {
        screenWidth < 360.dp -> 0.80f
        screenWidth < 400.dp -> 0.95f
        screenWidth < 600.dp -> 1f
        else -> 1f
    }

    val columnPadding = when {
        screenWidth < 360.dp -> 8.dp
        screenWidth < 400.dp -> 6.dp
        screenWidth < 600.dp -> 4.dp
        else -> 2.dp
    }

    Column(
        modifier = modifier
            .fillMaxWidth(fraction = maxWidthFraction)
            .padding(horizontal = columnPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Team logo above the card
        if (teamLogo.isNotEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("$ASSETS$teamLogo")
                    .crossfade(true)
                    .build(),
                contentDescription = "${standing.team} logo",
                modifier = Modifier
                    .size(teamLogoWidth, teamLogoHeight)
                    .background(PrimaryBlack, RoundedCornerShape(8.dp))
                    .padding(teamHorizontalPadding, teamVerticalPadding)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.size(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth(fraction = maxWidthFraction)
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
                        contentDescription = "Driver ${standing.name}",
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(portraitScale)
                            .padding(
                                start = driverPadding,
                                end = driverPadding,
                                top = driverPadding
                            )
                            .offset(0.dp, portraitOffsetY),
                        contentScale = ContentScale.Fit
                    )
                }

                // Driver number
                Text(
                    text = standing.number.toString(),
                    style = AlataTypography.titleLarge.copy(fontSize = driverNumberFontSize),
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
                        style = AlataTypography.titleMedium.copy(fontSize = positionFontSize),
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }

        // Points text below the card
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = maxWidthFraction)
                .padding(top = 8.dp)
                .background(color, RoundedCornerShape(4.dp))
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${standing.points} pts",
                style = AlataTypography.bodyLarge.copy(fontSize = pointsFontSize),
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
    }
}