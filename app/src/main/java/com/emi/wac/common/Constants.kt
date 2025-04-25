package com.emi.wac.common

import com.emi.wac.data.model.RaceInfo
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Constants used throughout the app.
 */
object Constants {
    val LOADING_RACE_INFO = RaceInfo(
        gpName = "Loading...",
        flagPath = "",
        timeRemaining = "",
        leaderImagePath = "",
        leaderName = ""
    )

    const val HOME = "home"
    const val CAT_DETAILS = "category_details"
    const val ASSETS = "file:///android_asset"

    const val CATEGORY_MOTOGP = "motogp"
    const val CATEGORY_F1 = "f1"
    const val BCKG_IMG = "file:///android_asset/background.webp"

    const val RACE_DURATION = 2 * 60 * 60 * 1000L // 2 hours
    const val SESSION_DURATION = 60 * 60 * 1000L // 1 hour
    val DATE_FORMAT = SimpleDateFormat("dd MMM", Locale.ENGLISH)
    val SESSION_TYPES = listOf(
        "practice1" to "FP 1",
        "practice2" to "FP 2",
        "practice3" to "FP 3",
        "sprintQualifying" to "Sprint Qualy",
        "sprint" to "Sprint",
        "qualifying" to "Qualy",
        "race" to "Race"
    )
}