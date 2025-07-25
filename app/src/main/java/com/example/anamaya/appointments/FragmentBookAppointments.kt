package com.example.anamaya.appointments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.anamaya.R
import com.example.anamaya.`class`.Doctor
import com.example.anamaya.databinding.AppointmentsFragmentBookAppointmentsBinding
import java.util.*

class FragmentBookAppointments : Fragment() {

    private var _binding: AppointmentsFragmentBookAppointmentsBinding? = null
    private val binding get() = _binding!!

    private var selectedDate = ""
    private var selectedTime = ""
    private var selectedDoctor: Doctor? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = AppointmentsFragmentBookAppointmentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvSelectedDate.setOnClickListener { openDatePicker() }
        binding.tvSelectedTime.setOnClickListener { openTimePicker() }

        binding.btnSearchDoctor.setOnClickListener {
            val dummyDoctors = listOf(
                Doctor("Dr. A. Patel", "ENT", "Delhi", 10, 4.7),
                Doctor("Dr. B. Verma", "Cardiologist", "Mumbai", 15, 4.9),
                Doctor("Dr. C. Khan", "Ophthalmologist", "Kolkata", 8, 4.5),
                Doctor("Dr. D. Singh", "Orthopedic", "Delhi", 12, 4.6),
                Doctor("Dr. E. Reddy", "Pediatrician", "Bangalore", 6, 4.3),
            )

            SearchDoctorDialogFragment(dummyDoctors) { selected ->
                selectedDoctor = selected
                binding.tvSelectedDoctor.text = "Selected: ${selectedDoctor?.name} (${selectedDoctor?.specialization})"
            }.show(parentFragmentManager, "SearchDoctorDialog")
        }

        binding.btnCheckAvailability.setOnClickListener {
            if (selectedDate.isBlank() || selectedTime.isBlank()) {
                Toast.makeText(requireContext(), "Please select date and time first", Toast.LENGTH_SHORT).show()
                binding.tvAvailabilityResult.visibility = View.GONE
            } else {
                val available = selectedTime.endsWith("00") || selectedTime.endsWith("30")
                binding.tvAvailabilityResult.apply {
                    text = if (available) "Available to Book" else "Not available, check other time"
                    setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            if (available) R.color.green else R.color.red
                        )
                    )
                    visibility = View.VISIBLE
                }
            }
        }

        binding.btnBookAppointment.setOnClickListener {
            if (selectedDoctor == null) {
                Toast.makeText(requireContext(), "Please select a doctor first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Future: Save to DB or API
            Toast.makeText(
                requireContext(),
                "Booking ${selectedDoctor?.name} on $selectedDate at $selectedTime",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun openDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day)
                binding.tvSelectedDate.text = selectedDate
                binding.tvAvailabilityResult.visibility = View.GONE
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun openTimePicker() {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                val roundedMinute = (minute / 15) * 15
                selectedTime = String.format("%02d:%02d", hourOfDay, roundedMinute)
                binding.tvSelectedTime.text = selectedTime
                binding.tvAvailabilityResult.visibility = View.GONE
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            0,
            true
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
