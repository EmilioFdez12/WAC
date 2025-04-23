package com.emi.wac.ui.components.category_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emi.wac.data.model.sessions.GrandPrix
import com.emi.wac.data.repository.RacingRepository
import com.emi.wac.ui.components.category_details.schedule.RaceScheduleCard

@Composable
fun ScheduleComponent(
    modifier: Modifier = Modifier,
    category: String,
    racingRepository: RacingRepository,
    schedule: List<GrandPrix>? = null
) {
    val scheduleData = schedule ?: racingRepository.getSchedule(category)?.schedule

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(scheduleData ?: emptyList()) { grandPrix ->
            RaceScheduleCard(
                modifier = Modifier.animateItem(),
                grandPrix = grandPrix,
                category = category
            )
        }
    }
}