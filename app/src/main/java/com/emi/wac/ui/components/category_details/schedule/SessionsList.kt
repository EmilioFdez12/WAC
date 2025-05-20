package com.emi.wac.ui.components.category_details.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.emi.wac.data.model.circuit.Circuits
import com.emi.wac.data.model.sessions.GrandPrix
import com.emi.wac.ui.theme.AlataTypography
import com.emi.wac.utils.SessionsUtils

@Composable
fun SessionsList(
    grandPrix: GrandPrix,
    primaryColor: Color,
    modifier: Modifier,
    circuitsData: Circuits?
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Race Weekend title
        Text(
            text = "Race Weekend",
            style = AlataTypography.titleMedium,
            color = primaryColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Create a sorted list of sessions
        val sessions = mutableListOf<Triple<String, String, String>>()

        // Add each session to the list
        grandPrix.sessions.race.let {
            sessions.add(Triple("RACE", it?.day ?: "", it?.time ?: ""))
        }

        grandPrix.sessions.qualifying?.let {
            sessions.add(Triple("QUALIFYING", it.day, it.time))
        }

        grandPrix.sessions.sprint?.let {
            sessions.add(Triple("SPRINT", it.day, it.time))
        }

        grandPrix.sessions.sprintQualifying?.let {
            sessions.add(Triple("SPRINT QUALIFYING", it.day, it.time))
        }

        grandPrix.sessions.practice3?.let {
            sessions.add(Triple("PRACTICE 3", it.day, it.time))
        }

        grandPrix.sessions.practice2?.let {
            sessions.add(Triple("PRACTICE 2", it.day, it.time))
        }

        grandPrix.sessions.practice1.let {
            sessions.add(Triple("PRACTICE 1", it.day, it.time))
        }

        val sortedSessions = SessionsUtils.sortSessionsByDateDesc(sessions)

        // Show sessions in order
        sortedSessions.forEach { (name, day, time) ->
            SessionRow(
                sessionName = name,
                day = day,
                time = time,
                isRace = name == "RACE",
                primaryColor = primaryColor,
            )
        }

        // Circuit image
        circuitsData?.circuits?.find { it.gp.contains(grandPrix.gp, ignoreCase = true) }?.let { circuit ->
            if (circuit.image.isNotEmpty()) {
                val category = getCategory(circuit.image)

                // Configurar el fondo y escala según la categoría
                val imgBackground = when(category) {
                    "indycar" -> Color.White
                    else -> Color.Transparent
                }
                val imgScale = 1f
                val imgPadding = if (category == "f1") 0.dp else 8.dp

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("file:///android_asset${circuit.image}")
                        .build(),
                    contentDescription = "Circuit image for ${grandPrix.gp}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .background(imgBackground, shape = RoundedCornerShape(8.dp))
                        .padding(imgPadding)
                        .size(200.dp)
                        .scale(imgScale),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

/**
 * Determina la categoría basada en la ruta de la imagen del circuito
 * @param imagePath Ruta de la imagen del circuito
 * @return Categoría identificada (f1, motogp, indycar, o cadena vacía si no se identifica)
 */
private fun getCategory(imagePath: String): String {
    return when {
        imagePath.contains("/f1/") -> "f1"
        imagePath.contains("/motogp/") -> "motogp"
        imagePath.contains("/indycar/") -> "indycar"
        else -> ""
    }
}