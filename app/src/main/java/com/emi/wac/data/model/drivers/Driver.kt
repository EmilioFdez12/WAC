package com.emi.wac.data.model.drivers

/**
 * Clase que representa un piloto de cualquier categoría
 *
 * @property id El identificador único del piloto.
 * @property name El nombre del piloto.
 * @property teamId El identificador único del equipo al que pertenece el piloto.
 * @property team El nombre del equipo al que pertenece el piloto.
 * @property number El número del piloto.
 * @property portrait La ruta de la imagen del piloto.
 *
 */
data class Driver(
    val id: Int,
    val name: String,
    val teamId: Int,
    val team: String,
    val number: Int,
    val portrait: String
)

data class Drivers(
    val pilotos: List<Driver>
)