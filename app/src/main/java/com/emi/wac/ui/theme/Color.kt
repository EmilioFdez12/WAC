package com.emi.wac.ui.theme

import androidx.compose.ui.graphics.Color
import com.emi.wac.common.Constants.CATEGORY_INDYCAR
import com.emi.wac.common.Constants.CATEGORY_MOTOGP

val PrimaryRed = Color(0xFFC62828)
val SoftRed = Color(0x4DC62828)
val HardRed = Color(0xFFFF0000)
val PrimaryOrange = Color(0xFFEF7810)
val SoftOrange = Color(0x4DEF7810)
val HardOrange = Color(0xFFFF7700)
val PrimaryGreen = Color(0xFF15A434)
val SoftGreen = Color(0x4D15A434)
val HardGreen = Color(0xFF00E134)
val PrimaryBlue = Color(0xFF154DA4)
val SoftBlue =  Color(0x4D154DA4)
val HardBlue =  Color(0xFF0065FF)
val PrimaryWhite = Color(0xFFFFFFFF)
val PrimaryBlack = Color(0xFF303030)


/**
 * Returns the primary color based on the racing category
 * @param category The racing category (f1 or motogp)
 * @return The appropriate primary color
 */
fun getPrimaryColorForCategory(category: String): Color {
    return when (category) {
        CATEGORY_MOTOGP -> PrimaryOrange
        CATEGORY_INDYCAR -> PrimaryGreen
        else -> PrimaryRed
    }
}

/**
 * Returns the soft color based on the racing category
 * @param category The racing category (f1 or motogp)
 * @return The appropriate soft color
 */
fun getSoftColorForCategory(category: String): Color {
    return when (category) {
        CATEGORY_MOTOGP -> SoftOrange
        CATEGORY_INDYCAR -> SoftGreen
        else -> SoftRed
    }
}

/**
 * Returns the hard color based on the racing category
 * @param category The racing category (f1 or motogp)
 * @return The appropriate hard color
 */
fun getHardColorForCategory(category: String): Color {
    return when (category) {
        CATEGORY_MOTOGP -> HardOrange
        CATEGORY_INDYCAR -> HardGreen
        else -> HardRed
    }
}