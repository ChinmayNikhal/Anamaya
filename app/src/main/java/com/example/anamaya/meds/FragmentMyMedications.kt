package com.example.anamaya.meds

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.anamaya.R
import com.example.anamaya.`class`.Medication
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class FragmentMyMedications : Fragment() {

    private lateinit var medicationsListContainer: LinearLayout
    private lateinit var searchBar: EditText
    private lateinit var fabAddMedicine: FloatingActionButton
    private val allMedications = mutableListOf<Medication>()

    private val databaseRef by lazy {
        FirebaseDatabase.getInstance("https://anamaya-41e41e-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("meds")
    }

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

        fetchMedicationsFromFirebase()

        fabAddMedicine.setOnClickListener {
            FragmentAddMedicineDialog().show(childFragmentManager, "AddMedicineDialog")
        }

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                val filteredList = if (query.isEmpty()) allMedications else {
                    allMedications.filter {
                        it.name.contains(query, ignoreCase = true)
                    }
                }
                displayMedications(filteredList)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun fetchMedicationsFromFirebase() {
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allMedications.clear()
                for (medSnapshot in snapshot.children) {
                    val med = medSnapshot.getValue(Medication::class.java)
                    if (med != null) {
                        allMedications.add(med)
                    }
                }
                displayMedications(allMedications)
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Failed to fetch medications: ${error.message}")
            }
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
                gravity = Gravity.CENTER
                setPadding(0, 32, 0, 32)
                setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
            }
            medicationsListContainer.addView(noResultsTv)
            return
        }

        val inflater = LayoutInflater.from(context)
        medsToDisplay.forEach { medication ->
            val itemView = inflater.inflate(R.layout.item_medication, medicationsListContainer, false)

            itemView.findViewById<TextView>(R.id.tvMedicationName).text = medication.name
            itemView.findViewById<TextView>(R.id.tvMedicationQuantity).text = "Qty: ${medication.quantity}"

            itemView.setOnClickListener {
                FragmentAddMedicineDialog(medication).show(childFragmentManager, "ViewMedicineDialog")
            }

            medicationsListContainer.addView(itemView)
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
        if (isAdded) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }
}
