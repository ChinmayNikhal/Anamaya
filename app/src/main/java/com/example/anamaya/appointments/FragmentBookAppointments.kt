package com.example.anamaya.appointments

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.anamaya.R
import com.example.anamaya.`class`.Doctor
import com.example.anamaya.databinding.AppointmentsFragmentBookAppointmentsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class FragmentBookAppointments : Fragment() {

    private var _binding: AppointmentsFragmentBookAppointmentsBinding? = null
    private val binding get() = _binding!!

    private var selectedDate = ""
    private var selectedTime = ""
    private var selectedDoctorUid: String? = null
    private var selectedDoctorDetails: Doctor? = null

    private val user = FirebaseAuth.getInstance().currentUser

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
            val dialog = SearchDoctorDialogFragment { doctor, uid ->
                selectedDoctorUid = uid
                selectedDoctorDetails = doctor
                binding.tvSelectedDoctor.text =
                    "Selected: ${doctor.name} (${doctor.specialization})"
            }
            dialog.show(parentFragmentManager, "SearchDoctorDialog")
        }

        binding.btnCheckAvailability.setOnClickListener {
            if (selectedDate.isBlank() || selectedTime.isBlank() || selectedDoctorUid.isNullOrBlank()) {
                Toast.makeText(requireContext(), "Please select doctor, date and time", Toast.LENGTH_SHORT).show()
                binding.tvAvailabilityResult.visibility = View.GONE
                return@setOnClickListener
            }

            val key = selectedTime.replace(":", "_") + "_" + selectedDate.replace("-", "_")
            val doctorUid = selectedDoctorUid!!

            val dbRef = FirebaseDatabase.getInstance("https://anamaya-41e41e-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users")
                .child(doctorUid)

            val appointmentRequestsRef = dbRef.child("appointment_requests").child(key)
            val userAppointmentsRef = dbRef.child("user_appointments").child(key)
            val patientAppointmentsRef = dbRef.child("patient_appointments").child(key)

            appointmentRequestsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot1: DataSnapshot) {
                    if (snapshot1.exists()) {
                        showAvailability(false)
                    } else {
                        userAppointmentsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot2: DataSnapshot) {
                                if (snapshot2.exists()) {
                                    showAvailability(false)
                                } else {
                                    patientAppointmentsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(snapshot3: DataSnapshot) {
                                            if (snapshot3.exists()) {
                                                showAvailability(false)
                                            } else {
                                                showAvailability(true)
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            Log.e("CheckAvail", "Error (patient_appointments): ${error.message}")
                                            Toast.makeText(requireContext(), "Error checking availability", Toast.LENGTH_SHORT).show()
                                        }
                                    })
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("CheckAvail", "Error (user_appointments): ${error.message}")
                                Toast.makeText(requireContext(), "Error checking availability", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("CheckAvail", "Error (appointment_requests): ${error.message}")
                    Toast.makeText(requireContext(), "Error checking availability", Toast.LENGTH_SHORT).show()
                }
            })
        }

        binding.btnBookAppointment.setOnClickListener {
            if (selectedDate.isBlank() || selectedTime.isBlank() || selectedDoctorUid.isNullOrBlank()) {
                Toast.makeText(requireContext(), "Please select doctor, date and time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val key = selectedTime.replace(":", "_") + "_" + selectedDate.replace("-", "_")
            val doctorUid = selectedDoctorUid!!

            // Assume you already have current user's name and UID
            val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
            val dbRootRef = FirebaseDatabase.getInstance("https://anamaya-41e41e-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users")

            dbRootRef.child(currentUserUid).get().addOnSuccessListener { snapshot ->
                val patientName = snapshot.child("fullName").value?.toString() ?: "Unknown"
                val gender = snapshot.child("gender").value?.toString() ?: "Unknown"
                val allergies = snapshot.child("allergies").value?.toString() ?: "None"
                val medicalConditions = snapshot.child("medical_conditions").value?.toString() ?: "None"
                val dob = snapshot.child("dob").value?.toString() ?: "01/01/2000"
                val age = calculateAge(dob)

                val requestData = mapOf(
                    "date" to selectedDate,
                    "time" to selectedTime,
                    "patient_name" to patientName,
                    "patient_uid" to currentUserUid,
                    "age" to age,
                    "gender" to gender,
                    "allergies" to allergies,
                    "medical_conditions" to medicalConditions,
                    "notes" to binding.etPurpose.text.toString().trim()

                )

                dbRootRef.child(doctorUid)
                    .child("appointment_requests")
                    .child(key)
                    .setValue(requestData)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Appointment request sent!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to send request", Toast.LENGTH_SHORT).show()
                    }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Could not fetch patient info", Toast.LENGTH_SHORT).show()
            }
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
        val dialog = TimeSlotPickerDialogFragment { time ->
            selectedTime = time
            binding.tvSelectedTime.text = selectedTime
            binding.tvAvailabilityResult.visibility = View.GONE
        }
        dialog.show(parentFragmentManager, "TimeSlotPicker")
    }

    private fun showAvailability(available: Boolean) {
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

    fun calculateAge(dob: String): Int {
        // Format: DD/MM/YYYY
        val parts = dob.split("/")
        if (parts.size != 3) return 0
        val birthDay = parts[0].toInt()
        val birthMonth = parts[1].toInt()
        val birthYear = parts[2].toInt()

        val today = java.util.Calendar.getInstance()
        var age = today.get(java.util.Calendar.YEAR) - birthYear

        if (today.get(java.util.Calendar.MONTH) + 1 < birthMonth ||
            (today.get(java.util.Calendar.MONTH) + 1 == birthMonth && today.get(java.util.Calendar.DAY_OF_MONTH) < birthDay)) {
            age--
        }

        return age
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
