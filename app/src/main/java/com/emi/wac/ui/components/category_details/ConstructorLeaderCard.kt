package com.emi.wac.ui.components.category_details

import android.util.Log
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
import androidx.compose.ui.draw.rotate
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
import com.emi.wac.data.model.contructor.ConstructorStanding
import com.emi.wac.ui.theme.AlataTypography
import com.emi.wac.ui.theme.PrimaryRed

private const val TAG = "ConstructorLeaderCard"

@Composable
fun ConstructorLeaderCard(
    modifier: Modifier = Modifier,
    constructorStanding: ConstructorStanding,
    car: String,
    offsetX: Dp = 60.dp,
    offsetY: Dp = 0.dp,
    imageScale: Float = 1f,
    rotation: Float = 0f,
) {
    // Log para depuración
    Log.d(TAG, "ConstructorLeaderCard called with team: ${constructorStanding.team}, car: $car")
    
    Box(modifier = modifier.fillMaxWidth()) {
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = constructorStanding.team,
                        style = AlataTypography.titleLarge,
                        color = Color.White
                    )
                    
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
                                text = "${constructorStanding.position}º",
                                style = AlataTypography.bodyLarge,
                                color = Color.Black
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(PrimaryRed, RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${constructorStanding.points} pts",
                                style = AlataTypography.bodyLarge,
                                color = Color.White
                            )
                        }
                    }
                }
                
                // Log para depuración de la ruta de la imagen
                Log.d(TAG, "Loading car image from: file:///android_asset$car")
                
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("file:///android_asset$car")
                        .crossfade(true)
                        .build(),
                    contentDescription = "Constructor Car",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .scale(imageScale)
                        .size(200.dp)
                        .rotate(rotation)
                        .offset(x = offsetX, y = offsetY)
                        .align(Alignment.CenterEnd),
                )
            }
        }
    }
}