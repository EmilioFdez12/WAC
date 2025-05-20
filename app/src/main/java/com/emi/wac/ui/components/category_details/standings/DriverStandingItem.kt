package com.emi.wac.ui.components.category_details.standings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.emi.wac.common.Constants.ASSETS
import com.emi.wac.data.model.contructor.Constructor
import com.emi.wac.data.model.drivers.Driver
import com.emi.wac.ui.theme.AlataTypography
import com.emi.wac.ui.theme.PrimaryWhite
import com.emi.wac.ui.theme.getPrimaryColorForCategory

@Composable
fun DriverStandingItem(
    standing: Driver,
    category: String,
    drivers: List<Driver>?,
    constructors: List<Constructor>?
) {
    val primaryColor = getPrimaryColorForCategory(category)
    val context = LocalContext.current

    // Search the driver and the team logo
    val driver = drivers?.find { it.name.contains(standing.name, ignoreCase = true) }
    val teamLogo = driver?.teamId?.let { teamId ->
        constructors?.find { it.teamId == teamId }?.logo
    } ?: ""

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Position
        Box(
            modifier = Modifier
                .background(primaryColor, RoundedCornerShape(4.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = standing.position.toString(),
                style = AlataTypography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = PrimaryWhite
            )
        }

        // Driver name
        Text(
            text = standing.name,
            style = AlataTypography.titleSmall,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )

        // Points
        Text(
            text = "${standing.points} pts",
            style = AlataTypography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        // Team logo
        if (teamLogo.isNotEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data("$ASSETS$teamLogo")
                    .crossfade(true)
                    .build(),
                contentDescription = "${driver?.team} Logo",
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )
        }
    }
}
