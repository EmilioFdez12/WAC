package com.emi.wac.data.model.contructor

/**
 * Represents a constructor's standing in the championship.
 *
 * @property team The name of the constructor/team
 * @property points The total points accumulated by the constructor
 * @property position The current position in the championship standings
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

data class Constructors(
    val constructors: List<Constructor>
)