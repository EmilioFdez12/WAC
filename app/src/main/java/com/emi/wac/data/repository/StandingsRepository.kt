package com.emi.wac.data.repository

import com.emi.wac.data.model.drivers.DriverStanding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Repository for accessing and managing racing standings data. Uses Firebase Firestore as the data
 * source.
 *
 * @property db FirebaseFirestore instance for database operations
 */
class StandingsRepository(private val db: FirebaseFirestore) {

    /**
     * Retrieves the current championship leader for a specific racing category.
     *
     * @param category The racing category (e.g., "f1", "motogp")
     * @return Result containing DriverStanding if successful, or failure with exception
     */
    suspend fun getLeaderDriver(category: String): Result<DriverStanding> {
        return try {
            // Fetch latest standings document from Firestore
            val document = db.collection("${category}_standings").document("latest").get().await()

            // Verify if data is a list of maps <String, String>
            val data =
                when (val rawData = document.get("data")) {
                    is List<*> -> rawData.filterIsInstance<Map<String, String>>()
                    else -> null
                }

            // Extract first driver's data
            val firstDriver = data?.firstOrNull()

            if (firstDriver != null) {
                // Extract driver name from "driver" or "name" field
                val driverName = (firstDriver["driver"] ?: firstDriver["name"] ?: "").trim()
                // Create DriverStanding object and return success result
                Result.success(
                    DriverStanding(
                        driver = driverName,
                        points = firstDriver["points"] ?: "",
                        position = firstDriver["position"] ?: "",
                        team = firstDriver["team"] ?: ""
                    )
                )
            } else {
                Result.failure(Exception("No driver data found for $category"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
