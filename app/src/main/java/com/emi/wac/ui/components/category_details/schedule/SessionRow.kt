package com.emi.wac.ui.components.category_details.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.emi.wac.ui.theme.AlataTypography
import com.emi.wac.ui.theme.PrimaryBlack
import com.emi.wac.ui.theme.PrimaryWhite

/**
 * Composable function to display a row representing a session.
 *
 * @param sessionName The name of the session.
 * @param day The day of the session.
 * @param time The time of the session.
 * @param isRace Flag to indicate if the session is a race.
 * @param primaryColor The primary color used for styling.
 */
@Composable
fun SessionRow(
    sessionName: String,
    day: String,
    time: String,
    isRace: Boolean,
    primaryColor: Color,
) {
    val backgroundColor = when {
        isRace -> primaryColor
        else -> PrimaryBlack
    }

    val textColor = PrimaryWhite

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Day badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(Color.White.copy(alpha = 0.3f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = day.split(" ")[0],
                style = AlataTypography.bodySmall,
                color = textColor
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Session name
        Text(
            text = sessionName,
            style = AlataTypography.bodyLarge,
            color = textColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        // Time
        Text(
            text = time,
            style = AlataTypography.bodyLarge,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}