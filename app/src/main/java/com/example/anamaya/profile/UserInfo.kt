package com.example.anamaya.profile

object UserInfo {
    var fullName: String? = null
    var isDoctor: Boolean = false

    val firstName: String
        get() {
            val name = fullName?.trim().orEmpty()
            return name.split(" ").firstOrNull() ?: ""
        }

    // Converted to a computed property
    val lastName: String
        get() {
            val parts = fullName?.trim()?.split(" ") ?: return ""
            return if (parts.size >= 2) parts.last() else ""
        }


    val displayName: String
        get() {
            val baseName = "$firstName $lastName".trim()
            return if (isDoctor) "Dr. $baseName" else baseName
        }

    fun logState() {

        android.util.Log.d("UserInfo", "fullName: $fullName, firstName: $firstName, lastName: $lastName, displayName: $displayName, isDoctor: $isDoctor")
    }
}