package com.emi.wac.ui.components.category_details.standings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.emi.wac.common.Constants.ASSETS
import com.emi.wac.data.model.contructor.ConstructorStanding
import com.emi.wac.data.model.contructor.Constructors
import com.emi.wac.data.repository.RacingRepository
import com.emi.wac.ui.theme.AlataTypography
import com.emi.wac.ui.theme.PrimaryWhite
import com.emi.wac.ui.theme.getPrimaryColorForCategory

@Composable
fun ConstructorStandingItem(
    standing: ConstructorStanding,
    category: String
) {
    val primaryColor = getPrimaryColorForCategory(category)
    val context = LocalContext.current
    val racingRepository = remember { RacingRepository(context) }
    var constructorsData by remember { mutableStateOf<Constructors?>(null) }
    
    LaunchedEffect(category) {
        constructorsData = racingRepository.getConstructors(category)
    }
    
    // Buscar el logo del constructor
    val constructor = constructorsData?.constructors?.find { it.team.contains(standing.team, ignoreCase = true) }
    val teamLogo = constructor?.logo ?: ""

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Posición
        Box(
            modifier = Modifier
                .background(primaryColor, RoundedCornerShape(4.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = standing.position,
                style = AlataTypography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = PrimaryWhite
            )
        }

        // Nombre del constructor
        Text(
            text = standing.team,
            style = AlataTypography.titleSmall,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )

        // Puntos
        Text(
            text = "${standing.points} pts",
            style = AlataTypography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        // Logo del equipo
        if (teamLogo.isNotEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data("$ASSETS$teamLogo")
                    .crossfade(true)
                    .build(),
                contentDescription = "${standing.team} Logo",
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )
        }
    }
}