package com.emi.wac.data.model.auth

/**
 * Data class representing a user's preferences for a specific category.
 *
 * @property category The motorsport category (e.g., F1, MotoGP).
 * @property notificationsEnabled Whether notifications are enabled for this category.
 * @property favoriteDriver The user's favorite driver in this category (optional).
 */
data class UserPreference(
    val category: String,
    val notificationsEnabled: Boolean = false,
    val favoriteDriver: String? = null
)
