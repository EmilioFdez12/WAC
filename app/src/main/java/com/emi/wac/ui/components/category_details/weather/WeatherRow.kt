package com.emi.wac.ui.components.category_details.weather

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emi.wac.utils.WeatherUtils
import com.emi.wac.ui.theme.AlataTypography
import com.emi.wac.ui.theme.getPrimaryColorForCategory

/**
 * Composable function to display the weather row in the standings.
 *
 * @param sessionName The name of the session.
 * @param temperature The temperature in the session.
 * @param weatherCode The weather code for the session.
 * @param modifier The modifier for the composable.
 * @param category The category of the Grand Prix.
 */
@Composable
fun WeatherRow(
    sessionName: String,
    temperature: Float,
    weatherCode: Int,
    modifier: Modifier = Modifier,
    category: String
) {
    // Get screen width for adaptive font sizing
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val fontSize = when {
        screenWidth < 360.dp -> 12.sp
        screenWidth < 400.dp -> 14.sp
        screenWidth < 600.dp -> 20.sp
        else -> 22.sp
    }

    val paddingVertical = when {
        screenWidth < 360.dp -> 4.dp
        screenWidth < 400.dp -> 8.dp
        screenWidth < 600.dp -> 10.dp
        else -> 12.dp
    }

    val paddingHorizontal = when {
        screenWidth < 360.dp -> 6.dp
        screenWidth < 400.dp -> 10.dp
        screenWidth < 600.dp -> 12.dp
        else -> 16.dp
    }

    // Gets weather description based on the weather code
    val weatherDescription = WeatherUtils.getWeatherDescription(weatherCode)
    Box(
        modifier = modifier
            .background(
                color = Color.Black,
                shape = RoundedCornerShape(50.dp)
            )
            .padding(paddingHorizontal, paddingVertical)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Name on the left
            Text(
                text = sessionName,
                color = getPrimaryColorForCategory(category),
                style = AlataTypography.titleMedium.copy(fontSize = fontSize),
            )

            // Temp on the center
            Text(
                text = "${temperature.toInt()}°",
                color = Color.White,
                style = AlataTypography.titleMedium.copy(fontSize = fontSize),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 2.dp)
            )

            // Icon on the right
            Image(
                painter = painterResource(id = WeatherUtils.getWeatherIcon(weatherCode)),
                contentDescription = weatherDescription,
                modifier = Modifier
                    .size(32.dp)
            )
        }
    }
}