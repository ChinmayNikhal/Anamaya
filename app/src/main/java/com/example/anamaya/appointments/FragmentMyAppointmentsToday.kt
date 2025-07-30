package com.example.anamaya.appointments

import android.graphics.Color
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
        checkUserTypeAndLoadAppointments()
    }

    private fun checkUserTypeAndLoadAppointments() {
        dbRef.child(userUid).child("type")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userType = snapshot.getValue(String::class.java)
                    if (userType == "Doctor") {
                        loadDoctorAppointments()
                    } else {
                        loadUserAppointments()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun loadUserAppointments() {
        val todayFormatted = SimpleDateFormat("dd_MM_yyyy", Locale.getDefault()).format(Date())

        dbRef.child(userUid).child("user_appointments")
            .orderByKey()
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    appointmentsToday.clear()

                    for (snap in snapshot.children) {
                        val key = snap.key ?: continue
                        val parts = key.split("_")
                        if (parts.size < 5) continue

                        val keyFormatted = "${parts[4]}_${parts[3]}_${parts[2]}"
                        if (keyFormatted != todayFormatted) continue

                        val time = "${parts[0]}:${parts[1]}"
                        val date = "${parts[4]}-${parts[3]}-${parts[2]}"

                        val doctor = snap.child("doctor").getValue(String::class.java) ?: "Unknown"
                        val specialization = snap.child("specialization").getValue(String::class.java) ?: "-"
                        val purpose = snap.child("purpose").getValue(String::class.java) ?: "-"
                        val notes = snap.child("notes").getValue(String::class.java) ?: "-"

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

                    showAppointments(isDoctor = false)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun loadDoctorAppointments() {
        val todayFormatted = SimpleDateFormat("dd_MM_yyyy", Locale.getDefault()).format(Date())

        dbRef.child(userUid).child("patient_appointments")
            .orderByKey()
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    appointmentsToday.clear()

                    for (snap in snapshot.children) {
                        val key = snap.key ?: continue
                        val parts = key.split("_")
                        if (parts.size < 5) continue

                        val keyFormatted = "${parts[4]}_${parts[3]}_${parts[2]}"
                        if (keyFormatted != todayFormatted) continue

                        val time = "${parts[0]}:${parts[1]}"
                        val date = "${parts[4]}-${parts[3]}-${parts[2]}"

                        val patientName = snap.child("patient_name").getValue(String::class.java) ?: "Unknown"
                        val purpose = snap.child("notes").getValue(String::class.java) ?: "-"
                        val gender = snap.child("gender").getValue(String::class.java) ?: "-"
                        val age = snap.child("age").getValue(Int::class.java)?.toString() ?: "-"
                        val allergies = snap.child("allergies").getValue(String::class.java) ?: "-"
                        val conditions = snap.child("medical_conditions").getValue(String::class.java) ?: "-"

                        appointmentsToday.add(
                            Appointment(
                                time = time,
                                date = date,
                                doctor = patientName, // Using `doctor` field to show patient name
                                specialization = "Age: $age | Gender: $gender",
                                purpose = purpose,
                                notes = "Allergies: $allergies\nConditions: $conditions"
                            )
                        )
                    }

                    showAppointments(isDoctor = true)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun showAppointments(isDoctor: Boolean) {
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

            if (isDoctor) {
                doctorView.setTextColor(Color.parseColor("#ADD8E6")) // Light Blue
            }

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
