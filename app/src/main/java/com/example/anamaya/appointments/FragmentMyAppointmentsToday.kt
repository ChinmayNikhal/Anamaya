package com.example.anamaya.appointments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.anamaya.R
import com.example.anamaya.databinding.AppointmentsFragmentMyAppointmentsTodayBinding
import java.text.SimpleDateFormat
import java.util.*

class FragmentMyAppointmentsToday : Fragment() {

    private var _binding: AppointmentsFragmentMyAppointmentsTodayBinding? = null
    private val binding get() = _binding!!

    private val appointmentsToday = mutableListOf<Appointment>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AppointmentsFragmentMyAppointmentsTodayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        populateDummyAppointments()
        showAppointments()
    }

    private fun showAppointments() {
        binding.appointmentsContainer.removeAllViews()

        if (appointmentsToday.isEmpty()) {
            val emptyView = TextView(requireContext()).apply {
                text = "No appointments for today."
                setTextColor(resources.getColor(R.color.app_text_black, null))
                textSize = 16f
                setPadding(16, 32, 16, 32)
            }
            binding.appointmentsContainer.addView(emptyView)
            return
        }

        for (appointment in appointmentsToday) {
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
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        appointmentsToday.addAll(
            listOf(
                Appointment(
                    time = "09:00 AM",
                    date = today,
                    doctor = "Dr. Ramesh Patel",
                    specialization = "ENT",
                    purpose = "Throat check",
                    notes = "Avoid cold drinks"
                ),
                Appointment(
                    time = "01:00 PM",
                    date = today,
                    doctor = "Dr. Anjali Bansal",
                    specialization = "Physiotherapist",
                    purpose = "Back pain consultation",
                    notes = "Continue physiotherapy exercises"
                )
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
