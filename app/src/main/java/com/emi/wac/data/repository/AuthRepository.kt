package com.emi.wac.data.repository

import android.util.Log
import com.emi.wac.data.model.auth.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

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
                // Saves the user on firestore
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