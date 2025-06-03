package com.emi.wac.ui.components.category_details.overview.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emi.wac.ui.theme.AlataTypography
import com.emi.wac.ui.theme.PrimaryBlack
import com.emi.wac.ui.theme.PrimaryWhite
import com.emi.wac.ui.theme.getPrimaryColorForCategory
import com.emi.wac.utils.DateUtils

/**
 * Displays a single session in a Grand Prix weekend schedule.
 *
 * @param day The date of the session in "day month" format (e.g., "15 May") or "TBD".
 * @param name The name of the session (e.g., "RACE", "PRACTICE 1").
 * @param time The session time in UTC (e.g., "14:00") or "TBD".
 * @param isPrimary Whether this is the main race session (affects background color).
 * @param category The racing category (e.g., "F1", "Indycar") to determine styling.
 */
@Composable
fun SessionItem(
    day: String,
    name: String,
    time: String,
    isPrimary: Boolean,
    category: String,
) {
    val primaryColor = getPrimaryColorForCategory(category)
    val backgroundColor = if (isPrimary) primaryColor else PrimaryBlack
    val (dayNumber, monthName) = parseDay(day)
    val localTime = DateUtils.convertToLocalTime(time).takeIf { it.isNotEmpty() } ?: "TBD"

    // Get screen width for adaptive font sizing
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // Adaptive font size for the 'name' text
    val nameFontSize = when {
        screenWidth < 360.dp -> 14.sp
        screenWidth < 400.dp -> 16.sp
        screenWidth < 600.dp -> 18.sp
        else -> 18.sp
    }

    val bodyMediumFontSize = when {
        screenWidth < 360.dp -> 12.sp
        screenWidth < 400.dp -> 14.sp
        screenWidth < 600.dp -> 16.sp
        else -> 18.sp
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.width(100.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = dayNumber,
                style = AlataTypography.titleMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = monthName,
                style = AlataTypography.bodyMedium.copy(fontSize = bodyMediumFontSize),
                color = PrimaryWhite,
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.30f), RoundedCornerShape(50.dp))
                    .padding(horizontal = 8.dp)
            )
        }

        Text(
            text = name,
            style = AlataTypography.titleMedium.copy(fontSize = nameFontSize),
            color = Color.White,
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp),
            textAlign = TextAlign.Center
        )

        Text(
            text = localTime,
            style = AlataTypography.titleMedium.copy(fontSize = nameFontSize),
            color = Color.White,
            modifier = Modifier.padding(end = 16.dp)
        )
    }
}

// Parses the day string into day number and month name
private fun parseDay(day: String): Pair<String, String> {
    val parts = day.split(" ")
    return Pair(
        parts.firstOrNull() ?: "TBD",
        if (parts.size > 1) parts[1].uppercase() else "TBD"
    )
}