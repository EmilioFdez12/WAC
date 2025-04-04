package com.emi.wac.data.model.sessions

/**
 * Class representing a Championship Schedule.
 *
 * @property schedule The list of Grand Prix in the schedule
 */
data class Schedule(
    val schedule: List<GrandPrix>
)