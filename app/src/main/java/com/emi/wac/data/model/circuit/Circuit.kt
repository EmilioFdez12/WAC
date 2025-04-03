package com.emi.wac.data.model.circuit

/**
 * Clase que representa un circuito de Fórmula 1.
 * @property id El identificador único del circuito.
 * @property name El nombre del circuito.
 * @property gp El nombre del Grand Prix del campeonato.
 * @property image La ruta de la imagen del circuito.
 * @property flag La ruta de la bandera del país del circuito.
 * @property lapRecord El record del circuito.
 * @property raceLaps El número de vueltas de la carrera.
 * @property length La longitud del circuito en kilómetros.
 *
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