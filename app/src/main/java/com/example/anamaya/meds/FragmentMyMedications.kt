package com.example.anamaya.meds

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.anamaya.R
import com.example.anamaya.`class`.Medication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FragmentMyMedications : Fragment() {

    private lateinit var layoutMeds: LinearLayout
    private lateinit var textNoMeds: TextView
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private val TAG = "MyMeds"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.meds_fragment_my_medications, container, false)
        layoutMeds = view.findViewById(R.id.medicationsListContainer)
        textNoMeds = view.findViewById(R.id.textNoMeds)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://anamaya-41e41e-default-rtdb.asia-southeast1.firebasedatabase.app/")

        loadUserMeds()

        return view
    }

    private fun loadUserMeds() {
        val uid = auth.currentUser?.uid ?: return
        val userMedsRef = database.getReference("users/$uid/user_meds")
        val medsRef = database.getReference("meds")

        userMedsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(userMedsSnapshot: DataSnapshot) {
                layoutMeds.removeAllViews()

                if (!userMedsSnapshot.exists()) {
                    textNoMeds.isVisible = true
                    return
                }

                textNoMeds.isVisible = false

                for (medSnapshot in userMedsSnapshot.children) {
                    val rawMedId = medSnapshot.child("med_id").getValue(String::class.java)
                    val amt = medSnapshot.child("amt").getValue(String::class.java)
                    val time = medSnapshot.child("time").getValue(String::class.java)
                    val mealOption = medSnapshot.child("meal_option").getValue(String::class.java)

                    if (rawMedId.isNullOrBlank()) return

                    // Attempt direct lookup
                    medsRef.child(rawMedId).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(medDetailsSnapshot: DataSnapshot) {
                            if (medDetailsSnapshot.exists()) {
                                val name = medDetailsSnapshot.child("name").getValue(String::class.java) ?: "Unknown"
                                val description = medDetailsSnapshot.child("description").getValue(String::class.java) ?: ""
                                val manufacturer = medDetailsSnapshot.child("manufacturer").getValue(String::class.java) ?: ""

                                displayMed(rawMedId, name, description, manufacturer, amt, time, mealOption)
                            } else {
                                Log.d(TAG, "med_id not found directly: $rawMedId, trying reverse lookup")
                                // Reverse lookup by name instead
                                medsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(medsSnapshot: DataSnapshot) {
                                        var matched = false
                                        for (med in medsSnapshot.children) {
                                            val name = med.child("name").getValue(String::class.java)
                                            if (name == rawMedId) {
                                                val medId = med.key ?: continue
                                                val description = med.child("description").getValue(String::class.java) ?: ""
                                                val manufacturer = med.child("manufacturer").getValue(String::class.java) ?: ""

                                                displayMed(medId, name, description, manufacturer, amt, time, mealOption)
                                                matched = true
                                                break
                                            }
                                        }
                                        if (!matched) {
                                            Log.d(TAG, "Reverse lookup failed for med name: $rawMedId")
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Log.e(TAG, "Reverse lookup failed: ${error.message}")
                                    }
                                })
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e(TAG, "Failed to fetch med details: ${error.message}")
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to fetch user_meds: ${error.message}")
            }
        })
    }

    private fun displayMed(
        medId: String,
        name: String,
        description: String,
        manufacturer: String,
        amt: String?,
        time: String?,
        mealOption: String?
    ) {
        val med = Medication(
            name = name,
            quantity = amt?.toIntOrNull() ?: 0,
            id = medId,
            description = description,
            manufacturer = manufacturer,
            time = time ?: "",
            mealOption = mealOption ?: ""
        )

        val medInfo = "$name - ${amt ?: "?"} pcs at ${time ?: "?"} (${mealOption ?: "?"})"

        val textView = TextView(requireContext()).apply {
            text = medInfo
            textSize = 16f
            setTextColor(ContextCompat.getColor(context, R.color.app_text_black))
            setPadding(32, 24, 32, 24)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 16)
            }
            background = ContextCompat.getDrawable(context, R.drawable.rounded_rectangle_outline)
            setOnClickListener {
                FragmentAddMedicineDialog(med).show(childFragmentManager, "view_medicine")
            }
        }

        layoutMeds.addView(textView)
    }

}
