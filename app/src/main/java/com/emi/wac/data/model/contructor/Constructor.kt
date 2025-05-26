package com.emi.wac.data.model.contructor

/**
 * Represents a constructor's standing in the championship.
 *
 * @property car The name of the car associated with the constructor.
 * @property id Unique identifier for the constructor.
 * @property logo URL or path to the constructor's logo image.
 * @property points Total points accumulated by the constructor in the championship.
 * @property position Current position of the constructor in the championship standings.
 * @property team Name of the constructor/team.
 * @property teamId Unique identifier for the team.
 */
data class Constructor(
    val car: String = "",
    val id: Int = 0,
    val logo: String = "",
    val points: Int = 0,
    val position: Int = 0,
    val team: String = "",
    val teamId: Int = 0,
)