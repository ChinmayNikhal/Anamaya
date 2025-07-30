package com.example.anamaya.`class`

data class ScheduledMedication(
    val dayLabel: String = "",       // e.g., "Today", "Tomorrow", "Mon, 12 Aug"
    val timeLabel: String = "",      // e.g., "08:00 AM"
    val medicationName: String = "",
    val amount: Int = 0,
    val mealOption: String = "",     // "Before" or "After"
    val description: String = "",    // Optional: description or instructions
    val manufacturer: String = ""    // Optional
)