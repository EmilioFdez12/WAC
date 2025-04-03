package com.emi.wac.common

import com.emi.wac.data.model.RaceInfo

object Constants {
    val LOADING_RACE_INFO = RaceInfo(
        gpName = "Loading...",
        flagPath = "",
        timeRemaining = "",
        leaderImagePath = "",
        leaderName = ""
    )

        // Time constants in milliseconds
        const val MILLISECONDS_PER_DAY = 24 * 60 * 60 * 1000L
        const val MILLISECONDS_PER_HOUR = 60 * 60 * 1000L
        const val MILLISECONDS_PER_MINUTE = 60 * 1000L
}