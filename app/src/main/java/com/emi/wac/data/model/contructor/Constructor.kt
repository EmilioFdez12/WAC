package com.emi.wac.data.model.contructor

data class Constructor(
    val teamId: String,
    val team: String,
    val car: String,
)

data class Constructors(
    val constructors: List<Constructor>
)