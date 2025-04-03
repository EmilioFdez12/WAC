package com.emi.wac.data.model.sessions

/**
 * Clase que representa un Grand Prix.
 *
 * @property gp El nombre del Grand Prix.
 * @property dates La fecha del Grand Prix.
 * @property flag La ruta de la bandera del país del Grand Prix.
 * @property sessions La lista de sesiones del Grand Prix.
 *
 */
data class GrandPrix(
    val gp: String,
    val dates: String,
    val flag: String,
    val sessions: Sessions
)