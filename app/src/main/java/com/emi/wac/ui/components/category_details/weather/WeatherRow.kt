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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.emi.wac.utils.WeatherUtils
import com.emi.wac.ui.theme.AlataTypography
import com.emi.wac.ui.theme.getPrimaryColorForCategory

@Composable
fun WeatherRow(
    sessionName: String,
    temperature: Float,
    weatherCode: Int,
    modifier: Modifier = Modifier,
    category: String
) {
    val weatherDescription = WeatherUtils.getWeatherDescription(weatherCode)
    Box(
        modifier = modifier
            .background(
                color = Color.Black,
                shape = RoundedCornerShape(50.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Name on the left
            Text(
                text = sessionName,
                color = getPrimaryColorForCategory(category),
                style = AlataTypography.titleMedium,
            )

            // Temp on the center
            Text(
                text = "${temperature.toInt()}°",
                color = Color.White,
                style = AlataTypography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
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