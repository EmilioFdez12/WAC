package com.emi.wac.data.model.auth

data class User(
    val uid: String?,
    val email: String?,
    val displayName: String?,
    val photoUrl: String? = null,
    val isAnonymous: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLogin: Long = System.currentTimeMillis()
)