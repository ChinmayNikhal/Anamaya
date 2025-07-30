package com.example.anamaya.`class`

data class Prescription(
    val date: String = "",
    val doctorName: String = "",
    val medications: List<String> = emptyList(),
    val patientName: String = "",
    val imageUrl: String? = null,
    val ttl: Long = System.currentTimeMillis() + 90L * 24 * 60 * 60 * 1000
)
