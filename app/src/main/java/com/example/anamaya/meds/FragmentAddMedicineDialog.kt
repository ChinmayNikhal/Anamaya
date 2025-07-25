package com.example.anamaya.meds

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import com.example.anamaya.R
import com.example.anamaya.`class`.Medication

class FragmentAddMedicineDialog(private val existingMedication: Medication? = null) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_add_medicine, null)

        val etName = view.findViewById<EditText>(R.id.etMedicineName)
        val tvManufacturer = view.findViewById<TextView>(R.id.tvManufacturer)
        val tvDescription = view.findViewById<TextView>(R.id.tvDescription)
        val etQuantity = view.findViewById<EditText>(R.id.quantityValue)
        val btnMinus = view.findViewById<Button>(R.id.quantityMinus)
        val btnPlus = view.findViewById<Button>(R.id.quantityPlus)

        val editButtons = view.findViewById<LinearLayout>(R.id.editActionButtons)
        val addButtons = view.findViewById<LinearLayout>(R.id.addActionButtons)

        val btnDelete = view.findViewById<Button>(R.id.btnDelete)
        val btnUpdate = view.findViewById<Button>(R.id.btnUpdate)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnAdd = view.findViewById<Button>(R.id.btnAdd)

        // Utility methods
        fun getQuantity(): Int = etQuantity.text.toString().toIntOrNull() ?: 0
        fun updateQuantity(delta: Int) {
            val newQty = (getQuantity() + delta).coerceAtLeast(0)
            etQuantity.setText(newQty.toString())
        }

        // Custom Title
        val dialogTitle = TextView(requireContext()).apply {
            text = if (existingMedication != null) "View Medication" else "Add Medication"
            setTextColor(ContextCompat.getColor(context, R.color.app_primary_teal))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            setPadding(40, 40, 40, 20)
            typeface = ResourcesCompat.getFont(context, R.font.roboto_mono)
        }

        // Quantity +/- listeners
        btnPlus.setOnClickListener { updateQuantity(+1) }
        btnMinus.setOnClickListener { updateQuantity(-1) }

        // Edit Mode
        if (existingMedication != null) {
            etName.setText(existingMedication.name)
            etQuantity.setText(existingMedication.quantity.toString())
            tvManufacturer.text = "Manufacturer: ${existingMedication.manufacturer}"
            tvDescription.text = "Description: ${existingMedication.description}"
            editButtons.visibility = View.VISIBLE
            addButtons.visibility = View.GONE

            btnUpdate.setOnClickListener {
                val updatedName = etName.text.toString().trim()
                val updatedQty = getQuantity()

                if (updatedName.isBlank() || updatedQty <= 0) {
                    (parentFragment as? FragmentMyMedications)?.showToast("Please enter valid data.")
                } else {
                    existingMedication.name = updatedName
                    existingMedication.quantity = updatedQty
                    (parentFragment as? FragmentMyMedications)?.showToast("Medication updated.")
                    (parentFragment as? FragmentMyMedications)?.displayMedications(
                        (parentFragment as FragmentMyMedications).getAllMedications()
                    )
                    dismiss()
                }
            }

            btnDelete.setOnClickListener {
                (parentFragment as? FragmentMyMedications)?.deleteMedication(existingMedication)
                dismiss()
            }

        } else {
            // Add Mode
            editButtons.visibility = View.GONE
            addButtons.visibility = View.VISIBLE

            tvManufacturer.text = "Manufacturer: Generic Pharma Ltd."
            tvDescription.text = "Description: Standard over-the-counter medicine"
            etQuantity.setText("1")

            btnAdd.setOnClickListener {
                val name = etName.text.toString().trim()
                val quantity = getQuantity()

                if (name.isNotBlank() && quantity > 0) {
                    val newMedication = Medication(
                        name = name,
                        quantity = quantity,
                        manufacturer = "Generic Pharma Ltd.",
                        description = "Standard over-the-counter medicine"
                    )
                    (parentFragment as? FragmentMyMedications)?.addNewMedication(newMedication)
                    dismiss()
                } else {
                    (parentFragment as? FragmentMyMedications)?.showToast("Please enter valid name and quantity.")
                }
            }

            btnCancel.setOnClickListener {
                dismiss()
            }
        }

        return AlertDialog.Builder(requireContext())
            .setCustomTitle(dialogTitle)
            .setView(view)
            .create()
            .apply {
                window?.setBackgroundDrawableResource(android.R.color.transparent)
            }
    }
}
