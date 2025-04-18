package com.emi.wac.data.repository

import com.emi.wac.data.model.contructor.ConstructorStanding
import com.emi.wac.data.model.drivers.DriverStanding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Repository for accessing and managing racing standings data using Firebase Firestore.
 *
 * @param db FirebaseFirestore instance for database operations.
 */
class StandingsRepository(private val db: FirebaseFirestore) {

    suspend fun getDriverStandings(category: String): Result<List<DriverStanding>> {
        try {
            val document = db.collection("${category}_standings").document("latest").get().await()
            val rawData = document.data
    
            // Log raw data for debugging
            android.util.Log.d("StandingsRepository", "Raw data: $rawData")
    
            if (rawData != null) {
                // Extract the standings list from the "data" key
                val standingsData = rawData["data"]
                if (standingsData is List<*>) {
                    val standingsList = standingsData.filterIsInstance<Map<*, *>>()
                    val standings = standingsList.map { item ->
                        // Handle both "driver" and "name" fields for different categories
                        val driverName = (item["driver"] as? String) ?: (item["name"] as? String) ?: ""
                        val points = (item["points"] as? String) ?: ""
                        val position = (item["position"] as? String) ?: ""
                        val team = (item["team"] as? String) ?: ""
                        
                        DriverStanding(
                            driver = driverName,
                            points = points,
                            position = position,
                            team = team
                        )
                    }
                    return Result.success(standings)
                }
                return Result.failure(Exception("Invalid data format"))
            }
            return Result.failure(Exception("No data found"))
        } catch (e: Exception) {
            android.util.Log.e("StandingsRepository", "Error fetching standings: ${e.message}", e)
            return Result.failure(e)
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
}