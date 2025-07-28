package com.example.anamaya.appointments

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.example.anamaya.R
import com.example.anamaya.`class`.Doctor
import com.google.firebase.database.*

class SearchDoctorDialogFragment(
    private val onDoctorSelected: (Doctor, String) -> Unit
) : DialogFragment() {

    private lateinit var container: LinearLayout
    private lateinit var nameSearch: EditText
    private lateinit var specializationSearch: AutoCompleteTextView
    private lateinit var locationSearch: EditText
    private val doctorList = mutableListOf<Pair<String, Doctor>>() // Pair of UID and Doctor

    private val databaseRef: DatabaseReference by lazy {
        FirebaseDatabase.getInstance("https://anamaya-41e41e-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("doctors")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_search_doctor, null)

        container = view.findViewById(R.id.doctorListContainer)
        nameSearch = view.findViewById(R.id.etSearchName)
        specializationSearch = view.findViewById(R.id.etSearchSpecialization)
        locationSearch = view.findViewById(R.id.etSearchLocation)

        setupSpecializationDropdown()
        setupTextWatchers()
        fetchDoctorsFromFirebase()

        return Dialog(requireContext()).apply {
            setContentView(view)
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun fetchDoctorsFromFirebase() {
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                doctorList.clear()
                for (doctorSnap in snapshot.children) {
                    val uid = doctorSnap.key ?: continue
                    val fullName = doctorSnap.child("fullName").getValue(String::class.java) ?: ""
                    val specialization = doctorSnap.child("specialization").getValue(String::class.java) ?: ""
                    val location = doctorSnap.child("location").getValue(String::class.java) ?: ""
                    val experience = doctorSnap.child("experienceYears").getValue(Int::class.java) ?: 0
                    val rating = doctorSnap.child("rating").getValue(Double::class.java) ?: 0.0

                    val doctor = Doctor(
                        name = fullName,
                        specialization = specialization,
                        location = location,
                        experienceYears = experience,
                        rating = rating
                    )
                    doctorList.add(Pair(uid, doctor))
                }
                renderDoctorList(doctorList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load doctors.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupSpecializationDropdown() {
        val specializations = listOf(
            "Cardiologist", "Dermatologist", "ENT", "General Physician",
            "Neurologist", "Ophthalmologist", "Orthopedic", "Pediatrician", "Psychiatrist"
        )

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, specializations)
        specializationSearch.setAdapter(adapter)
        specializationSearch.threshold = 1

        specializationSearch.setOnTouchListener { _, _ ->
            specializationSearch.showDropDown(); false
        }

        specializationSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) specializationSearch.showDropDown()
        }
    }

    private fun setupTextWatchers() {
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val filtered = doctorList.filter {
                    it.second.name.contains(nameSearch.text.toString(), ignoreCase = true) &&
                            it.second.specialization.contains(specializationSearch.text.toString(), ignoreCase = true) &&
                            it.second.location.contains(locationSearch.text.toString(), ignoreCase = true)
                }
                renderDoctorList(filtered)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        nameSearch.addTextChangedListener(watcher)
        specializationSearch.addTextChangedListener(watcher)
        locationSearch.addTextChangedListener(watcher)
    }

    private fun renderDoctorList(doctors: List<Pair<String, Doctor>>) {
        container.removeAllViews()
        for ((uid, doctor) in doctors) {
            val itemView = layoutInflater.inflate(R.layout.item_doctor, container, false)
            itemView.findViewById<TextView>(R.id.tvDoctorName).text = doctor.name
            itemView.findViewById<TextView>(R.id.tvDoctorDetails).text =
                "${doctor.specialization} | ${doctor.experienceYears} yrs | ${doctor.location}"


            itemView.setOnClickListener {
                onDoctorSelected(doctor, uid)
//                Toast.makeText(requireContext(), "${uid},${doctor}!", Toast.LENGTH_LONG).show()
                dismiss()
            }
            container.addView(itemView)
        }
    }
}
