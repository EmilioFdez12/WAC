package com.emi.wac.data.model.drivers

/**
 * Class representing a driver from any category
 *
 * @property id The unique identifier of the driver
 * @property name The name of the driver
 * @property teamId The unique identifier of the driver's team
 * @property team The name of the driver's team
 * @property number The driver's number
 * @property portrait The path to the driver's portrait image
 */
data class Driver(
    val id: Int,
    val name: String,
    val teamId: Int,
    val team: String,
    val number: Int,
    val portrait: String
)

/**
 * Class that represents a list of drivers from a category
 */
data class Drivers(
    val drivers: List<Driver>
)