package com.example.anamaya

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.anamaya.`class`.UserSession

class SplashActivity : AppCompatActivity() {

    private lateinit var userSession: UserSession
    private lateinit var continueButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        userSession = UserSession(this)
        continueButton = findViewById(R.id.get_started_button)

        if (userSession.isFirstRun()) {
            continueButton.visibility = Button.VISIBLE

            continueButton.setOnClickListener {
                userSession.setFirstRunDone()
                goNext()
            }
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                goNext()
            }, 1500)
        }
    }

    private fun goNext() {
        val intent = if (userSession.isLoggedIn()) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, LoginActivity::class.java)
        }

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
