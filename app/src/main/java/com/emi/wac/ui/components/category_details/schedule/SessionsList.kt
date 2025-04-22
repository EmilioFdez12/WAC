package com.emi.wac.ui.components.category_details.schedule

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.emi.wac.data.model.sessions.GrandPrix
import com.emi.wac.data.utils.SessionsUtils
import com.emi.wac.ui.theme.AlataTypography

@Composable
fun SessionsList(
    grandPrix: GrandPrix,
    primaryColor: Color,
    modifier: Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
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
            sessions.add(Triple("RACE", it.day, it.time))
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
    }
}