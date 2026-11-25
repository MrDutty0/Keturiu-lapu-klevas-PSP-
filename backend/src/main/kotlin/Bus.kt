package com.example

import kotlinx.serialization.Serializable

@Serializable
data class Bus(
    val direction: Boolean,
    val route: String,
    val lon: Double,
    val lat: Double,
    val a: String,
    val b: String,
    val id: String,
    val K: Boolean, // Kondicionierius?
    val W: Boolean, // Wifi?
    val N: Boolean, // Neigaliesiems?
    val Z: Boolean, // Rusiskas?
    val D: Boolean, // Dviraciai?
)
