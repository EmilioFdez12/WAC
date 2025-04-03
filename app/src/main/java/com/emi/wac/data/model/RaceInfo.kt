package com.emi.wac.data.model

/**
 * Clase con la informacion general del siguiente GrandPrix.
 *
 * @property gpName Nombre del gp
 * @property flagPath Ruta de la imagen de la bandera del siguiente GrandPrix
 * @property timeRemaining Tiempo restante para el siguiente GrandPrix
 * @property leaderImagePath Ruta de la imagen del piloto lider del siguiente GrandPrix
 * @property leaderName Nombre del piloto lider del siguiente GrandPrix
 *
 */
data class RaceInfo(
    val gpName: String,
    val flagPath: String,
    val timeRemaining: String,
    val leaderImagePath: String,
    val leaderName: String
)