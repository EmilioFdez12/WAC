package com.emi.wac.ui.components.category_details.standings

import android.util.Log
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
import com.emi.wac.ui.theme.AlataTypography
import com.emi.wac.ui.theme.PrimaryWhite
import com.emi.wac.ui.theme.getPrimaryColorForCategory

@Composable
fun ConstructorStandingItem(
    standing: Constructor,
    category: String
) {
    val primaryColor = getPrimaryColorForCategory(category)
    val context = LocalContext.current
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

        // Constructor name
        Text(
            text = standing.team,
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
        Log.d("ConstructorStandingItem", "Logo URL: ${standing.logo}")
        if (standing.logo.isNotEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data("$ASSETS${standing.logo}")
                    .crossfade(true)
                    .build(),
                contentDescription = "${standing.team} Logo",
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )
        }
    }
}