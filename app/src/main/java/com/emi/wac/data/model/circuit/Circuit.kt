package com.emi.wac.data.model.circuit

/**
 * Data class representing a circuit.
 * 
 * @property id The unique identifier of the circuit
 * @property name The name of the circuit
 * @property gp The name of the championship Grand Prix
 * @property image The path to the circuit image
 * @property flag The path to the country flag of the circuit
 * @property lapRecord The circuit lap record
 * @property raceLaps The number of laps in the race
 * @property length The length of the circuit in kilometers
 */
data class Circuit(
    val id: Int,
    val name: String,
    val gp: String,
    val image: String,
    val flag: String,
    val lapRecord: String,
    val raceLaps: Int,
    val length: Double,
    val localization: String,
)


/**
 * Class that represents a list of circuits.
 */
data class Circuits(
    val circuits: List<Circuit>
)