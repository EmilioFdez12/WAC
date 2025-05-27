package com.emi.wac.data.repository

import android.util.Log
import com.emi.wac.data.model.contructor.Constructor
import com.emi.wac.data.model.drivers.Driver
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Repository for accessing and managing racing standings data using Firebase Firestore.
 * Provides methods to fetch driver and constructor standings with error handling.
 *
 * @param db FirebaseFirestore instance for database operations
 */
class StandingsRepository(private val db: FirebaseFirestore) {
    private val tag = "StandingsRepository"

    /**
     * Safely converts an Any? value to an Int. Handles Long, Int, and Double types.
     * Returns 0 for null or other unsupported types.
     *
     * @param value The value to convert.
     * @return The converted Int, or 0 if conversion fails.
     */
    private fun safeIntConversion(value: Any?): Int = when (value) {
        is Long -> value.toInt()
        is Int -> value
        is Double -> value.toInt()
        else -> 0
    }

    /**
     * Fetches current driver standings for a specific racing category.
     *
     * @param category Racing category (e.g., "f1", "motogp").
     * @return Result containing List<Driver> on success or Exception on failure.
     */
    suspend fun getDriverStandings(category: String): Result<List<Driver>> = try {
        val documents = db.collection("${category}_standings").get().await()

        val standings = documents.documents
            .filter { it.id.startsWith("driver_") }
            .sortedBy { it.id.substringAfter("driver_").toIntOrNull() ?: Int.MAX_VALUE }
            .mapNotNull { doc -> doc.data?.let { extractDriverFromData(it) } }

        if (standings.isNotEmpty()) Result.success(standings)
        else Result.failure(Exception("No driver data found"))
    } catch (e: Exception) {
        Log.e(tag, "Error getting driver standings: ${e.message}", e)
        Result.failure(e)
    }

    /**
     * Fetches the current championship leader driver for a specific racing category.
     *
     * @param category Racing category (e.g., "f1", "motogp").
     * @return Result containing Driver on success or Exception on failure.
     */
    suspend fun getLeaderDriver(category: String): Result<Driver> = try {
        val document = db.collection("${category}_standings").document("driver_01").get().await()

        if (document.exists() && document.data != null) {
            document.data?.let { driverData ->
                Result.success(extractDriverFromData(driverData))
            } ?: Result.failure(Exception("Empty driver data for $category"))
        } else {
            Result.failure(Exception("No driver data found for $category"))
        }
    } catch (e: Exception) {
        Log.e(tag, "Error getting driver_01: ${e.message}", e)
        Result.failure(e)
    }

    /**
     * Fetches the current championship leader constructor for a specific racing category.
     *
     * @param category Racing category (e.g., "f1", "motogp").
     * @return Result containing Constructor on success or Exception on failure.
     */
    suspend fun getLeaderConstructor(category: String): Result<Constructor> = try {
        val document = db.collection("${category}_constructors")
            .document("constructor_01").get().await()

        if (document.exists() && document.data != null) {
            document.data?.let { constructorData ->
                Result.success(extractConstructorFromData(constructorData))
            } ?: Result.failure(Exception("Empty constructor data for $category"))
        } else {
            Result.failure(Exception("No constructor data found for $category"))
        }
    } catch (e: Exception) {
        Log.e(tag, "Error getting constructor_01: ${e.message}", e)
        Result.failure(e)
    }

    /**
     * Fetches current constructor standings for a specific racing category.
     *
     * @param category Racing category (e.g., "f1", "motogp").
     * @return Result containing List<Constructor> on success or Exception on failure.
     */
    suspend fun getConstructorStandings(category: String): Result<List<Constructor>> = try {
        val documents = db.collection("${category}_constructors").get().await()

        val standings = documents.documents
            .filter { it.id.startsWith("constructor_") }
            .sortedBy { it.id.substringAfter("constructor_").toIntOrNull() ?: Int.MAX_VALUE }
            .mapNotNull { doc -> doc.data?.let { extractConstructorFromData(it) } }

        if (standings.isNotEmpty()) Result.success(standings)
        else Result.failure(Exception("No constructor data found"))
    } catch (e: Exception) {
        // Propagate coroutine cancellation exceptions without logging as errors
        if (e is kotlinx.coroutines.CancellationException) throw e
        Log.e(tag, "Error getting constructor standings: ${e.message}", e)
        Result.failure(e)
    }

    /**
     * Extracts a Driver object from a map of data.
     * Handles different numeric data types that may come from Firebase.
     *
     * @param data The map containing driver data.
     * @return A Driver object.
     */
    private fun extractDriverFromData(data: Map<String, Any>): Driver {
        return Driver(
            id = safeIntConversion(data["id"]),
            name = (data["name"] as? String)?.trim() ?: "",
            number = safeIntConversion(data["number"]),
            points = safeIntConversion(data["points"]),
            portrait = (data["portrait"] as? String) ?: "",
            position = safeIntConversion(data["position"]),
            team = (data["team"] as? String) ?: "",
            teamCar = (data["teamCar"] as? String) ?: "",
            teamId = safeIntConversion(data["teamId"]),
            teamLogo = (data["teamLogo"] as? String) ?: ""
        )
    }

    /**
     * Extracts a Constructor object from a map of data.
     * Handles different numeric data types that may come from Firebase.
     *
     * @param data The map containing constructor data.
     * @return A Constructor object.
     */
    private fun extractConstructorFromData(data: Map<String, Any>): Constructor {
        return Constructor(
            car = (data["car"] as? String) ?: "",
            id = safeIntConversion(data["id"]),
            logo = (data["logo"] as? String) ?: "",
            points = safeIntConversion(data["points"]),
            position = safeIntConversion(data["position"]),
            team = (data["team"] as? String) ?: "",
            teamId = safeIntConversion(data["teamId"])
        )
    }
}