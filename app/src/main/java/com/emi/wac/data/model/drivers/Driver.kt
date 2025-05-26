package com.emi.wac.data.model.drivers

/**
 * Represents a driver's standing in the championship.
 *
 * @property id Unique identifier for the driver.
 * @property name The name of the driver.
 * @property number The racing number assigned to the driver.
 * @property points Total points accumulated by the driver in the championship.
 * @property portrait URL or path to the driver's portrait image.
 * @property position Current position of the driver in the championship standings.
 * @property team Name of the team the driver belongs to.
 * @property teamCar The car model associated with the driver's team.
 * @property teamId Unique identifier for the team.
 * @property teamLogo URL or path to the team's logo image.
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