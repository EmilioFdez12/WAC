package com.emi.wac.ui.components.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.emi.wac.common.Constants.LEXENDBOLD
import com.emi.wac.ui.theme.PrimaryRed

/**
 * Enum for the different button styles available
 */
enum class WACButtonStyle {
    PRIMARY,
    SECONDARY,
    OUTLINED,
    TEXT
}

/**
 *
 * Button component that provides consistent styles and animations for all WAC buttons.
 * 
 * @param text Text to display on the button
 * @param onClick Action to perform when the button is clicked
 * @param modifier Modifier
 * @param style Button style (PRIMARY, SECONDARY, OUTLINED, TEXT)
 * @param leadingIcon Icon to display before the text
 * @param trailingIcon Icon to display after the text
 * @param iconRes ID of the resource of the icon
 */
@Composable
fun WACButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: WACButtonStyle = WACButtonStyle.PRIMARY,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    iconRes: Int? = null,
    gradientColors: List<Color> = listOf(PrimaryRed, Color(0xFFB71C1C)),
    textColor: Color = Color.White,
    borderColor: Color? = null,
    cornerRadius: Dp = 8.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
) {
    // Interaction state
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Animations
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.95f else 1f,
        animationSpec = tween(150),
        label = "button_scale"
    )
    
    val animatedTextColor by animateColorAsState(
        targetValue = if (enabled) textColor else textColor.copy(alpha = 0.6f),
        label = "text_color"
    )

    // Background config
    val backgroundModifier = when (style) {
        WACButtonStyle.PRIMARY -> {
            if (enabled) {
                Modifier.background(
                    brush = Brush.linearGradient(gradientColors),
                    shape = RoundedCornerShape(cornerRadius)
                )
            } else {
                Modifier.background(
                    color = Color.Gray.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(cornerRadius)
                )
            }
        }
        WACButtonStyle.SECONDARY -> {
            Modifier.background(
                color = Color.Transparent,
                shape = RoundedCornerShape(cornerRadius)
            ).border(
                width = 2.dp,
                color = borderColor ?: gradientColors.first(),
                shape = RoundedCornerShape(cornerRadius)
            )
        }
        WACButtonStyle.OUTLINED -> {
            Modifier.border(
                width = 1.dp,
                color = borderColor ?: textColor,
                shape = RoundedCornerShape(cornerRadius)
            )
        }
        WACButtonStyle.TEXT -> Modifier
    }
    
    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(cornerRadius))
            .then(backgroundModifier)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled
            ) { onClick() }
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Leading icon
            leadingIcon?.let {
                Icon(
                    imageVector = it,
                    tint = PrimaryRed,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            iconRes?.let {
                Icon(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = text,
                fontFamily = LEXENDBOLD
            )

            trailingIcon?.let {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = animatedTextColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}