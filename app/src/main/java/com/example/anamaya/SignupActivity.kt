package com.example.anamaya

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class SignupActivity : AppCompatActivity() {

    private lateinit var memberOptionTv: TextView
    private lateinit var doctorOptionTv: TextView
    private lateinit var doctorFieldsContainer: LinearLayout

    private lateinit var fullNameInputEditText: TextInputEditText
    private lateinit var genderRadioGroup: RadioGroup
    private lateinit var dobInputEditText: TextInputEditText
    private lateinit var phoneInputEditText: TextInputEditText
    private lateinit var emailInputEditText: TextInputEditText
    private lateinit var passwordInputEditText: TextInputEditText
    private lateinit var confirmPasswordInputEditText: TextInputEditText
    private lateinit var termsCheckbox: CheckBox
    private lateinit var signupButton: Button
    private lateinit var googleSignupButton: Button
    private lateinit var loginRedirectTv: TextView

    private lateinit var medicalLicenseInputEditText: TextInputEditText
    private lateinit var specializationInputAutoComplete: AutoCompleteTextView
    private lateinit var medicalCouncilInputAutoComplete: AutoCompleteTextView

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance("https://anamaya-41e41e-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val dbRef = database.reference

        // UI References
        memberOptionTv = findViewById(R.id.member_option_tv)
        doctorOptionTv = findViewById(R.id.doctor_option_tv)
        doctorFieldsContainer = findViewById(R.id.doctor_fields_container)

        fullNameInputEditText = findViewById(R.id.full_name_input_edittext)
        genderRadioGroup = findViewById(R.id.gender_radio_group)
        dobInputEditText = findViewById(R.id.dob_input_edittext)
        phoneInputEditText = findViewById(R.id.phone_input_edittext)
        emailInputEditText = findViewById(R.id.email_input_edittext)
        passwordInputEditText = findViewById(R.id.password_input_edittext)
        confirmPasswordInputEditText = findViewById(R.id.confirm_password_input_edittext)
        termsCheckbox = findViewById(R.id.terms_checkbox)
        signupButton = findViewById(R.id.signup_button)
        googleSignupButton = findViewById(R.id.google_signup_button)
        loginRedirectTv = findViewById(R.id.login_redirect)

        medicalLicenseInputEditText = findViewById(R.id.medical_license_input_edittext)
        specializationInputAutoComplete = findViewById(R.id.specialization_input_autocomplete)
        medicalCouncilInputAutoComplete = findViewById(R.id.medical_council_input_autocomplete)

        // Dropdowns
        val specializations = arrayOf("Cardiology", "Neurology", "Pediatrics", "Dermatology")
        specializationInputAutoComplete.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, specializations)
        )
        specializationInputAutoComplete.setOnClickListener {
            specializationInputAutoComplete.showDropDown()
        }

        val councils = arrayOf("MCI", "DCI", "INC", "PCI")
        medicalCouncilInputAutoComplete.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, councils)
        )
        medicalCouncilInputAutoComplete.setOnClickListener {
            medicalCouncilInputAutoComplete.showDropDown()
        }

        // Toggle role
        memberOptionTv.isSelected = true
        doctorFieldsContainer.visibility = View.GONE

        memberOptionTv.setOnClickListener {
            if (!memberOptionTv.isSelected) {
                memberOptionTv.isSelected = true
                doctorOptionTv.isSelected = false
                doctorFieldsContainer.visibility = View.GONE
            }
        }

        doctorOptionTv.setOnClickListener {
            if (!doctorOptionTv.isSelected) {
                doctorOptionTv.isSelected = true
                memberOptionTv.isSelected = false
                doctorFieldsContainer.visibility = View.VISIBLE
            }
        }

        // Date picker
        dobInputEditText.setOnClickListener {
            showDatePickerDialog()
        }

        // Signup logic
        signupButton.setOnClickListener {
            val fullName = fullNameInputEditText.text.toString().trim()
            val gender = getSelectedGender()
            val dob = dobInputEditText.text.toString().trim()
            val phone = phoneInputEditText.text.toString().trim()
            val email = emailInputEditText.text.toString().trim()
            val password = passwordInputEditText.text.toString().trim()
            val confirmPassword = confirmPasswordInputEditText.text.toString().trim()
            val isDoctor = doctorOptionTv.isSelected

            if (fullName.isEmpty() || gender.isEmpty() || dob.isEmpty() ||
                phone.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
            ) {
                Toast.makeText(this, "Please fill in all common fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!termsCheckbox.isChecked) {
                Toast.makeText(this, "Please accept the Terms and Conditions", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val doctorFields = mutableMapOf<String, Any>()
            if (isDoctor) {
                val license = medicalLicenseInputEditText.text.toString().trim()
                val specialization = specializationInputAutoComplete.text.toString().trim()
                val council = medicalCouncilInputAutoComplete.text.toString().trim()

                if (license.isEmpty() || specialization.isEmpty() || council.isEmpty()) {
                    Toast.makeText(this, "Please fill in all doctor-specific fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                doctorFields["license"] = license
                doctorFields["specialization"] = specialization
                doctorFields["council"] = council
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                        val userMap = mutableMapOf<String, Any>(
                            "fullName" to fullName,
                            "gender" to gender,
                            "dob" to dob,
                            "phone" to phone,
                            "email" to email,
                            "isDoctor" to isDoctor,

                            // Default values
                            "bloodType" to "A+",
                            "allergies" to "none",
                            "medicalConditions" to "none",
                            "emergencyContactName" to "Emergency Contact Name",
                            "emergencyContactPhone" to "0000000000",

                            // Placeholder for related paths
                            "user_prescription" to mapOf("dummy_prescription_id" to true),
                            "user_meds" to mapOf("dummy_med_id" to true)
                        )

                        userMap.putAll(doctorFields)

                        dbRef.child("users").child(uid).setValue(userMap)
                            .addOnSuccessListener {
                                // Add to doctors node if doctor
                                if (isDoctor) {
                                    val doctorEntry = mapOf(
                                        "fullName" to fullName,
                                        "specialization" to doctorFields["specialization"].toString()
                                    )
                                    dbRef.child("doctors").child(uid).setValue(doctorEntry)
                                }

                                Toast.makeText(this, "Signup successful", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Signup failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        loginRedirectTv.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val formattedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                dobInputEditText.setText(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun getSelectedGender(): String {
        val selectedId = genderRadioGroup.checkedRadioButtonId
        return if (selectedId != -1) {
            findViewById<RadioButton>(selectedId).text.toString()
        } else ""
    }
}
