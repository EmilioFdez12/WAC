package com.emi.wac.data.model.auth

/**
 * Data class that represents a user in the application.
 *
 * @property uid Unique identifier for the user, typically assigned by the authentication provider.
 * @property email Email address associated with the user account.
 * @property displayName Display name of the user, used in the UI.
 * @property photoUrl URL to the user's profile picture, if available.
 * @property isAnonymous Indicates whether the user is logged in anonymously.
 * @property createdAt Timestamp of when the user account was created.
 * @property lastLogin Timestamp of the user's last login.
 */
data class User(
    val uid: String?,
    val email: String?,
    val displayName: String?,
    val photoUrl: String? = null,
    val isAnonymous: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLogin: Long = System.currentTimeMillis()
)