package com.emi.wac.data.repository

import android.util.Log
import com.emi.wac.data.model.auth.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    suspend fun createUserWithEmail(email: String, password: String, displayName: String): Result<User> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user

            if (user != null) {
                // Update the display name
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()

                user.updateProfile(profileUpdates).await()

                val userModel = User(
                    uid = user.uid,
                    email = user.email,
                    displayName = displayName
                )

                // Save the user to Firestore
                saveUserToFirestore(userModel)
                Result.success(userModel)
            } else {
                Result.failure(Exception("Registration failed: User is null"))
            }
        } catch (e: FirebaseAuthUserCollisionException) {
            // Specific handling for email already in use
            Result.failure(Exception("An account with this email already exists"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithEmail(email: String, password: String): Result<User> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user
            if (user != null) {
                val userModel = User(
                    uid = user.uid,
                    email = user.email,
                    displayName = user.displayName
                )
                // Saves the user on Firestore
                saveUserToFirestore(userModel)
                Result.success(userModel)
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
                displayName = user.displayName,
                photoUrl = user.photoUrl?.toString()
            )
        } else {
            null
        }
    }

    /**
     * Guarda o actualiza la información del usuario en Firestore
     */
    private suspend fun saveUserToFirestore(user: User) {
        user.uid?.let { uid ->
            try {
                firestore.collection("users").document(uid)
                    .set(user)
                    .await()
            } catch (e: Exception) {
                Log.e("AuthRepository", "Error al guardar usuario en Firestore: ${e.message}", e)
            }
        }
    }
}