package com.example.anamaya.appointments

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.anamaya.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FragmentDoctorRequests : Fragment() {

    private lateinit var containerLayout: LinearLayout
    private val dbRef by lazy {
        FirebaseDatabase.getInstance("https://anamaya-41e41e-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("users")
    }

    private val doctorUid by lazy { FirebaseAuth.getInstance().currentUser?.uid.orEmpty() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.appointments_fragment_doctor_request_page, container, false)
        containerLayout = view.findViewById(R.id.llRequestsContainer)
        fetchRequests()
        return view
    }

    private fun fetchRequests() {
        val requestRef = dbRef.child(doctorUid).child("appointment_requests")

        requestRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                containerLayout.removeAllViews()
                val grouped = mutableMapOf<String, MutableList<DataSnapshot>>()

                for (snap in snapshot.children) {
                    val key = snap.key ?: continue
                    val datePart = key.split("_").drop(2).joinToString("-") // e.g. 20-07-2025
                    grouped.getOrPut(datePart) { mutableListOf() }.add(snap)
                }

                for ((date, requests) in grouped.entries.sortedBy { it.key }) {
                    val dateLabel = TextView(requireContext()).apply {
                        text = date
                        setPadding(0, 20, 0, 10)
                        setTextColor(resources.getColor(R.color.app_primary_teal, null))
                        textSize = 18f
                        setTypeface(null, android.graphics.Typeface.BOLD)
                    }
                    containerLayout.addView(dateLabel)

                    for (snap in requests) {
                        val data = snap.value as? Map<*, *> ?: continue
                        val time = snap.key?.split("_")?.take(2)?.joinToString(":") ?: "Time Unknown"
                        val name = data["patient_name"] as? String ?: "Unknown"
                        val btn = Button(requireContext()).apply {
                            text = "$time - $name"
                            setBackgroundColor(resources.getColor(R.color.teal_700, null))
                            setTextColor(resources.getColor(android.R.color.white, null))
                            setOnClickListener { showDialog(snap.key.orEmpty(), data) }
                        }
                        containerLayout.addView(btn)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun showDialog(key: String, data: Map<*, *>) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_appointment)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        dialog.findViewById<TextView>(R.id.tvDialogTime).text = "Time: " + key.split("_").take(2).joinToString(":")
        dialog.findViewById<TextView>(R.id.tvDialogDate).text = "Date: " + key.split("_").drop(2).joinToString("-")

        dialog.findViewById<TextView>(R.id.tvDialogDoctor).visibility = View.GONE
        dialog.findViewById<TextView>(R.id.tvDialogSpecialization).visibility = View.GONE

        dialog.findViewById<TextView>(R.id.tvDialogPatientName).apply {
            text = "Patient: ${data["patient_name"]}"
            visibility = View.VISIBLE
        }

        dialog.findViewById<TextView>(R.id.tvDialogAgeGender).apply {
            val age = data["age"]
            val gender = data["gender"]
            text = "Age: $age, Gender: $gender"
            visibility = View.VISIBLE
        }

        dialog.findViewById<TextView>(R.id.tvDialogPurpose).text = "Purpose: ${data["notes"] ?: "-"}"
        dialog.findViewById<TextView>(R.id.tvDialogNotes).visibility = View.GONE

        dialog.findViewById<LinearLayout>(R.id.layoutDoctorButtons).visibility = View.VISIBLE

        dialog.findViewById<Button>(R.id.btnDialogClose).setOnClickListener {
            dialog.dismiss()
        }

        dialog.findViewById<Button>(R.id.btnAcceptAppointment).setOnClickListener {
            val patientUid = data["patient_uid"] as? String
            if (patientUid.isNullOrBlank()) {
                Toast.makeText(requireContext(), "Invalid patient data", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                return@setOnClickListener
            }

            val appointmentData = mapOf(
                "date" to data["date"],
                "time" to data["time"],
                "patient_name" to data["patient_name"],
                "age" to data["age"],
                "gender" to data["gender"],
                "allergies" to data["allergies"],
                "medical_conditions" to data["medical_conditions"],
                "notes" to data["notes"],
                "doctor_uid" to doctorUid
            )

            val updates = hashMapOf<String, Any?>(
                "/$doctorUid/appointment_requests/$key" to null, // delete from requests
                "/$doctorUid/patient_appointments/$key" to appointmentData,
                "/$patientUid/user_appointments/$key" to appointmentData
            )

            dbRef.updateChildren(updates).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(requireContext(), "Appointment Accepted", Toast.LENGTH_SHORT).show()
                    fetchRequests()
                } else {
                    Toast.makeText(requireContext(), "Error updating records", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
        }

        dialog.findViewById<Button>(R.id.btnDeclineAppointment).setOnClickListener {
            dbRef.child(doctorUid).child("appointment_requests").child(key).removeValue()
            Toast.makeText(requireContext(), "Declined & removed", Toast.LENGTH_SHORT).show()
            fetchRequests()
            dialog.dismiss()
        }

        dialog.show()
    }
}
