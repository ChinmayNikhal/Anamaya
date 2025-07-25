package com.example.anamaya.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.anamaya.R
import com.google.android.material.textfield.TextInputEditText

class FragmentMyInfo : Fragment() {

    // Personal Details
    private lateinit var fullNameEditText: TextInputEditText
    private lateinit var emailTextView: TextView
    private lateinit var phoneEditText: TextInputEditText
    private lateinit var genderTextView: TextView
    private lateinit var dobTextView: TextView

    // Medical Details
    private lateinit var bloodTypeSpinner: Spinner
    private lateinit var allergiesEditText: TextInputEditText
    private lateinit var medicalConditionsEditText: TextInputEditText

    // Emergency Contact
    private lateinit var emergencyContactNameEditText: TextInputEditText
    private lateinit var emergencyContactPhoneEditText: TextInputEditText

    private lateinit var saveChangesButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_fragment_my_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
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

        saveChangesButton = view.findViewById(R.id.my_info_save_changes_button)

        setupBloodTypeSpinner()
        loadUserData()

        saveChangesButton.setOnClickListener {
            saveUserData()
        }
    }

    private fun setupBloodTypeSpinner() {
        val bloodTypesAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.blood_types_array,
            android.R.layout.simple_spinner_item
        )
        bloodTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        bloodTypeSpinner.adapter = bloodTypesAdapter

        // Set text color of selected item dynamically to ensure visibility
        bloodTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                (view as? TextView)?.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.app_text_black)
                )
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun loadUserData() {
        fullNameEditText.setText("John Doe")
        emailTextView.text = "john.doe@example.com"
        phoneEditText.setText("123-456-7890")
        genderTextView.text = "Male"
        dobTextView.text = "01/01/1990"

        val bloodTypes = resources.getStringArray(R.array.blood_types_array)
        val defaultBloodTypeIndex = bloodTypes.indexOf("O+")
        if (defaultBloodTypeIndex != -1) {
            bloodTypeSpinner.setSelection(defaultBloodTypeIndex)
        }

        // Force spinner text color initially
        bloodTypeSpinner.post {
            (bloodTypeSpinner.selectedView as? TextView)?.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.app_text_black)
            )
        }

        allergiesEditText.setText("Pollen, Dust Mites")
        medicalConditionsEditText.setText("Seasonal Asthma")
        emergencyContactNameEditText.setText("Jane Doe")
        emergencyContactPhoneEditText.setText("987-654-3210")
    }

    private fun saveUserData() {
        val fullName = fullNameEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()
        val selectedBloodType = bloodTypeSpinner.selectedItem.toString()
        val allergies = allergiesEditText.text.toString().trim()
        val medicalConditions = medicalConditionsEditText.text.toString().trim()
        val emergencyContactName = emergencyContactNameEditText.text.toString().trim()
        val emergencyContactPhone = emergencyContactPhoneEditText.text.toString().trim()

        val bloodTypes = resources.getStringArray(R.array.blood_types_array)

        if (fullName.isEmpty() || phone.isEmpty() || allergies.isEmpty() || medicalConditions.isEmpty()
            || emergencyContactName.isEmpty() || emergencyContactPhone.isEmpty()
            || selectedBloodType == bloodTypes[0]
        ) {
            Toast.makeText(
                requireContext(),
                "Please fill all required fields and select blood type.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        Log.d("MyInfoFragment", "Saving Data:")
        Log.d("MyInfoFragment", "Full Name: $fullName")
        Log.d("MyInfoFragment", "Phone: $phone")
        Log.d("MyInfoFragment", "Blood Type: $selectedBloodType")
        Log.d("MyInfoFragment", "Allergies: $allergies")
        Log.d("MyInfoFragment", "Medical Conditions: $medicalConditions")
        Log.d("MyInfoFragment", "Emergency Contact Name: $emergencyContactName")
        Log.d("MyInfoFragment", "Emergency Contact Phone: $emergencyContactPhone")

        Toast.makeText(requireContext(), "Changes saved successfully!", Toast.LENGTH_SHORT).show()
    }
}
