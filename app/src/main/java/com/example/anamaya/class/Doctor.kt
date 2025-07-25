package com.example.anamaya.`class`

data class Doctor(
    val name: String,
    val specialization: String,
    val location: String,
    val experienceYears: Int = 0,
    val rating: Double = 0.0,
    val isAvailable: Boolean = true
)