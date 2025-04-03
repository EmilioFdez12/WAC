package com.emi.wac.data.model.sessions

/**
 * Clase que representa las sesiones de un Grand Prix.
 *
 * @property practice1 La primera sesión de la carrera.
 * @property practice2 La segunda sesión de la carrera.
 * @property practice3 La tercera sesión de la carrera.
 * @property qualifying La sesión de clasificación.
 * @property race La sesión de carrera.
 *
 */
data class Sessions(
    val practice1: Session,
    val practice2: Session,
    val practice3: Session,
    val qualifying: Session,
    val race: Session
)

/**
 * Clase que representa una sesión de un Grand Prix.
 *
 * @property day El día de la sesión.
 * @property time El horario de la sesión.
 */
data class Session(
    val day: String,
    val time: String
)