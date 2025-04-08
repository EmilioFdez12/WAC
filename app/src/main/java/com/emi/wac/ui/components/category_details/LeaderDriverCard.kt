package com.emi.wac.ui.components.category_details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.emi.wac.data.model.drivers.DriverStanding
import com.emi.wac.ui.theme.AlataTypography
import com.emi.wac.ui.theme.getPrimaryColorForCategory
import kotlin.Float

@Composable
fun LeaderDriverCard(
    modifier: Modifier = Modifier,
    driverStanding: DriverStanding,
    driverLogo: String,
    offsetX: Dp = 60.dp,
    offsetY: Dp = 0.dp ,
    imageScale: Float = 1f,
    category:String,
) {
    val primaryColor = getPrimaryColorForCategory(category)

    Box(modifier = modifier.fillMaxWidth()) {
        // Card principal
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(horizontal = 32.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF404040),
                                Color(0xFF151515),
                                Color(0xFF151515)
                            )
                        )
                    )
                    .clip(RoundedCornerShape(8.dp))
            ) {
                // Contenido de la tarjeta
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Nombre del piloto arriba a la izquierda
                    Text(
                        text = driverStanding.driver,
                        style = AlataTypography.titleLarge,
                        color = Color.White
                    )
                    
                    // Posición y puntos abajo a la izquierda
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFFFD700), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${driverStanding.position}º",
                                style = AlataTypography.bodyLarge,
                                color = Color.Black
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(primaryColor, RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${driverStanding.points} pts",
                                style = AlataTypography.bodyLarge,
                                color = Color.White
                            )
                        }
                    }
                }
                
                // Imagen del piloto dentro de la card (con recorte)
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("file:///android_asset$driverLogo")
                        .crossfade(true)
                        .build(),
                    contentDescription = "Driver Portrait",
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.BottomCenter)
                        .offset(x = offsetX, y = offsetY)
                        .scale(imageScale),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}