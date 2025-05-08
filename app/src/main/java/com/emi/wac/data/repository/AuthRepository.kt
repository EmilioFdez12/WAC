package com.emi.wac.data.repository

import com.emi.wac.data.model.auth.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(private val firebaseAuth: FirebaseAuth) {

    suspend fun signInWithEmail(email: String, password: String): Result<User> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user
            if (user != null) {
                Result.success(
                    User(
                        uid = user.uid,
                        email = user.email,
                        displayName = user.displayName
                    )
                )
            } else {
                Result.failure(Exception("Email Sign-In failed: User is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    fun getCurrentUser(): User? {
        val user = firebaseAuth.currentUser
        return if (user != null && !user.isAnonymous) {
            User(
                uid = user.uid,
                email = user.email,
                displayName = user.displayName
            )
        } else {
            null
        }
    }
}