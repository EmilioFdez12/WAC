package com.emi.wac.common

import com.emi.wac.data.model.RaceInfo

/**
 * Constants used throughout the app.
 */
object Constants {
    val LOADING_RACE_INFO = RaceInfo(
        gpName = "Loading...",
        flagPath = "",
        timeRemaining = "",
        leaderImagePath = "",
        leaderName = "Fernando Alonso"
    )

    const val HOME = "home"
    const val CAT_DETAILS = "category_details"

    const val CATEGORY_MOTOGP = "motogp"
    const val BCKG_IMG = "file:///android_asset/background.webp"
}