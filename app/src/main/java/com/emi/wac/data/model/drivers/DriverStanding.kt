package com.emi.wac.data.model.drivers

/**
 * Represents a driver's standing in the championship.
 *
 * @property driver The name of the driver
 * @property points The total points accumulated by the driver
 * @property position The current position in the championship standings
 * @property team The name of the team the driver belongs to
 */
data class DriverStanding(
    val driver: String = "",
    val points: String = "",
    val position: String = "",
    val team: String = ""
)