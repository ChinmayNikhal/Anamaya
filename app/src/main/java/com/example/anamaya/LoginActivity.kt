package com.example.anamaya

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {

    private lateinit var memberOptionTv: TextView
    private lateinit var doctorOptionTv: TextView
    private lateinit var emailInputEditText: TextInputEditText
    private lateinit var passwordInputEditText: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var signupRedirectTv: TextView

    private lateinit var userSession: UserSession
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        userSession = UserSession(this)
        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance("https://anamaya-41e41e-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

        memberOptionTv = findViewById(R.id.member_option_tv)
        doctorOptionTv = findViewById(R.id.doctor_option_tv)
        emailInputEditText = findViewById(R.id.email_input_edittext)
        passwordInputEditText = findViewById(R.id.password_input_edittext)
        loginButton = findViewById(R.id.login_button)
        signupRedirectTv = findViewById(R.id.signup_redirect)

        memberOptionTv.isSelected = true
        doctorOptionTv.isSelected = false

        memberOptionTv.setOnClickListener {
            if (!memberOptionTv.isSelected) {
                memberOptionTv.isSelected = true
                doctorOptionTv.isSelected = false
                Log.d("LoginActivity", "Member selected for login")
            }
        }

        doctorOptionTv.setOnClickListener {
            if (!doctorOptionTv.isSelected) {
                doctorOptionTv.isSelected = true
                memberOptionTv.isSelected = false
                Log.d("LoginActivity", "Doctor selected for login")
            }
        }

        signupRedirectTv.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            Log.d("LoginActivity", "Redirecting to Signup screen")
        }

        loginButton.setOnClickListener {
            val email = emailInputEditText.text.toString().trim()
            val password = passwordInputEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d("LoginActivity", "Attempting login with Email: $email")

            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = firebaseAuth.currentUser?.uid
                        if (uid != null) {
                            databaseReference.child("users").child(uid)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val userData = snapshot.value
                                        Log.d("LoginActivity", "User data: $userData")
                                        userSession.setLoggedIn(true)
                                        Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                        finish()
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Toast.makeText(this@LoginActivity, "Failed to load user data", Toast.LENGTH_SHORT).show()
                                    }
                                })
                        }
                    } else {
                        Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        Log.e("LoginActivity", "Login error", task.exception)
                    }
                }
        }
    }
}
