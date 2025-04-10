package com.emi.wac.ui.components.category_details.overview.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.emi.wac.ui.theme.AlataTypography
import com.emi.wac.ui.theme.PrimaryBlack
import com.emi.wac.ui.theme.PrimaryWhite
import com.emi.wac.ui.theme.getPrimaryColorForCategory

@Composable
fun SessionItem(
    day: String,
    name: String,
    time: String,
    isPrimary: Boolean,
    category: String,
) {
    val primaryColor = getPrimaryColorForCategory(category)

    val backgroundColor = if (isPrimary) {
        primaryColor
    } else {
        PrimaryBlack
    }

    val parts = day.split(" ")
    val dayNumber = parts[0]
    val monthName = parts[1].uppercase()


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .height(48.dp)
            .background(backgroundColor, RoundedCornerShape(4.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Day column - now horizontal layout
        Box(
            modifier = Modifier
                .width(100.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dayNumber,
                    style = AlataTypography.titleMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = monthName,
                    style = AlataTypography.bodyMedium,
                    color = PrimaryWhite,
                    modifier = Modifier
                        .background(
                            Color.White.copy(alpha = 0.30f),
                            RoundedCornerShape(50.dp)
                        )
                        .padding(horizontal = 8.dp)
                )
            }
        }

        // Session name
        Text(
            text = name,
            style = AlataTypography.titleLarge,
            color = Color.White,
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp),
            textAlign = TextAlign.Center,
        )

        // Time
        Text(
            text = time,
            style = AlataTypography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(end = 16.dp)
        )
    }
}