package com.example.anamaya.`class`

data class Medication(
    var name: String,
    var quantity: Int,
    val manufacturer: String = "Generic Pharma Ltd.",
    val description: String = "No description available"
)
