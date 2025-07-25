package com.example.anamaya.meds

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.LinearLayout
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.anamaya.`class`.Medication
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.anamaya.R

class FragmentMyMedications : Fragment() {

    private lateinit var medicationsListContainer: LinearLayout
    private lateinit var searchBar: EditText
    private lateinit var fabAddMedicine: FloatingActionButton

    private val allMedications = mutableListOf(
        Medication("Paracetamol", 30),
        Medication("Amoxicillin", 14),
        Medication("Multivitamin", 60),
        Medication("Ibuprofen", 20),
        Medication("Cetirizine", 10),
        Medication("Omeprazole", 28),
        Medication("Paracetamol", 30),
        Medication("Amoxicillin", 14),
        Medication("Multivitamin", 60),
        Medication("Ibuprofen", 20),
        Medication("Cetirizine", 10),
        Medication("Omeprazole", 28)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.meds_fragment_my_medications, container, false)

        medicationsListContainer = view.findViewById(R.id.medicationsListContainer)
        searchBar = view.findViewById(R.id.searchBar)
        fabAddMedicine = view.findViewById(R.id.fabAddMedicine)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displayMedications(allMedications)

        fabAddMedicine.setOnClickListener {
            FragmentAddMedicineDialog().show(childFragmentManager, "AddMedicineDialog")
        }

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isEmpty()) {
                    displayMedications(allMedications)
                } else {
                    val filteredList = allMedications.filter {
                        it.name.contains(query, ignoreCase = true)
                    }
                    displayMedications(filteredList)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    fun displayMedications(medsToDisplay: List<Medication>) {
        medicationsListContainer.removeAllViews()

        if (medsToDisplay.isEmpty()) {
            val noResultsTv = TextView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                text = "No medications found."
//                textSize = resources.getDimension(R.dimen.text_size) // Use dimension resource
                gravity = android.view.Gravity.CENTER
                setPadding(0, 32, 0, 32)
                setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
            }
            medicationsListContainer.addView(noResultsTv)
            return
        }

        val inflater = LayoutInflater.from(context)
        medsToDisplay.forEach { medication ->
            val medicationItemView = inflater.inflate(R.layout.item_medication, medicationsListContainer, false)

            val tvMedicationName = medicationItemView.findViewById<TextView>(R.id.tvMedicationName)
            val tvMedicationQuantity = medicationItemView.findViewById<TextView>(R.id.tvMedicationQuantity)

            tvMedicationName.text = medication.name
            tvMedicationQuantity.text = "Qty: ${medication.quantity}"

            medicationItemView.setOnClickListener {
                FragmentAddMedicineDialog(medication).show(childFragmentManager, "ViewMedicineDialog")
            }

            medicationsListContainer.addView(medicationItemView)
        }
    }

    fun addNewMedication(medication: Medication) {
        allMedications.add(medication)
        displayMedications(allMedications.sortedBy { it.name })
        showToast("Added new medication: ${medication.name}")
    }

    fun deleteMedication(med: Medication) {
        allMedications.remove(med)
        displayMedications(allMedications)
        showToast("Deleted: ${med.name}")
    }

    fun getAllMedications(): List<Medication> = allMedications


    fun showToast(message: String) {
//        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        if (isAdded) { // Check if fragment is attached to the activity
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
