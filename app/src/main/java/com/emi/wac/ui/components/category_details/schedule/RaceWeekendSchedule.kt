package com.emi.wac.ui.components.category_details.schedule

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
        // Use the new method to get the next race directly
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
                    modifier = Modifier.padding(bottom = 8.dp)
                        .background(softColor, shape = RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
                
                // Display sessions conditionally based on what's available
                // Race session (always present)
                SessionItem(
                    day = race.sessions.race.day,
                    name = "RACE",
                    time = race.sessions.race.time,
                    isPrimary = true,
                    category = category,
                )
                
                // Qualifying session (if present)
                race.sessions.qualifying?.let {
                    SessionItem(
                        day = it.day,
                        name = "QUALIFYING",
                        time = it.time,
                        isPrimary = false,
                        category = category,
                    )
                }
                
                // Sprint session (if present)
                race.sessions.sprint?.let {
                    SessionItem(
                        day = it.day,
                        name = "SPRINT",
                        time = it.time,
                        isPrimary = false,
                        category = category,
                    )
                }
                
                // Sprint Qualifying session (if present)
                race.sessions.sprint_qualifying?.let {
                    SessionItem(
                        day = it.day,
                        name = "SPRINT QUALIFYING",
                        time = it.time,
                        isPrimary = false,
                        category = category,
                    )
                }
                
                // Practice 3 session (if present)
                race.sessions.practice3?.let {
                    SessionItem(
                        day = it.day,
                        name = "PRACTICE 3",
                        time = it.time,
                        isPrimary = false,
                        category = category,
                    )
                }
                
                // Practice 2 session (if present)
                race.sessions.practice2?.let {
                    SessionItem(
                        day = it.day,
                        name = "PRACTICE 2",
                        time = it.time,
                        isPrimary = false,
                        category = category,
                    )
                }
                
                // Practice 1 session (always present)
                SessionItem(
                    day = race.sessions.practice1.day,
                    name = "PRACTICE 1",
                    time = race.sessions.practice1.time,
                    isPrimary = false,
                    category = category,
                )
            }
        }
    }
}