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

class SearchDoctorDialogFragment(
    private val doctorList: List<Doctor>,
    private val onDoctorSelected: (Doctor) -> Unit
) : DialogFragment() {

    private lateinit var container: LinearLayout
    private lateinit var nameSearch: EditText
    private lateinit var specializationSearch: AutoCompleteTextView
    private lateinit var locationSearch: EditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_search_doctor, null)

        container = view.findViewById(R.id.doctorListContainer)
        nameSearch = view.findViewById(R.id.etSearchName)
        specializationSearch = view.findViewById(R.id.etSearchSpecialization)
        locationSearch = view.findViewById(R.id.etSearchLocation)

        setupSpecializationDropdown()
        setupTextWatchers()
        renderDoctorList(doctorList)

        return Dialog(requireContext()).apply {
            setContentView(view)
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun setupSpecializationDropdown() {
        val specializations = listOf(
            "Cardiologist",
            "Dermatologist",
            "ENT",
            "General Physician",
            "Neurologist",
            "Ophthalmologist",
            "Orthopedic",
            "Pediatrician",
            "Psychiatrist"
        )

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, specializations)
        specializationSearch.setAdapter(adapter)
        specializationSearch.threshold = 1

        // Show dropdown on touch or focus
        specializationSearch.setOnTouchListener { _, _ ->
            specializationSearch.showDropDown()
            false
        }

        specializationSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                specializationSearch.showDropDown()
            }
        }
    }

    private fun setupTextWatchers() {
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val filtered = doctorList.filter {
                    it.name.contains(nameSearch.text.toString(), ignoreCase = true) &&
                            it.specialization.contains(specializationSearch.text.toString(), ignoreCase = true) &&
                            it.location.contains(locationSearch.text.toString(), ignoreCase = true)
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

    private fun renderDoctorList(doctors: List<Doctor>) {
        container.removeAllViews()
        for (doctor in doctors) {
            val itemView = layoutInflater.inflate(R.layout.item_doctor, container, false)
            itemView.findViewById<TextView>(R.id.tvDoctorName).text = doctor.name
            itemView.findViewById<TextView>(R.id.tvDoctorDetails).text =
                "${doctor.specialization} | ${doctor.experienceYears} yrs | ${doctor.location}"

            itemView.setOnClickListener {
                onDoctorSelected(doctor)
                dismiss()
            }
            container.addView(itemView)
        }
    }
}
