package com.emi.wac.ui.components.category_details.overview.leader

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.emi.wac.common.Constants.ASSETS
import com.emi.wac.data.model.contructor.Constructor

/**
 * Displays a card for the leading constructor in a racing category with adaptive image sizing.
 *
 * @param modifier Modifier to customize the composable layout.
 * @param constructor The [Constructor] data to display.
 * @param car The asset path for the constructor's car image.
 * @param offsetX X offset for the car image (default: 60.dp).
 * @param offsetY Y offset for the car image (default: 0.dp).
 * @param imageScale Scale factor for the car image (default: 1f).
 * @param rotation Rotation angle for the car image in degrees (default: 0f).
 * @param category The racing category (e.g., "F1", "MotoGP").
 */
@Composable
fun ConstructorLeaderCard(
    modifier: Modifier = Modifier,
    constructor: Constructor,
    car: String,
    offsetX: Dp = 60.dp,
    offsetY: Dp = 0.dp,
    imageScale: Float = 1f,
    rotation: Float = 0f,
    category: String
) {
    // Get screen configuration for adaptive sizing
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // Adaptive image size based on screen width
    val imageSize = when {
        screenWidth < 360.dp -> 100.dp
        screenWidth < 400.dp -> 140.dp
        screenWidth < 600.dp -> 160.dp
        screenWidth < 840.dp -> 180.dp
        else -> 200.dp
    }

    Log.d("ConstructorLeaderCard", "Constructor: $constructor, ImageSize: $imageSize")
    BaseLeaderCard(
        modifier = modifier,
        title = constructor.team,
        position = constructor.position,
        points = constructor.points,
        imageUrl = "$ASSETS$car",
        imageDescription = "Constructor Car for ${constructor.team}",
        category = category,
        offsetX = offsetX,
        offsetY = offsetY,
        imageScale = imageScale,
        imageSize = imageSize,
        rotation = rotation,
        imageAlignment = Alignment.CenterEnd
    )
}