package com.example.anamaya.meds

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import com.example.anamaya.R
import com.example.anamaya.`class`.Medication

class FragmentAddMedicineDialog(private val existingMedication: Medication) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_add_medicine, null)

        val tvMedId = view.findViewById<TextView>(R.id.tvMedId)
        val tvAmt = view.findViewById<TextView>(R.id.tvAmt)
        val tvTime = view.findViewById<TextView>(R.id.tvTime)
        val tvMealOption = view.findViewById<TextView>(R.id.tvMealOption)

        // Properly display actual values
        tvMedId.text = "Medicine: ${existingMedication.name}"
        tvAmt.text = "Amount: ${existingMedication.quantity}"
        tvTime.text = "Time: ${existingMedication.time}"
        tvMealOption.text = "Meal: ${existingMedication.mealOption}"

        val dialogTitle = TextView(requireContext()).apply {
            text = "Medication Details"
            setTextColor(ContextCompat.getColor(context, R.color.app_primary_teal))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            setPadding(40, 40, 40, 20)
            typeface = ResourcesCompat.getFont(context, R.font.roboto_mono)
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
