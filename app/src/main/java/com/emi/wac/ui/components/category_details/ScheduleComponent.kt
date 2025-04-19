package com.emi.wac.ui.components.category_details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.emi.wac.data.model.sessions.GrandPrix
import com.emi.wac.data.repository.RacingRepository
import com.emi.wac.ui.components.category_details.schedule.RaceScheduleCard

@Composable
fun ScheduleComponent(
    modifier: Modifier = Modifier,
    category: String
) {
    val context = LocalContext.current
    val racingRepository = remember { RacingRepository(context) }
    var schedule by remember { mutableStateOf<List<GrandPrix>?>(null) }

    LaunchedEffect(category) {
        val scheduleData = racingRepository.getSchedule(category)
        schedule = scheduleData?.schedule
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(schedule ?: emptyList()) { grandPrix ->
            RaceScheduleCard(
                grandPrix = grandPrix,
                category = category
            )
        }
    }
}