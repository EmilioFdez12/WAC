package com.emi.wac.data.repository

import android.util.Log
import com.emi.wac.data.model.auth.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Repository class responsible for handling authentication-related operations.
 * This class provides methods to create a new user account, sign in with email and password
 *
 * @param firebaseAuth The Firebase Authentication instance for user authentication.
 * @param firestore The Firebase Firestore instance for user data storage.
 */
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    /**
     * Creates a new user account with the provided email, password, and display name.
     * Updates the user's profile with the display name and saves the user information to Firestore.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @param displayName The display name to be set for the user.
     * @return Result containing the User object if successful, or an error if failed.
     */
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
        } catch (_: FirebaseAuthUserCollisionException) {
            // Specific handling for email already in use
            Result.failure(Exception("An account with this email already exists"))
        } catch (e: Exception) {
            handleFirebaseException(e)
        }
    }

    /**
     * Signs in a user with the provided email and password.
     * Saves the user information to Firestore if sign-in is successful.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @return Result containing the User object if successful, or an error if failed.
     */
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
            handleFirebaseException(e)
        }
    }

    /**
     * Signs out the currently authenticated user.
     */
    fun signOut() {
        firebaseAuth.signOut()
    }

    /**
     * Retrieves the currently authenticated user.
     *
     * @return User object if a non-anonymous user is authenticated, null otherwise.
     */
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

    // Saves or updates the user information in Firestore.
    private suspend fun saveUserToFirestore(user: User) {
        // Get the user's UID
        user.uid?.let { uid ->
            try {
                firestore.collection("users").document(uid)
                    .set(user)
                    .await()
            } catch (e: Exception) {
                Log.e("AuthRepository", "Error while trying to save user to Firestore: ${e.message}", e)
            }
        }
    }

    // Handles Firebase exceptions
    private fun handleFirebaseException(e: Exception): Result<User> {
        return when (e) {
            is FirebaseAuthUserCollisionException -> Result.failure(Exception("An account with this email already exists"))
            else -> Result.failure(e)
        }
    }
}