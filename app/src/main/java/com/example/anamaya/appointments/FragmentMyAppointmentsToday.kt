package com.example.anamaya.appointments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.anamaya.R
import com.example.anamaya.databinding.AppointmentsFragmentMyAppointmentsTodayBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class FragmentMyAppointmentsToday : Fragment() {

    private var _binding: AppointmentsFragmentMyAppointmentsTodayBinding? = null
    private val binding get() = _binding!!

    private val appointmentsToday = mutableListOf<Appointment>()

    private val dbRef by lazy {
        FirebaseDatabase.getInstance("https://anamaya-41e41e-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("users")
    }

    private val userUid by lazy { FirebaseAuth.getInstance().currentUser?.uid.orEmpty() }

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
        fetchTodayAppointments()
    }

    private fun fetchTodayAppointments() {
        val todayFormatted = SimpleDateFormat("dd_MM_yyyy", Locale.getDefault()).format(Date())

        dbRef.child(userUid).child("user_appointments")
            .orderByKey()
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    appointmentsToday.clear()

                    for (snap in snapshot.children) {
                        val key = snap.key ?: continue
                        if (!key.endsWith(todayFormatted)) continue

                        val value = snap.value as? Map<*, *> ?: continue
                        val time = key.split("_").take(2).joinToString(":")
                        val date = key.split("_").drop(2).joinToString("-")

                        val doctor = value["doctor"] as? String ?: "Unknown"
                        val specialization = value["specialization"] as? String ?: "-"
                        val purpose = value["purpose"] as? String ?: "-"
                        val notes = value["notes"] as? String ?: "-"

                        appointmentsToday.add(
                            Appointment(
                                time = time,
                                date = date,
                                doctor = doctor,
                                specialization = specialization,
                                purpose = purpose,
                                notes = notes
                            )
                        )
                    }

                    showAppointments()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error gracefully if needed
                }
            })
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
