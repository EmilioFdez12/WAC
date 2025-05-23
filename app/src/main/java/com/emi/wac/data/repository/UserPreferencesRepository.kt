package com.emi.wac.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class UserPreferencesRepository(private val db: FirebaseFirestore) {
    
    private val tag = "UserPreferencesRepository"
    
    data class UserPreference(
        val category: String,
        val notificationsEnabled: Boolean = false,
        val favoriteDriver: String? = null
    )
    
    /**
     * Guarda las preferencias del usuario en Firestore
     */
    fun saveUserPreferences(userId: String, preferences: List<UserPreference>) {
        try {
            val userPrefsMap = hashMapOf(
                "preferences" to preferences.map { pref ->
                    hashMapOf(
                        "category" to pref.category,
                        "notificationsEnabled" to pref.notificationsEnabled,
                        "favoriteDriver" to pref.favoriteDriver
                    )
                }
            )
            
            db.collection("user_preferences")
                .document(userId)
                .set(userPrefsMap, SetOptions.merge()) // Usar merge para mantener otros campos
                .addOnSuccessListener {
                    Log.d(tag, "Preferencias guardadas correctamente para el usuario $userId")
                }
                .addOnFailureListener { e ->
                    Log.e(tag, "Error al guardar preferencias: ${e.message}", e)
                }
        } catch (e: Exception) {
            Log.e(tag, "Error al guardar preferencias: ${e.message}", e)
        }
    }
    
    /**
     * Obtiene las preferencias del usuario desde Firestore
     */
    suspend fun getUserPreferences(userId: String): List<UserPreference>? {
        return try {
            val document = db.collection("user_preferences")
                .document(userId)
                .get()
                .await()
            
            if (document.exists()) {
                val preferencesData = document.get("preferences") as? List<Map<String, Any>>
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
            Log.e(tag, "Error al obtener preferencias: ${e.message}", e)
            null
        }
    }

    fun updateFcmToken(userId: String, token: String) {
        db.collection("user_preferences")
            .document(userId)
            .set(mapOf("fcmToken" to token), SetOptions.merge())
            .addOnFailureListener { Log.e("UserPreferences", "Failed to update FCM token", it) }
    }
}