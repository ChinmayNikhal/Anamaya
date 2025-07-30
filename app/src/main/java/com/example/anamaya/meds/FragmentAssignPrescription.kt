package com.example.anamaya.meds

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.anamaya.R
import com.example.anamaya.profile.UserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class FragmentAssignPrescription : Fragment() {

    private lateinit var spinnerPatient: Spinner
    private lateinit var tvDoctorName: TextView
    private lateinit var tvPatientName: TextView
    private lateinit var tilDate: TextView
    private lateinit var btnAddMed: Button
    private lateinit var medsListLayout: LinearLayout
    private lateinit var btnUploadImage: Button
    private lateinit var ivPrescriptionImage: ImageView
    private lateinit var etNotes: EditText
    private lateinit var btnCancel: Button
    private lateinit var btnSend: Button

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    private val medicines = mutableListOf<String>()
    private var selectedImageUri: Uri? = null
    private var selectedPatientUid: String = ""
    private var doctorName: String = UserInfo.displayName ?: "Doctor"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.meds_fragment_assign_prescription, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bind views
        spinnerPatient = view.findViewById(R.id.spinnerPatient)
        tvDoctorName = view.findViewById(R.id.tvDoctorName)
        tvPatientName = view.findViewById(R.id.tvPatientName)
        tilDate = view.findViewById(R.id.tilDate)
        btnAddMed = view.findViewById(R.id.btnAddMed)
        btnUploadImage = view.findViewById(R.id.btnUploadImage)
        ivPrescriptionImage = view.findViewById(R.id.ivPrescriptionImage)
        btnCancel = view.findViewById(R.id.btnCancel)
        btnSend = view.findViewById(R.id.btnSend)
        medsListLayout = view.findViewById(R.id.medsListContainer)

        etNotes = EditText(requireContext()).apply {
            hint = "Additional Notes"
            setPadding(16, 16, 16, 16)
        }
        medsListLayout.addView(etNotes)

        // Set current date
        tilDate.text = "Date: " + SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        tvDoctorName.text = "Doctor: $doctorName"

//        setupPatientSpinnerFromAppointments()
        setupPatientSpinner()
        setupAddMedButton()
        setupUploadImage()

        btnCancel.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        btnSend.setOnClickListener {
            sendPrescription()
        }
    }

    private fun setupAddMedButton() {
        btnAddMed.setOnClickListener {
            DialogAddMedicineFragment { medSummary ->

                medicines.add(medSummary)

                // Create a horizontal layout to hold the med summary + delete button
                val itemLayout = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.HORIZONTAL
                    setPadding(8, 8, 8, 8)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                }

                // TextView for the medicine summary
                val medTextView = TextView(requireContext()).apply {
                    text = medSummary
                    setTextAppearance(android.R.style.TextAppearance_Material_Body1)
                    typeface = resources.getFont(R.font.roboto_mono)
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.app_secondary_text_dark_grey))
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                }

                // Delete button
                val deleteBtn = ImageButton(requireContext()).apply {
                    setImageResource(R.drawable.ic_delete) // Use your delete icon here
                    background = null
                    setColorFilter(ContextCompat.getColor(requireContext(), R.color.teal_700)) // Or your app's teal
                    setOnClickListener {
                        medicines.remove(medSummary)
                        medsListLayout.removeView(itemLayout)
                    }
                }

                // Add both to the horizontal layout
                itemLayout.addView(medTextView)
                itemLayout.addView(deleteBtn)

                // Insert *before* the notes EditText
                val notesIndex = medsListLayout.indexOfChild(etNotes)
                medsListLayout.addView(itemLayout, notesIndex)
            }.show(childFragmentManager, "AddMedicine")
        }
    }

    private fun setupUploadImage() {
        btnUploadImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            startActivityForResult(intent, REQUEST_IMAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            ivPrescriptionImage.setImageURI(selectedImageUri)
        }
    }

    private fun sendPrescription() {
        val doctorUid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val patientUid = selectedPatientUid
        if (patientUid.isBlank()) {
            Toast.makeText(requireContext(), "Please select a patient", Toast.LENGTH_SHORT).show()
            return
        }

        val db = FirebaseDatabase.getInstance("https://anamaya-41e41e-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .reference

        val prescriptionId = db.child("users").child(patientUid).child("user_prescription").push().key ?: return
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val prescriptionMap = mapOf(
            "prescriptionId" to prescriptionId,
            "date" to currentDate,
            "doctorUid" to doctorUid,
            "patientUid" to patientUid,
            "doctorName" to doctorName,
            "medications" to medicines,
            "notes" to etNotes.text.toString().trim(),
            "imageUri" to (selectedImageUri?.toString() ?: ""),
            "ttl" to (System.currentTimeMillis() + 90L * 24 * 60 * 60 * 1000)
        )

        val doctorAppointmentsRef = db.child("users").child(doctorUid).child("patient_appointments")
        doctorAppointmentsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var appointmentKey: String? = null

                for (apptSnap in snapshot.children) {
                    val apptPatientUid = apptSnap.child("patient_uid").getValue(String::class.java)
                    val apptDate = apptSnap.child("date").getValue(String::class.java)

                    if (apptPatientUid == patientUid && apptDate == currentDate) {
                        appointmentKey = apptSnap.key
                        break
                    }
                }

                if (appointmentKey == null) {
                    Toast.makeText(requireContext(), "Appointment not found for today.", Toast.LENGTH_SHORT).show()
                    return
                }

                val updates = hashMapOf<String, Any?>(
                    "/users/$patientUid/user_prescription/$prescriptionId" to prescriptionMap,
                    "/users/$doctorUid/sent_prescriptions/$prescriptionId" to true,
                    "/users/$doctorUid/patient_appointments/$appointmentKey" to null,
                    "/users/$patientUid/user_appointments/$appointmentKey" to null,
                    "/users/$patientUid/appointments_history/$appointmentKey" to true,
                    "/doctors/$doctorUid/appointments_history/$appointmentKey" to true
                )

                db.updateChildren(updates)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Prescription sent successfully", Toast.LENGTH_SHORT).show()
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to send: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }



    companion object {
        private const val REQUEST_IMAGE = 111
    }

    private fun setupPatientSpinner() {
        val doctorUid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val dbRef = FirebaseDatabase.getInstance("https://anamaya-41e41e-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("users").child(doctorUid).child("patient_appointments")

        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val seenUids = mutableSetOf<String>()
                val patientList = mutableListOf<Pair<String, String>>()

                for (apptSnap in snapshot.children) {
                    val name = apptSnap.child("patient_name").getValue(String::class.java)
                    val uid = apptSnap.child("patient_uid").getValue(String::class.java)
                    if (!uid.isNullOrBlank() && !name.isNullOrBlank() && seenUids.add(uid)) {
                        patientList.add(uid to name)
                    }
                }

                setSpinnerWithPatients(patientList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Could not load patients", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setSpinnerWithPatients(patientList: List<Pair<String, String>>) {
        val adapter = object : ArrayAdapter<String>(
            requireContext(),
            R.layout.item_spinner_patient,
            patientList.map { it.second }
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_spinner_patient, parent, false)
                val textView = view.findViewById<TextView>(R.id.spinnerText)
                textView.text = getItem(position)
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_spinner_patient, parent, false)
                val textView = view.findViewById<TextView>(R.id.spinnerText)
                textView.text = getItem(position)
                return view
            }
        }

        spinnerPatient.adapter = adapter

        spinnerPatient.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                selectedPatientUid = patientList[pos].first
                val name = patientList[pos].second
                tvPatientName.text = "Patient Name: $name"
                Log.d("PatientSpinner", "Selected: $name ($selectedPatientUid)")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

}
