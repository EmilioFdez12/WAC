package com.emi.wac.ui.components.category_details.overview.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.emi.wac.data.model.sessions.GrandPrix
import com.emi.wac.data.model.sessions.Session
import com.emi.wac.data.repository.RacingRepository
import com.emi.wac.data.repository.StandingsRepository
import com.emi.wac.ui.theme.AlataTypography
import com.emi.wac.ui.theme.getHardColorForCategory
import com.emi.wac.ui.theme.getSoftColorForCategory
import com.emi.wac.utils.DateUtils
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.util.Date

/**
 * Composable function to display the race weekend schedule
 * for a specific category.
 *
 * @param modifier Modifier to be applied to the composable
 * @param category The category for which the schedule is displayed
 */
@Composable
fun RaceWeekendSchedule(
    modifier: Modifier = Modifier,
    category: String
) {
    val context = LocalContext.current
    val db = Firebase.firestore
    // Repositories
    val standingsRepository = remember { StandingsRepository(db) }
    val racingRepository = remember { RacingRepository(standingsRepository, context) }
    // State to hold the next race weekend
    var nextRace by remember { mutableStateOf<GrandPrix?>(null) }
    val softColor = getSoftColorForCategory(category)
    val hardColor = getHardColorForCategory(category)

    // Fetches the next race weekend
    LaunchedEffect(category) {
        nextRace = racingRepository.getNextGrandPrixObject(category)
    }

    // Shows the next race weekend
    nextRace?.let { race ->
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF151515))
                    .clip(RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = "Race Weekend",
                    style = AlataTypography.titleLarge,
                    color = hardColor,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .background(softColor, shape = RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )

                // Map sessions to SessionInfo
                val sessions = listOfNotNull(
                    race.sessions.race?.let { createSessionInfo("RACE", it, isPrimary = true) },
                    race.sessions.sprint?.let { createSessionInfo("SPRINT", it) },
                    race.sessions.sprintQualifying?.let { createSessionInfo("SPRINT QUALY", it) },
                    race.sessions.qualifying?.let { createSessionInfo("QUALIFYING", it) },
                    race.sessions.practice3?.let { createSessionInfo("PRACTICE 3", it) },
                    race.sessions.practice2?.let { createSessionInfo("PRACTICE 2", it) },
                    race.sessions.practice1?.let { createSessionInfo("PRACTICE 1", it) }
                ).sortedByDescending {
                    try {
                        DateUtils.parseSessionDate(it.day, it.time)
                    } catch (_: Exception) {
                        Date(Long.MAX_VALUE)
                    }
                }

                // Display sorted sessions
                sessions.forEachIndexed { index, session ->
                    // Spacing between items
                    if (index > 0) Spacer(modifier = Modifier.height(10.dp))
                    SessionItem(
                        day = session.day,
                        name = session.name,
                        time = session.time,
                        isPrimary = session.isPrimary,
                        category = category
                    )
                }
            }
        }
    }
}

// Object to hold session information
private data class SessionInfo(
    val name: String,
    val day: String,
    val time: String,
    val isPrimary: Boolean
)

// Creates a SessionInfo object from a Session
private fun createSessionInfo(name: String, session: Session, isPrimary: Boolean = false): SessionInfo {
    val day = session.day.takeIf { it.isNotEmpty() && it != "TBD" } ?: "TBD"
    val time = session.time.takeIf { it.isNotEmpty() && it != "TBD" } ?: "TBD"
    return SessionInfo(name, day, time, isPrimary)
}