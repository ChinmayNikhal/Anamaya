package com.example.anamaya.appointments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.anamaya.R
import com.example.anamaya.databinding.AppointmentsFragmentMyAppointmentsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class FragmentMyAppointments : Fragment() {

    private var _binding: AppointmentsFragmentMyAppointmentsBinding? = null
    private val binding get() = _binding!!

    private val appointmentsMap = mutableMapOf<String, MutableList<Appointment>>() // yyyy-MM-dd -> appointments

    private val dbRef by lazy {
        FirebaseDatabase.getInstance("https://anamaya-41e41e-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("users")
    }

    private val userUid by lazy { FirebaseAuth.getInstance().currentUser?.uid.orEmpty() }

    private val displayFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val keyFormat = SimpleDateFormat("dd_MM_yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AppointmentsFragmentMyAppointmentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchAllAppointments()

        val today = displayFormat.format(Date())
        showAppointmentsForDate(today)

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            showAppointmentsForDate(selectedDate)
        }
    }

    private fun fetchAllAppointments() {
        dbRef.child(userUid).child("user_appointments")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    appointmentsMap.clear()

                    for (snap in snapshot.children) {
                        val key = snap.key ?: continue
                        val parts = key.split("_")
                        if (parts.size != 5) continue

                        val hour = parts[0]
                        val minute = parts[1]
                        val day = parts[2]
                        val month = parts[3]
                        val year = parts[4]

                        val dateFormatted = "$year-$month-$day"
                        val timeFormatted = "$hour:$minute"

                        val value = snap.value as? Map<*, *> ?: continue

                        val doctor = value["doctor"] as? String ?: "Unknown"
                        val specialization = value["specialization"] as? String ?: "-"
                        val purpose = value["purpose"] as? String ?: "-"
                        val notes = value["notes"] as? String ?: "-"

                        val appointment = Appointment(
                            time = timeFormatted,
                            date = dateFormatted,
                            doctor = doctor,
                            specialization = specialization,
                            purpose = purpose,
                            notes = notes
                        )

                        val list = appointmentsMap.getOrPut(dateFormatted) { mutableListOf() }
                        list.add(appointment)
                    }

                    // Show today by default after data load
                    showAppointmentsForDate(displayFormat.format(Date()))
                }

                override fun onCancelled(error: DatabaseError) {
                    // handle error
                }
            })
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
