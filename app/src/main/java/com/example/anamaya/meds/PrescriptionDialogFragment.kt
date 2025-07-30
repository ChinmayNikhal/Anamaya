package com.example.anamaya.meds

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.anamaya.R
import com.example.anamaya.`class`.Prescription
import com.example.anamaya.databinding.DialogPrescriptionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID
import java.util.regex.Pattern

class PrescriptionDialogFragment : DialogFragment() {

    private lateinit var binding: DialogPrescriptionBinding

    // Firebase reference to /users
    private val databaseRef by lazy {
        FirebaseDatabase.getInstance("https://anamaya-41e41e-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("users")
    }

    companion object {
        private var currentPrescription: Prescription? = null

        fun newInstance(prescription: Prescription): PrescriptionDialogFragment {
            currentPrescription = prescription
            return PrescriptionDialogFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogPrescriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val presc = currentPrescription ?: return

        binding.etDoctor.setText(presc.doctorName)
        binding.etDate.setText(presc.date)

        val medsContainer = binding.medsListContainer.getChildAt(0) as LinearLayout
        medsContainer.removeAllViews()

        presc.medications.forEach { med ->
            val textView = TextView(requireContext()).apply {
                text = "• $med"
                setTextColor(ContextCompat.getColor(context, R.color.splash_primary_text))
                textSize = 16f
                setPadding(0, 4, 0, 4)
            }
            medsContainer.addView(textView)
        }

        if (!presc.imageUrl.isNullOrBlank()) {
            val fileName = Uri.parse(presc.imageUrl).lastPathSegment ?: "File"
            binding.tvImageFileName.text = "Attached: $fileName"
            binding.tvImageFileName.visibility = View.VISIBLE
        } else {
            binding.tvImageFileName.visibility = View.GONE
        }

        // View-only mode setup
        binding.etDoctor.isEnabled = false
        binding.etDate.isEnabled = false
        binding.viewButtons.visibility = View.VISIBLE
        binding.addButtons.visibility = View.GONE
        binding.btnAddMed.visibility = View.VISIBLE
        binding.btnUploadImage.visibility = View.GONE

        binding.btnClose.setOnClickListener { dismiss() }

        binding.btnAddMed.setOnClickListener {
            addMedsToUser()
        }
    }

    private fun addMedsToUser() {
        val presc = currentPrescription ?: return
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val medsRef = FirebaseDatabase.getInstance("https://anamaya-41e41e-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("users")
            .child(uid)
            .child("user_meds")

        medsRef.get().addOnSuccessListener { snapshot ->
            val existingMeds = snapshot.children.mapNotNull { it.value as? Map<*, *> }

            var addedCount = 0
            var duplicateCount = 0

            for (med in presc.medications) {
                val (name, amt, time, mealOption) = extractMedDetails(med)

                val isDuplicate = existingMeds.any {
                    it["med_id"] == name && it["amt"] == amt && it["time"] == time && it["meal_option"] == mealOption
                }

                if (isDuplicate) {
                    duplicateCount++
                    continue
                }

                val medId = UUID.randomUUID().toString()
                val medData = mapOf(
                    "med_id" to name,
                    "amt" to amt,
                    "time" to time,
                    "meal_option" to mealOption
                )

                medsRef.child(medId).setValue(medData).addOnSuccessListener {
                    addedCount++
                    if (addedCount + duplicateCount == presc.medications.size) {
                        val msg = buildString {
                            if (addedCount > 0) append("$addedCount meds added. ")
                            if (duplicateCount > 0) append("$duplicateCount already existed.")
                        }
                        Toast.makeText(requireContext(), msg.trim(), Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to add some meds", Toast.LENGTH_SHORT).show()
                }
            }

            // If all were duplicates
            if (presc.medications.isNotEmpty() && addedCount == 0 && duplicateCount == presc.medications.size) {
                Toast.makeText(requireContext(), "All medications already exist.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failed to check existing meds", Toast.LENGTH_SHORT).show()
        }
    }

    private fun extractMedDetails(medStr: String): Quadruple<String, String, String, String> {
        // Example: "Paracetamol 320mg Tablet - 2 pcs at 05:31 (After)"
        val pattern = Pattern.compile("^(.*?) - (\\d+) pcs at ([0-9]{2}:[0-9]{2}) \\((.*?)\\)$")
        val matcher = pattern.matcher(medStr)

        return if (matcher.find()) {
            val name = matcher.group(1)
            val amt = matcher.group(2)
            val time = matcher.group(3)
            val meal = matcher.group(4)
            Quadruple(name, amt, time, meal)
        } else {
            Quadruple(medStr, "1", "08:00", "Before") // fallback values
        }
    }

    data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}
