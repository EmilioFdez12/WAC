package com.emi.wac.data.repository

import android.util.Log
import com.emi.wac.data.model.auth.UserPreference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

/**
 * Repository class responsible for managing user preferences in Firestore.
 * Handles operations such as saving and retrieving user preferences, and managing FCM tokens.
 *
 * @property db The Firestore database instance for data operations.
 */
class UserPreferencesRepository(private val db: FirebaseFirestore) {

    private companion object {
        const val TAG = "UserPreferencesRepository"
        const val COLLECTION_NAME = "user_preferences"
        const val PREFERENCES_FIELD = "preferences"
        const val FCM_TOKEN_FIELD = "fcmToken"
    }

    /**
     * Saves user preferences to Firestore.
     * Uses merge option to preserve other fields in the document.
     *
     * @param userId The unique identifier of the user.
     * @param preferences List of user preferences to save.
     */
    fun saveUserPreferences(userId: String, preferences: List<UserPreference>) {
        try {
            val userPrefsMap = hashMapOf(
                PREFERENCES_FIELD to preferences.map { pref ->
                    hashMapOf(
                        "category" to pref.category,
                        "notificationsEnabled" to pref.notificationsEnabled,
                        "favoriteDriver" to pref.favoriteDriver
                    )
                }
            )

            db.collection(COLLECTION_NAME)
                .document(userId)
                .set(userPrefsMap, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d(TAG, "Successfully saved preferences for user $userId")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error saving preferences: ${e.message}", e)
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error saving preferences: ${e.message}", e)
        }
    }

    /**
     * Retrieves user preferences from Firestore.
     *
     * @param userId The unique identifier of the user.
     * @return List of user preferences if found, null otherwise.
     */
    suspend fun getUserPreferences(userId: String): List<UserPreference>? {
        return try {
            val document = db.collection(COLLECTION_NAME)
                .document(userId)
                .get()
                .await()

            if (document.exists()) {
                val preferencesData = document.get(PREFERENCES_FIELD) as? List<Map<String, Any>>
                preferencesData?.map { prefMap ->
                    UserPreference(
                        category = prefMap["category"] as String,
                        notificationsEnabled = prefMap["notificationsEnabled"] as Boolean,
                        favoriteDriver = prefMap["favoriteDriver"] as? String
                    )
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving preferences: ${e.message}", e)
            null
        }
    }

    /**
     * Updates the FCM token for a user in Firestore.
     *
     * @param userId The unique identifier of the user.
     * @param token The new FCM token to store.
     */
    fun updateFcmToken(userId: String, token: String) {
        db.collection(COLLECTION_NAME)
            .document(userId)
            .set(mapOf(FCM_TOKEN_FIELD to token), SetOptions.merge())
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to update FCM token", e)
            }
    }
}