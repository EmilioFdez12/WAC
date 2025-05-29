package com.emi.wac.ui.components.category_details.overview.leader

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.emi.wac.common.Constants.ASSETS
import com.emi.wac.data.model.drivers.Driver

/**
 * Displays a card for the leading driver in a racing category.
 *
 * @param modifier Modifier to customize the composable layout.
 * @param driver The [Driver] data to display.
 * @param offsetX X offset for the driver portrait (default: 60.dp).
 * @param offsetY Y offset for the driver portrait (default: 0.dp).
 * @param imageScale Scale factor for the driver portrait (default: 1f).
 * @param category The racing category (e.g., "F1", "MotoGP").
 */
@Composable
fun LeaderDriverCard(
    modifier: Modifier = Modifier,
    driver: Driver,
    offsetX: Dp = 60.dp,
    offsetY: Dp = 0.dp,
    imageScale: Float = 1f,
    category: String
) {
    Log.d("LeaderDriverCard", "Driver: $driver")
    BaseLeaderCard(
        modifier = modifier,
        title = driver.name,
        position = driver.position,
        points = driver.points,
        imageUrl = "$ASSETS${driver.portrait}",
        imageDescription = "Driver Portrait for ${driver.name}",
        category = category,
        offsetX = offsetX,
        offsetY = offsetY,
        imageScale = imageScale
    )
}