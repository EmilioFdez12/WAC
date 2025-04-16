package com.emi.wac.ui.components.category_details.overview.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.emi.wac.data.repository.RacingRepository
import com.emi.wac.data.utils.DateUtils
import com.emi.wac.ui.theme.AlataTypography
import com.emi.wac.ui.theme.getHardColorForCategory
import com.emi.wac.ui.theme.getSoftColorForCategory

@Composable
fun RaceWeekendSchedule(
    modifier: Modifier = Modifier,
    category: String
) {
    val context = LocalContext.current
    val racingRepository = remember { RacingRepository(context) }
    var nextRace by remember { mutableStateOf<GrandPrix?>(null) }
    val softColor = getSoftColorForCategory(category)
    val hardColor = getHardColorForCategory(category)

    LaunchedEffect(category) {
        nextRace = racingRepository.getNextGrandPrixObject(category)
    }

    nextRace?.let { race ->
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
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

                // List of all sessions with their information
                val sessions = mutableListOf<SessionInfo>()

                race.sessions.race.let {
                    sessions.add(SessionInfo("RACE", it.day, it.time, true))
                }

                race.sessions.qualifying?.let {
                    sessions.add(SessionInfo("QUALIFYING", it.day, it.time, false))
                }

                race.sessions.sprint?.let {
                    sessions.add(SessionInfo("SPRINT", it.day, it.time, false))
                }

                race.sessions.sprintQualifying?.let {
                    sessions.add(SessionInfo("SPRINT QUALIFYING", it.day, it.time, false))
                }

                race.sessions.practice3?.let {
                    sessions.add(SessionInfo("PRACTICE 3", it.day, it.time, false))
                }

                race.sessions.practice2?.let {
                    sessions.add(SessionInfo("PRACTICE 2", it.day, it.time, false))
                }

                race.sessions.practice1.let {
                    sessions.add(SessionInfo("PRACTICE 1", it.day, it.time, false))
                }

                // Order sessions by date and time
                val sortedSessions = sessions.sortedWith(compareByDescending {
                    DateUtils.parseSessionDate(
                        it.day,
                        it.time
                    )
                })

                // Show ordered sessions
                sortedSessions.forEach { session ->
                    SessionItem(
                        day = session.day,
                        name = session.name,
                        time = session.time,
                        isPrimary = session.isPrimary,
                        category = category,
                    )
                }
            }
        }
    }
}

// Private class to store session information
private data class SessionInfo(
    val name: String,
    val day: String,
    val time: String,
    val isPrimary: Boolean
)