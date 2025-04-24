package com.emi.wac.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.emi.wac.common.Constants.ASSETS
import com.emi.wac.data.model.RaceInfo
import com.emi.wac.ui.theme.AlataTypography
import com.emi.wac.ui.theme.PrimaryWhite
import com.emi.wac.ui.theme.getHardColorForCategory
import com.emi.wac.ui.theme.getPrimaryColorForCategory
import com.emi.wac.ui.theme.getSoftColorForCategory

@Composable
fun RaceCard(
    modifier: Modifier = Modifier,
    raceInfo: RaceInfo,
    logo: String,
    onCardClick: () -> Unit = {},
    gradientColors: List<Color> = listOf(
        Color(0xFF404040),
        Color(0xFF151515),
        Color(0xFF151515)
    ),
    category: String,
    countdownColor: Color = getPrimaryColorForCategory(logo),
    imageAlignment: Alignment = Alignment.CenterEnd,
    imagePadding: PaddingValues = PaddingValues(start = 100.dp),
    imageOffset: Offset = Offset(0f, 0f),
    imageScale: Float = 1f,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 16.dp)
            .clickable { onCardClick() },
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
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    Brush.linearGradient(
                        colors = gradientColors,
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                )
        ) {
            Text(
                text = "LEADER",
                color = getHardColorForCategory(category),
                style = AlataTypography.titleMedium,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 16.dp, top = 16.dp)
                    .background(getSoftColorForCategory(category), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            )

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("$ASSETS${raceInfo.leaderImagePath}")
                    .crossfade(true)
                    .build(),
                contentDescription = "Leader image",
                modifier = Modifier
                    .fillMaxSize()
                    .scale(imageScale)
                    .offset(x = imageOffset.x.dp, y = imageOffset.y.dp)
                    .align(imageAlignment)
                    .padding(imagePadding),
                contentScale = ContentScale.Fit
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Arrow Forward",
                tint = PrimaryWhite,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
                    .size(24.dp),
            )

            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 16.dp, top = 20.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("$ASSETS/logos/$logo.png")
                        .crossfade(true)
                        .build(),
                    contentDescription = logo.uppercase() + " Logo",
                )

                Text(
                    text = raceInfo.sessionName.uppercase(),
                    color = PrimaryWhite,
                    modifier = Modifier.padding(top = 12.dp),
                    style = MaterialTheme.typography.titleLarge
                )

                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .background(
                            if (raceInfo.timeRemaining == "LIVE") Color.Green else countdownColor,
                            RoundedCornerShape(4.dp)
                        )
                ) {
                    Text(
                        text = raceInfo.timeRemaining,
                        color = PrimaryWhite,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Spacer(Modifier.height(12.dp))

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("$ASSETS/${raceInfo.flagPath}")
                        .crossfade(true)
                        .build(),
                    contentDescription = "Country Flag",
                    modifier = Modifier
                        .size(width = 56.dp, height = 40.dp),
                    contentScale = ContentScale.FillWidth
                )
            }
        }
    }
}