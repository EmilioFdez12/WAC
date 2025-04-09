package com.emi.wac.data.model.circuit

import android.content.Context
import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

private const val tag = "Circuit"
private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

/**
 * Class representing a Formula 1 circuit.
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
    val length: Double
)

data class Circuits(
    val circuits: List<Circuit>
)