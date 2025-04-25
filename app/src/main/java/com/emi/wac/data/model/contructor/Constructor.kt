package com.emi.wac.data.model.contructor

/**
 * Class that represents a F1 team
 *
 * @property teamId The unique identifier of the team
 * @property team The name of the team
 * @property car The path to the car image of the team
 */
data class Constructor(
    val teamId: Int,
    val team: String,
    val car: String,
    val logo: String,
)

/**
 * Class that represents a list of teams.
 */
data class Constructors(
    val constructors: List<Constructor>
)