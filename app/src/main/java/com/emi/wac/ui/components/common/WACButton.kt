package com.emi.wac.ui.components.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emi.wac.ui.theme.AlataTypography
import com.emi.wac.ui.theme.PrimaryRed

/**
 * Enumeración para los diferentes estilos de botón disponibles
 */
enum class WACButtonStyle {
    PRIMARY,    // Botón principal con gradiente
    SECONDARY,  // Botón secundario con borde
    OUTLINED,   // Botón con solo borde
    TEXT        // Botón de solo texto
}

/**
 * Componente de botón unificado para toda la aplicación WAC.
 * Proporciona diferentes estilos y animaciones consistentes.
 * 
 * @param text Texto a mostrar en el botón
 * @param onClick Acción a ejecutar cuando se presiona el botón
 * @param modifier Modificador para personalizar el diseño
 * @param style Estilo del botón (PRIMARY, SECONDARY, OUTLINED, TEXT)
 * @param enabled Si el botón está habilitado
 * @param leadingIcon Icono a mostrar antes del texto
 * @param trailingIcon Icono a mostrar después del texto
 * @param iconRes ID del recurso de icono (alternativa a leadingIcon)
 * @param gradientColors Colores del gradiente para estilo PRIMARY
 * @param textColor Color del texto
 * @param borderColor Color del borde para estilos con borde
 * @param cornerRadius Radio de las esquinas
 * @param contentPadding Padding interno del contenido
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
    // Estados de interacción
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Animaciones
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.95f else 1f,
        animationSpec = tween(150),
        label = "button_scale"
    )
    
    val animatedTextColor by animateColorAsState(
        targetValue = if (enabled) textColor else textColor.copy(alpha = 0.6f),
        label = "text_color"
    )
    
    // Configuración del fondo según el estilo
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
            // Icono inicial
            leadingIcon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = animatedTextColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            // Icono de recurso
            iconRes?.let {
                Icon(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    tint = animatedTextColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            // Texto
            Text(
                text = text,
                style = AlataTypography.bodyLarge.copy(
                    color = animatedTextColor,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            )
            
            // Icono final
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