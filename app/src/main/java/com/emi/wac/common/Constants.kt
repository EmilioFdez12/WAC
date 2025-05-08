package com.emi.wac.common

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.emi.wac.BuildConfig
import com.emi.wac.R
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

    const val DOMAIN = "autosport.com"
    const val SORT = "sortby"
    const val LANGUAGE = "en"
    const val NEWS_API_KEY = BuildConfig.NEWS_API_KEY

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

    val LEXENDBLACK = FontFamily(
        Font(R.font.lexend_deca_black)
    )
    val LEXENDREGULAR = FontFamily(
        Font(R.font.lexend_deca_regular)
    )
    val LEXENDBOLD = FontFamily(
        Font(R.font.lexend_deca_bold)
    )

    // Image list
    val backgroundImages = listOf(
        R.drawable.background1,
        R.drawable.background2,
        R.drawable.background3,
        R.drawable.background4,
    )
}