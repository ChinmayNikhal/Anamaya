package com.example.anamaya.meds

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.anamaya.R
import java.util.*

class FragmentAddToSchedule : Fragment() {

    private var quantity = 1
    private lateinit var quantityText: EditText
    private lateinit var timePickerButton: Button
    private var selectedTime: String = "Not set"

    private lateinit var foodTimingGroup: RadioGroup
    private lateinit var radioBefore: RadioButton
    private lateinit var radioAfter: RadioButton

    private val selectedDays = mutableSetOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.meds_fragment_add_to_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        quantityText = view.findViewById(R.id.quantityValue)
        timePickerButton = view.findViewById(R.id.timePickerButton)
        foodTimingGroup = view.findViewById(R.id.foodTimingGroup)
        radioBefore = view.findViewById(R.id.radio_before_eating)
        radioAfter = view.findViewById(R.id.radio_after_eating)

        val minusBtn = view.findViewById<Button>(R.id.quantityMinus)
        val plusBtn = view.findViewById<Button>(R.id.quantityPlus)

        minusBtn.setOnClickListener {
            if (quantity > 1) {
                quantity--
                quantityText.setText(quantity.toString())
            }
        }

        plusBtn.setOnClickListener {
            quantity++
            quantityText.setText(quantity.toString())
        }

        timePickerButton.setOnClickListener {
            showTimePicker()
        }

        setupDayToggle(view)

        val addButton = view.findViewById<Button>(R.id.btnAddNewPrescription)
        addButton.setOnClickListener {
            val foodTiming = when (foodTimingGroup.checkedRadioButtonId) {
                R.id.radio_before_eating -> "Before Eating"
                R.id.radio_after_eating -> "After Eating"
                else -> "Not specified"
            }

            val selectedDaysFormatted = if (selectedDays.isEmpty()) "No days selected"
            else selectedDays.joinToString(", ")

            Toast.makeText(
                requireContext(),
                "Added:\nQty: $quantity\nTime: $selectedTime\nWhen: $foodTiming\nDays: $selectedDaysFormatted",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
            selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            timePickerButton.text = selectedTime
        }, hour, minute, true).show()
    }

    private fun setupDayToggle(view: View) {
        val days = listOf(
            Pair("Sun", R.id.day_sun),
            Pair("Mon", R.id.day_mon),
            Pair("Tue", R.id.day_tue),
            Pair("Wed", R.id.day_wed),
            Pair("Thu", R.id.day_thu),
            Pair("Fri", R.id.day_fri),
            Pair("Sat", R.id.day_sat)
        )

        for ((label, id) in days) {
            val toggle = view.findViewById<ToggleButton>(id)
            toggle.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedDays.add(label)
                } else {
                    selectedDays.remove(label)
                }
            }
        }
    }
}
