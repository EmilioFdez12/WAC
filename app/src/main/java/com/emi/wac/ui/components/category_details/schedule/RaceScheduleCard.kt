package com.emi.wac.ui.components.category_details.schedule

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import com.emi.wac.data.model.circuit.Circuits
import com.emi.wac.data.model.sessions.GrandPrix
import com.emi.wac.data.repository.RacingRepository
import com.emi.wac.ui.theme.AlataTypography
import com.emi.wac.ui.theme.getPrimaryColorForCategory

@Composable
fun RaceScheduleCard(
    grandPrix: GrandPrix,
    category: String,
    modifier: Modifier,
) {
    val primaryColor = getPrimaryColorForCategory(category)
    var expanded by remember { mutableStateOf(false) }
    var circuitsData by remember { mutableStateOf<Circuits?>(null) }
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "rotation"
    )

    val context = LocalContext.current
    val racingRepository = remember { RacingRepository(context) }

    LaunchedEffect(category) {
        circuitsData = racingRepository.getCircuits(category)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF202020)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header with GP name, dates and flag
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Date badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(primaryColor)
                        .padding(8.dp)
                ) {
                    Text(
                        text = grandPrix.dates,
                        style = AlataTypography.bodyLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // GP Name
                Text(
                    text = grandPrix.gp,
                    style = AlataTypography.titleMedium,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )

                // Flag
                if (grandPrix.flag.isNotEmpty()) {
                    val flagPath = "file:///android_asset${grandPrix.flag}"
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(flagPath)
                                .build()
                        ),
                        contentDescription = "Flag for ${grandPrix.gp}",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(4.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                // Dropdown arrow
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand",
                    tint = Color.White,
                    modifier = Modifier
                        .rotate(rotationState)
                        .padding(8.dp)
                )
            }

            // Sessions details (expanded view)
            AnimatedVisibility(visible = expanded) {
                SessionsList(
                    modifier = Modifier
                        .animateContentSize(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        ),
                    grandPrix = grandPrix,
                    primaryColor = primaryColor,
                    circuitsData = circuitsData
                )
            }
        }
    }
}