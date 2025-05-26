package com.emi.wac.ui.theme

import androidx.compose.ui.graphics.Color
import com.emi.wac.common.Constants.CATEGORY_INDYCAR
import com.emi.wac.common.Constants.CATEGORY_MOTOGP

// Colores principales por categoría
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
val SoftBlue = Color(0x4D154DA4)
val HardBlue = Color(0xFF0065FF)

// Colores base de la aplicación
val PrimaryWhite = Color(0xFFFFFFFF)
val PrimaryBlack = Color(0xFF303030)
val SecondaryGray = Color(0xFF757575)
val LightGray = Color(0xFFE0E0E0)
val DarkGray = Color(0xFF424242)

// Colores de estado
val SuccessGreen = Color(0xFF4CAF50)
val WarningOrange = Color(0xFFFF9800)
val ErrorRed = Color(0xFFF44336)
val InfoBlue = Color(0xFF2196F3)

// Gradientes comunes
val DefaultCardGradient = listOf(
    Color(0xFF404040),
    Color(0xFF151515)
)

val DarkCardGradient = listOf(
    Color(0xFF2C2C2C),
    Color(0xFF1A1A1A)
)

val LightCardGradient = listOf(
    Color(0xFFF5F5F5),
    Color(0xFFE8E8E8)
)

/**
 * Clase de datos para encapsular todos los colores de una categoría
 */
data class CategoryColors(
    val primary: Color,
    val soft: Color,
    val hard: Color,
    val gradient: List<Color>
)

/**
 * Obtains all colors associated with a category
 * @param category Category to obtain colors for
 * @return CategoryColors with all category colors
 */
fun getCategoryColors(category: String): CategoryColors {
    return when (category) {
        CATEGORY_MOTOGP -> CategoryColors(
            primary = PrimaryOrange,
            soft = SoftOrange,
            hard = HardOrange,
            gradient = listOf(PrimaryOrange, HardOrange)
        )
        CATEGORY_INDYCAR -> CategoryColors(
            primary = PrimaryGreen,
            soft = SoftGreen,
            hard = HardGreen,
            gradient = listOf(PrimaryGreen, HardGreen)
        )
        else -> CategoryColors(
            primary = PrimaryRed,
            soft = SoftRed,
            hard = HardRed,
            gradient = listOf(PrimaryRed, HardRed)
        )
    }
}


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