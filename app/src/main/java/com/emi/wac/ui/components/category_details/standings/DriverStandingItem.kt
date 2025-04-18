package com.emi.wac.ui.components.category_details.standings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.emi.wac.data.model.drivers.DriverStanding
import com.emi.wac.ui.theme.AlataTypography
import com.emi.wac.ui.theme.getPrimaryColorForCategory

@Composable
fun DriverStandingItem(
    standing: DriverStanding,
    category: String
) {
    val primaryColor = getPrimaryColorForCategory(category)

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
                text = standing.position,
                style = AlataTypography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        // Driver name
        Text(
            text = standing.driver,
            style = AlataTypography.titleMedium,
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
    }
}