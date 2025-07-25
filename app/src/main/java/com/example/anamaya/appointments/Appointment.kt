package com.example.anamaya.appointments

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Appointment(
    val time: String,
    val date: String,
    val doctor: String,
    val specialization: String,
    val purpose: String,
    val notes: String
) : Parcelable
