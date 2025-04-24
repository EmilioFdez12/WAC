package com.emi.wac.data.model

/**
 * Class containing general information about the next Grand Prix.
 *
 * @property gpName Name of the Grand Prix
 * @property flagPath Path to the flag image of the next Grand Prix
 * @property timeRemaining Time remaining until the next Grand Prix
 * @property leaderImagePath Path to the image of the championship leader
 * @property leaderName Name of the championship leader
 * @property sessionName Name of the session
 *
 */
data class RaceInfo(
    val gpName: String,
    val flagPath: String,
    val timeRemaining: String,
    val leaderImagePath: String,
    val leaderName: String,
    val sessionName: String = "Race"
)