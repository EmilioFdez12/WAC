package com.emi.wac.data.model.contructor

/**
 * Represents a constructor's standing in the championship.
 *
 * @property team The name of the constructor/team
 * @property points The total points accumulated by the constructor
 * @property position The current position in the championship standings
 */
data class ConstructorStanding(
    val team: String = "",
    val points: String = "",
    val position: String = ""
)