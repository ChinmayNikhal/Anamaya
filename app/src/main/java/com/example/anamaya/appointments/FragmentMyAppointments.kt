package com.example.anamaya.appointments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.anamaya.R
import com.example.anamaya.databinding.AppointmentsFragmentMyAppointmentsBinding
import java.text.SimpleDateFormat
import java.util.*

class FragmentMyAppointments : Fragment() {

    private var _binding: AppointmentsFragmentMyAppointmentsBinding? = null
    private val binding get() = _binding!!

    private val appointmentsMap = mutableMapOf<String, List<Appointment>>() // date -> appointments

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AppointmentsFragmentMyAppointmentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        populateDummyAppointments()

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val selectedDate = sdf.format(Date())
        showAppointmentsForDate(selectedDate)

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val dateString = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            showAppointmentsForDate(dateString)
        }
    }

    private fun showAppointmentsForDate(date: String) {
        binding.appointmentsContainer.removeAllViews()

        val appointments = appointmentsMap[date].orEmpty()

        if (appointments.isEmpty()) {
            val noAppointmentsView = TextView(requireContext()).apply {
                text = "No appointments for this date."
                setTextColor(resources.getColor(R.color.app_text_black, null))
                setPadding(8, 16, 8, 16)
            }
            binding.appointmentsContainer.addView(noAppointmentsView)
            return
        }

        for (appointment in appointments) {
            val itemView = layoutInflater.inflate(R.layout.item_appointment, binding.appointmentsContainer, false)
            val timeView = itemView.findViewById<TextView>(R.id.tvAppointmentTime)
            val doctorView = itemView.findViewById<TextView>(R.id.tvAppointmentDoctor)

            timeView.text = appointment.time
            doctorView.text = appointment.doctor

            itemView.setOnClickListener {
                AppointmentDialogFragment.newInstance(appointment)
                    .show(childFragmentManager, "AppointmentDialog")
            }

            binding.appointmentsContainer.addView(itemView)
        }
    }

    private fun populateDummyAppointments() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = sdf.format(Date())

        val sampleAppointments = listOf(
            Appointment("10:00 AM", today, "Dr. Asha Rao", "Dermatologist", "Skin Check", "Check acne progress"),
            Appointment("3:30 PM", today, "Dr. Mehul Desai", "Dentist", "Cavity", "Bring past x-rays")
        )

        appointmentsMap[today] = sampleAppointments
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
