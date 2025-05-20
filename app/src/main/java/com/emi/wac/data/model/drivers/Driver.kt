package com.emi.wac.data.model.drivers

/**
 * Represents a driver's standing in the championship.
 *
 * @property name The name of the driver
 * @property points The total points accumulated by the driver
 * @property position The current position in the championship standings
 * @property team The name of the team the driver belongs to
 */
data class Driver(
    val id: Int = 0,
    val name: String = "",
    val number: Int = 0,
    val points: Int = 0,
    val portrait: String = "",
    val position: Int = 0,
    val team: String = "",
    val teamCar: String = "",
    val teamId: Int = 0,
    val teamLogo: String = "",
)

data class Drivers(
    val drivers: List<Driver>
)