package com.emi.wac.data.repository

import android.util.Log
import com.emi.wac.data.model.contructor.Constructor
import com.emi.wac.data.model.drivers.Driver
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Repository para acceder y gestionar datos de clasificaciones de carreras usando Firebase Firestore.
 *
 * @param db Instancia de FirebaseFirestore para operaciones de base de datos.
 */
class StandingsRepository(private val db: FirebaseFirestore) {

    private val tag = "StandingsRepository"

    /**
     * Obtiene las clasificaciones actuales de pilotos en una categoría específica.
     *
     * @param category La categoría de carreras (ej. "f1", "motogp").
     * @return Result con una lista de Driver si es exitoso, o un error si falla.
     */
    suspend fun getDriverStandings(category: String): Result<List<Driver>> {
        return try {
            val standings = mutableListOf<Driver>()
            val documents = db.collection("${category}_standings").get().await()

            // Filtramos y ordenamos documentos que siguen el patrón "driver_XX"
            val driverDocs = documents.documents
                .filter { it.id.startsWith("driver_") }
                .sortedBy {
                    val positionStr = it.id.substringAfter("driver_")
                    positionStr.toIntOrNull() ?: Int.MAX_VALUE
                }

            for (doc in driverDocs) {
                val data = doc.data
                if (data != null) {
                    val driver = extractDriverFromData(data)
                    standings.add(driver)
                }
            }

            if (standings.isNotEmpty()) {
                Result.success(standings)
            } else {
                Result.failure(Exception("No se encontraron datos de pilotos"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error obteniendo clasificaciones: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Obtiene el líder actual del campeonato para una categoría específica (pilotos).
     *
     * @param category La categoría de carreras (ej. "f1", "motogp").
     * @return Result con Driver si es exitoso, o un error si falla.
     */
    suspend fun getLeaderDriver(category: String): Result<Driver> {
        return try {
            // Obtenemos directamente el documento "driver_01" que corresponde al líder
            val document = db.collection("${category}_standings").document("driver_01").get().await()

            // Verificamos si el documento existe y tiene datos
            if (document.exists() && document.data != null) {
                val driverData = document.data
                if (driverData != null) {
                    val driver = extractDriverFromData(driverData)
                    Result.success(driver)
                } else {
                    Result.failure(Exception("Datos de piloto vacíos para $category"))
                }
            } else {
                Result.failure(Exception("No se encontraron datos de piloto para $category"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error obteniendo driver_01: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Obtiene el líder actual del campeonato para una categoría específica (constructores).
     *
     * @param category La categoría de carreras (ej. "f1", "motogp").
     * @return Result con Constructor si es exitoso, o un error si falla.
     */
    suspend fun getLeaderConstructor(category: String): Result<Constructor> {
        return try {
            // Obtenemos directamente el documento "constructor_01" que corresponde al líder
            val document = db.collection("${category}_constructors")
                .document("constructor_01").get().await()

            // Verificamos si el documento existe y tiene datos
            if (document.exists() && document.data != null) {
                val constructorData = document.data
                if (constructorData != null) {
                    val constructor = extractConstructorFromData(constructorData)
                    Result.success(constructor)
                } else {
                    Result.failure(Exception("Datos de constructor vacíos para $category"))
                }
            } else {
                Result.failure(Exception("No se encontraron datos de constructor para $category"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error obteniendo constructor_01: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Obtiene las clasificaciones actuales de constructores en una categoría específica.
     *
     * @param category La categoría de carreras (ej. "f1", "motogp").
     * @return Result con una lista de Constructor si es exitoso, o un error si falla.
     */
    suspend fun getConstructorStandings(category: String): Result<List<Constructor>> {
        return try {
            val standings = mutableListOf<Constructor>()
            val documents = db.collection("${category}_constructors").get().await()

            // Filtramos y ordenamos documentos que siguen el patrón "constructor_XX"
            val constructorDocs = documents.documents
                .filter { it.id.startsWith("constructor_") }
                .sortedBy {
                    val positionStr = it.id.substringAfter("constructor_")
                    positionStr.toIntOrNull() ?: Int.MAX_VALUE
                }

            for (doc in constructorDocs) {
                val data = doc.data
                if (data != null) {
                    val constructor = extractConstructorFromData(data)
                    standings.add(constructor)
                }
            }

            if (standings.isNotEmpty()) {
                Result.success(standings)
            } else {
                Result.failure(Exception("No se encontraron datos de constructores"))
            }
        } catch (e: Exception) {
            // Verificamos si la excepción es por cancelación de corrutina
            if (e is kotlinx.coroutines.CancellationException) {
                // Simplemente propagamos la excepción de cancelación sin registrarla como error
                throw e
            }
            Log.e(tag, "Error obteniendo clasificaciones de constructores: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Extrae un objeto Driver a partir de un mapa de datos.
     * Maneja diferentes tipos de datos numéricos que pueden venir de Firebase.
     */
    private fun extractDriverFromData(data: Map<String, Any>): Driver {
        val id = when (val value = data["id"]) {
            is Long -> value.toInt()
            is Int -> value
            is Double -> value.toInt()
            else -> 0
        }

        val driverName = (data["name"] as? String) ?: ""

        val number = when (val value = data["number"]) {
            is Long -> value.toInt()
            is Int -> value
            is Double -> value.toInt()
            else -> 0
        }

        val points = when (val value = data["points"]) {
            is Long -> value.toInt()
            is Int -> value
            is Double -> value.toInt()
            else -> 0
        }

        val portrait = (data["portrait"] as? String) ?: ""

        val position = when (val value = data["position"]) {
            is Long -> value.toInt()
            is Int -> value
            is Double -> value.toInt()
            else -> 0
        }

        val team = (data["team"] as? String) ?: ""
        val teamCar = (data["teamCar"] as? String) ?: ""

        val teamId = when (val value = data["teamId"]) {
            is Long -> value.toInt()
            is Int -> value
            is Double -> value.toInt()
            else -> 0
        }

        val teamLogo = (data["teamLogo"] as? String) ?: ""

        return Driver(
            id = id,
            name = driverName.trim(),
            number = number,
            points = points,
            portrait = portrait,
            position = position,
            team = team,
            teamCar = teamCar,
            teamId = teamId,
            teamLogo = teamLogo
        )
    }

    /**
     * Extrae un objeto Constructor a partir de un mapa de datos.
     * Maneja diferentes tipos de datos que pueden venir de Firebase.
     */
    private fun extractConstructorFromData(data: Map<String, Any>): Constructor {
        val car = (data["car"] as? String) ?: ""

        val id = when (val value = data["id"]) {
            is Long -> value.toInt()
            is Int -> value
            is Double -> value.toInt()
            else -> 0
        }

        val logo = (data["logo"] as? String) ?: ""
        val points = when (val value = data["points"]) {
            is Long -> value.toInt()
            is Int -> value
            is Double -> value.toInt()
            else -> 0
        }
        val position = when (val value = data["position"]) {
            is Long -> value.toInt()
            is Int -> value
            is Double -> value.toInt()
            else -> 0
        }
        val team = (data["team"] as? String) ?: ""

        val teamId = when (val value = data["teamId"]) {
            is Long -> value.toInt()
            is Int -> value
            is Double -> value.toInt()
            else -> 0
        }

        return Constructor(
            car = car,
            id = id,
            logo = logo,
            points = points,
            position = position,
            team = team,
            teamId = teamId
        )
    }
}