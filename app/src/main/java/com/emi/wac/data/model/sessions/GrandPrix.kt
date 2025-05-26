package com.emi.wac.data.model.sessions

/**
 * Class representing a Grand Prix.
 *
 * @property gp The name of the Grand Prix
 * @property dates The date of the Grand Prix
 * @property flag The path to the country flag of the Grand Prix
 * @property sessions The list of sessions for the Grand Prix
 * @property circuitImage The image of the circuit for the Grand Prix
 */
data class GrandPrix(
    val gp: String,
    val dates: String,
    val flag: String,
    val sessions: Sessions,
    val circuitImage: String = ""
)


/**
 * Class representing a Championship Schedule.
 *
 * @property schedule The list of Grand Prix in the schedule
 */
data class Schedule(
    val schedule: List<GrandPrix>
)