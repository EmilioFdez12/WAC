package com.emi.wac.data.model.sessions

/**
 * Class representing the sessions of a Grand Prix.
 *
 * @property practice1 The first practice session
 * @property practice2 The second practice session (may not exist in sprint weekends)
 * @property practice3 The third practice session (may not exist in sprint weekends)
 * @property sprintQualifying The sprint qualifying session (only exists in sprint weekends)
 * @property sprint The sprint race (only exists in sprint weekends)
 * @property qualifying The qualifying session
 * @property race The main race
 */
data class Sessions(
    val practice1: Session,
    val practice2: Session? = null,
    val practice3: Session? = null,
    val sprintQualifying: Session? = null,
    val sprint: Session? = null,
    val qualifying: Session? = null,
    val race: Session
)

/**
 * Class representing a session.
 *
 * @property day The day of the session
 * @property time The time of the session
 */
data class Session(
    val day: String,
    val time: String
)