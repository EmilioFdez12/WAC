package com.emi.wac.data.model.sessions

/**
 * Clase que representa un Calendario
 *
 * @property schedule La lista de Grand Prix del calendario.
 */
data class Schedule(
    val schedule: List<GrandPrix>
)