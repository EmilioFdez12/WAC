package com.emi.wac.data.model.sessions

/**
 * Class representing the sessions of a Grand Prix.
 *
 * @property practice1 The first practice session
 * @property practice2 The second practice session
 * @property practice3 The third practice session
 * @property qualifying The qualifying session
 * @property race The race session
 */
data class Sessions(
    val practice1: Session,
    val practice2: Session,
    val practice3: Session,
    val qualifying: Session,
    val race: Session
)

/**
 * Class representing a Grand Prix session.
 *
 * @property day The day of the session
 * @property time The time of the session
 */
data class Session(
    val day: String,
    val time: String
)