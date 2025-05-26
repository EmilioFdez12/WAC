package com.emi.wac.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Componente base reutilizable para todas las cards de la aplicación.
 * Proporciona un diseño consistente con gradiente y comportamiento de click.
 * 
 * @param modifier Modificador para personalizar el diseño
 * @param onClick Acción a ejecutar cuando se hace click en la card
 * @param gradientColors Lista de colores para el gradiente de fondo
 * @param cornerRadius Radio de las esquinas redondeadas
 * @param elevation Elevación de la card
 * @param padding Padding interno del contenido
 * @param content Contenido de la card
 */
@Composable
fun BaseCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    gradientColors: List<Color> = listOf(
        Color(0xFF404040),
        Color(0xFF151515)
    ),
    cornerRadius: Dp = 12.dp,
    elevation: Dp = 4.dp,
    padding: PaddingValues = PaddingValues(16.dp),
    content: @Composable BoxScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else Modifier
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(gradientColors),
                    shape = RoundedCornerShape(cornerRadius)
                )
                .clip(RoundedCornerShape(cornerRadius))
                .padding(padding)
        ) {
            content()
        }
    }
}