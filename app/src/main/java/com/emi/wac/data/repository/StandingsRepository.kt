package com.emi.wac.data.repository

import com.emi.wac.data.model.contructor.ConstructorStanding
import com.emi.wac.data.model.drivers.DriverStanding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Repository for accessing and managing racing standings data using Firebase Firestore as the data source.
 *
 * @param db FirebaseFirestore instance for database operations.
 */
class StandingsRepository(private val db: FirebaseFirestore) {

    /**
     * Fetches raw data from the "latest" document in a Firestore collection.
     *
     * @param collectionName Name of the collection (e.g., "f1_standings").
     * @return The first map of data if available, null if it fails or no data is found.
     */
    private suspend fun getFirstStandingFromFirestore(collectionName: String): Map<String, String>? {
        try {
            val document = db.collection(collectionName).document("latest").get().await()
            // Get the "data" field from the document
            val rawData = document.get("data")
            // Check if the data is a list and filter it to ensure it's a list of maps
            if (rawData is List<*>) {
                val data = rawData.filterIsInstance<Map<String, String>>()
                return data.firstOrNull()
            }
            return null
        } catch (_: Exception) {
            return null
        }
    }

    /**
     * Retrieves the current championship leader for a specific racing category (drivers).
     *
     * @param category The racing category (e.g., "f1", "motogp").
     * @return Result containing DriverStanding if successful, or failure with an exception.
     */
    suspend fun getLeaderDriver(category: String): Result<DriverStanding> {
        // Fetch the first driver's data from Firestore
        val firstDriver = getFirstStandingFromFirestore("${category}_standings")
        // Check if data was retrieved successfully
        return if (firstDriver != null) {
            // Extract the driver name, trying "driver" first, then "name" as fallback
            val driverName = firstDriver["driver"] ?: firstDriver["name"] ?: ""
            // Create a DriverStanding object
            Result.success(
                DriverStanding(
                    driver = driverName.trim(),
                    points = firstDriver["points"] ?: "",
                    position = firstDriver["position"] ?: "",
                    team = firstDriver["team"] ?: ""
                )
            )
        } else {
            Result.failure(Exception("No driver data found for $category"))
        }
    }

    /**
     * Retrieves the current championship leader for a specific racing category (constructors).
     *
     * @param category The racing category (e.g., "f1", "motogp").
     * @return Result containing ConstructorStanding if successful, or failure with an exception.
     */
    suspend fun getLeaderConstructor(category: String): Result<ConstructorStanding> {
        // Fetch the first constructors data from Firestore
        val firstTeam = getFirstStandingFromFirestore("${category}_constructors_standings")
        // Check if data was retrieved successfully
        return if (firstTeam != null) {
            // Create a ConstructorStanding object
            Result.success(
                ConstructorStanding(
                    team = firstTeam["team"] ?: "",
                    points = firstTeam["points"] ?: "",
                    position = firstTeam["position"] ?: ""
                )
            )
        } else {
            Result.failure(Exception("No constructor data found for $category"))
        }
    }
}