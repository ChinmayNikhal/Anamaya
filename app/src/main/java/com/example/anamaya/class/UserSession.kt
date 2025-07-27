package com.example.anamaya.`class`

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.example.anamaya.profile.UserInfo

class UserSession(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_FIRST_RUN = "first_run"
        private const val KEY_UID = "uid"
        private const val KEY_FULL_NAME = "full_name"
        private const val KEY_IS_DOCTOR = "is_doctor"
    }

    var phone: String?
        get() = prefs.getString("phone", null)
        set(value) = prefs.edit().putString("phone", value).apply()

    var email: String?
        get() = prefs.getString("email", null)
        set(value) = prefs.edit().putString("email", value).apply()

    var isDoctor: Boolean?
        get() = prefs.getBoolean("isDoctor", false)
        set(value) = prefs.edit().putBoolean("isDoctor", value == true).apply()

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    fun isFirstRun(): Boolean {
        return prefs.getBoolean(KEY_FIRST_RUN, true)
    }

    fun setFirstRunDone() {
        prefs.edit().putBoolean(KEY_FIRST_RUN, false).apply()
    }

    fun logout() {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, false).apply()
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun setUid(uid: String) {
        prefs.edit().putString(KEY_UID, uid).apply()
    }

    fun getUid(): String? = prefs.getString(KEY_UID, null)

    fun setFullName(fullName: String) {
        prefs.edit().putString(KEY_FULL_NAME, fullName).apply()
    }

    fun getFullName(): String? = prefs.getString(KEY_FULL_NAME, null)

    fun setIsDoctor(isDoctor: Boolean) {
        prefs.edit().putBoolean(KEY_IS_DOCTOR, isDoctor).apply()
    }

    fun isDoctor(): Boolean = prefs.getBoolean(KEY_IS_DOCTOR, false)

    fun initializeFromFirebase(onComplete: (() -> Unit)? = null) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Log.w("UserSession", "No user logged in!")
            onComplete?.invoke()
            return
        }

        val uid = currentUser.uid
        setUid(uid) // Save it to prefs
        val dbRef = FirebaseDatabase
            .getInstance("https://anamaya-41e41e-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("users").child(uid)

        dbRef.get().addOnSuccessListener { snapshot ->
            val fullName = snapshot.child("fullName").getValue(String::class.java)
            val isDoctor = snapshot.child("isDoctor").getValue(Boolean::class.java) ?: false

            if (!fullName.isNullOrBlank()) {
                setFullName(fullName)
                setIsDoctor(isDoctor)

                UserInfo.fullName = fullName
                UserInfo.isDoctor = isDoctor
                UserInfo.logState()
            }

            if (!fullName.isNullOrBlank()) {
                setFullName(fullName)
                setIsDoctor(isDoctor)
                Log.d("UserSession", "Fetched fullName='$fullName', isDoctor=$isDoctor")
            } else {
                Log.w("UserSession", "Missing fullName for UID=$uid")
            }

            onComplete?.invoke()
        }.addOnFailureListener {
            Log.e("UserSession", "Failed to fetch user info", it)
            onComplete?.invoke()
        }


    }
}