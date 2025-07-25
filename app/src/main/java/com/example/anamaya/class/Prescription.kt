package com.example.anamaya.`class`

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Prescription(
    val date: String,
    val doctorName: String,
    val medications: List<String>,
    var imageUri: Uri? = null
) : Parcelable