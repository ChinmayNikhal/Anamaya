package com.example.anamaya

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var userSession: UserSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userSession = UserSession(this)

        Handler(Looper.getMainLooper()).postDelayed({
            val isLoggedIn = userSession.isLoggedIn()
            Log.d("SessionDebug", "Logged in? $isLoggedIn")

            if (isLoggedIn) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, 1500)
    }
}
