package com.example.anamaya.meds

import android.app.AlertDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import com.example.anamaya.R
import com.google.firebase.database.*

class DialogAddMedicineFragment(
    private val onMedicineAdded: (String) -> Unit
) : DialogFragment() {

    private lateinit var binding: View

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = layoutInflater.inflate(R.layout.dialog_add_medicine_prescription, null)

        val etMedicine = binding.findViewById<AutoCompleteTextView>(R.id.etMedicineName)
        val etQuantity = binding.findViewById<EditText>(R.id.etQuantity)
        val rgFoodTiming = binding.findViewById<RadioGroup>(R.id.rgFoodTiming)
        val btnPickTime = binding.findViewById<Button>(R.id.btnPickTime)

        val btnPlus = binding.findViewById<Button>(R.id.btnQuantityPlus)
        val btnMinus = binding.findViewById<Button>(R.id.btnQuantityMinus)

        var selectedTime: String = ""

        val searchButton = binding.findViewById<ImageButton>(R.id.btnSearchMed)
        val autoCompleteTextView = etMedicine

        val databaseRef = FirebaseDatabase.getInstance("https://anamaya-41e41e-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("meds")

        searchButton.setOnClickListener {
            val query = autoCompleteTextView.text.toString().trim()
            if (query.isEmpty()) {
                Toast.makeText(requireContext(), "Enter medicine name to search", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            databaseRef.orderByChild("name")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val names = mutableListOf<String>()
                        for (medSnap in snapshot.children) {
                            val name = medSnap.child("name").getValue(String::class.java)
                            if (!name.isNullOrEmpty() && name.contains(query, ignoreCase = true)) {
                                names.add(name)
                            }
                        }

                        if (names.isEmpty()) {
                            Toast.makeText(requireContext(), "No matches found", Toast.LENGTH_SHORT).show()
                        } else {
                            val adapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_dropdown_item_1line,
                                names
                            )
                            autoCompleteTextView.setAdapter(adapter)
                            autoCompleteTextView.showDropDown()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(requireContext(), "Search failed: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        btnPickTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(requireContext(), { _, hour, minute ->
                selectedTime = String.format("%02d:%02d", hour, minute)
                btnPickTime.text = selectedTime
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }

        btnPlus.setOnClickListener {
            val qty = etQuantity.text.toString().toIntOrNull() ?: 1
            etQuantity.setText((qty + 1).toString())
        }

        btnMinus.setOnClickListener {
            val qty = etQuantity.text.toString().toIntOrNull() ?: 1
            if (qty > 1) etQuantity.setText((qty - 1).toString())
        }

        val btnCancel = binding.findViewById<Button>(R.id.btnCancel)
        val btnAdd = binding.findViewById<Button>(R.id.btnAdd)

        btnCancel.setOnClickListener { dismiss() }

        btnAdd.setOnClickListener {
            val medName = etMedicine.text.toString().trim()
            val quantity = etQuantity.text.toString().trim()
            val foodTiming = when (rgFoodTiming.checkedRadioButtonId) {
                R.id.rbBeforeEating -> "Before"
                R.id.rbAfterEating -> "After"
                else -> ""
            }

            if (medName.isEmpty() || quantity.isEmpty() || selectedTime.isEmpty() || foodTiming.isEmpty()) {
                Toast.makeText(context, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val summary = "$medName - $quantity pcs at $selectedTime ($foodTiming)"
            onMedicineAdded(summary)
            dismiss()
        }

        return AlertDialog.Builder(requireContext())
            .setView(binding)
            .create()
    }
}
