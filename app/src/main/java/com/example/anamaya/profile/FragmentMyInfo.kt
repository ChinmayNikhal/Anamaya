package com.example.anamaya.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.anamaya.R
import com.example.anamaya.`class`.UserSession
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FragmentMyInfo : Fragment() {

    // UI
    private lateinit var fullNameEditText: TextInputEditText
    private lateinit var emailTextView: TextView
    private lateinit var phoneEditText: TextInputEditText
    private lateinit var genderTextView: TextView
    private lateinit var dobTextView: TextView

    private lateinit var bloodTypeSpinner: Spinner
    private lateinit var allergiesEditText: TextInputEditText
    private lateinit var medicalConditionsEditText: TextInputEditText

    private lateinit var emergencyContactNameEditText: TextInputEditText
    private lateinit var emergencyContactPhoneEditText: TextInputEditText

    private lateinit var professionalSection: ViewGroup
    private lateinit var saveChangesButton: Button

    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.profile_fragment_my_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Firebase init
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://anamaya-41e41e-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .reference.child("users")

        // View init
        fullNameEditText = view.findViewById(R.id.my_info_full_name_edittext)
        emailTextView = view.findViewById(R.id.my_info_email_textview)
        phoneEditText = view.findViewById(R.id.my_info_phone_edittext)
        genderTextView = view.findViewById(R.id.my_info_gender_textview)
        dobTextView = view.findViewById(R.id.my_info_dob_textview)

        bloodTypeSpinner = view.findViewById(R.id.my_info_blood_type_spinner)
        allergiesEditText = view.findViewById(R.id.my_info_allergies_edittext)
        medicalConditionsEditText = view.findViewById(R.id.my_info_medical_conditions_edittext)

        emergencyContactNameEditText = view.findViewById(R.id.my_info_emergency_contact_name_edittext)
        emergencyContactPhoneEditText = view.findViewById(R.id.my_info_emergency_contact_phone_edittext)

        professionalSection = view.findViewById(R.id.professional_details_section)
        saveChangesButton = view.findViewById(R.id.my_info_save_changes_button)

        setupBloodTypeSpinner()
        loadUserData()

        saveChangesButton.setOnClickListener {
            saveUserData()
        }
    }

    private fun setupBloodTypeSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.blood_types_array,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        bloodTypeSpinner.adapter = adapter

        bloodTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                (view as? TextView)?.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_text_black))
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun loadUserData() {
        val uid = auth.currentUser?.uid ?: return

        database.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val fullName = snapshot.child("fullName").getValue(String::class.java)
                val email = snapshot.child("email").getValue(String::class.java)
                val phone = snapshot.child("phone").getValue(String::class.java)
                val gender = snapshot.child("gender").getValue(String::class.java)
                val dob = snapshot.child("dob").getValue(String::class.java)
                val isDoctor = snapshot.child("isDoctor").getValue(Boolean::class.java) ?: false

                fullNameEditText.setText(fullName ?: "")
                emailTextView.text = email ?: "N/A"
                phoneEditText.setText(phone ?: "")
                genderTextView.text = gender ?: "N/A"
                dobTextView.text = dob ?: "N/A"

                // Optional fields
                allergiesEditText.setText(snapshot.child("allergies").getValue(String::class.java) ?: "")
                medicalConditionsEditText.setText(snapshot.child("medicalConditions").getValue(String::class.java) ?: "")
                emergencyContactNameEditText.setText(snapshot.child("emergencyContactName").getValue(String::class.java) ?: "")
                emergencyContactPhoneEditText.setText(snapshot.child("emergencyContactPhone").getValue(String::class.java) ?: "")

                // Blood Type Spinner value
                val bloodType = snapshot.child("bloodType").getValue(String::class.java) ?: ""
                val bloodArray = resources.getStringArray(R.array.blood_types_array)
                val index = bloodArray.indexOf(bloodType)
                if (index >= 0) bloodTypeSpinner.setSelection(index)

                // Doctor visibility
                professionalSection.visibility = if (isDoctor) View.VISIBLE else View.GONE

                Log.d("MyInfo", "Loaded from Firebase for $uid: $fullName, $email")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MyInfo", "Firebase error: ${error.message}")
                Toast.makeText(requireContext(), "Failed to load info.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveUserData() {
        val uid = auth.currentUser?.uid ?: return

        val updatedData = mapOf<String, Any>(
            "fullName" to (fullNameEditText.text?.toString() ?: ""),
            "phone" to (phoneEditText.text?.toString() ?: ""),
            "bloodType" to (bloodTypeSpinner.selectedItem?.toString() ?: ""),
            "allergies" to (allergiesEditText.text?.toString() ?: ""),
            "medicalConditions" to (medicalConditionsEditText.text?.toString() ?: ""),
            "emergencyContactName" to (emergencyContactNameEditText.text?.toString() ?: ""),
            "emergencyContactPhone" to (emergencyContactPhoneEditText.text?.toString() ?: "")
        )

        database.child(uid).updateChildren(updatedData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Info saved successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { error ->
                Toast.makeText(requireContext(), "Failed to save info: ${error.message}", Toast.LENGTH_LONG).show()
                Log.e("MyInfo", "Error saving data: ${error.message}")
            }
    }

}
