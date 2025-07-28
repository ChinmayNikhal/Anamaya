package com.example.anamaya.appointments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.anamaya.R

class TimeSlotPickerDialogFragment(
    private val onTimeSelected: (String) -> Unit
) : DialogFragment() {

    private var hourIdle = true
    private var minuteIdle = true
    private var lastConfirmedTime: String? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_time_slot_picker, null)

        val npHour = view.findViewById<NumberPicker>(R.id.np_hour)
        val npMinute = view.findViewById<NumberPicker>(R.id.np_minute)

        // Set hour range: 00 to 23
        npHour.minValue = 6
        npHour.maxValue = 20
        npHour.displayedValues = (6..20).map { String.format("%02d", it) }.toTypedArray()

        // Set minute range: 00, 15, 30, 45
        val minuteValues = arrayOf("00", "30")
        npMinute.minValue = 0
        npMinute.maxValue = minuteValues.size - 1
        npMinute.displayedValues = minuteValues

        // Scroll listeners
        val scrollListener = NumberPicker.OnScrollListener { picker, scrollState ->
            val isIdle = scrollState == NumberPicker.OnScrollListener.SCROLL_STATE_IDLE

            if (picker == npHour) hourIdle = isIdle
            if (picker == npMinute) minuteIdle = isIdle

            if (hourIdle && minuteIdle) {
                val hour = String.format("%02d", npHour.value)
                val minute = minuteValues[npMinute.value]
                val selectedTime = "$hour:$minute"

                // Only call if time actually changed
                if (selectedTime != lastConfirmedTime) {
                    lastConfirmedTime = selectedTime
                    onTimeSelected(selectedTime)
                    dismiss() // Auto-close dialog
                }
            }
        }

        npHour.setOnScrollListener(scrollListener)
        npMinute.setOnScrollListener(scrollListener)

        builder.setView(view)
        return builder.create()
    }
}
