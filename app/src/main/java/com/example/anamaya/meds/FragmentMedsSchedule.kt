package com.example.anamaya.meds

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.anamaya.R
import com.example.anamaya.`class`.Medication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FragmentMedsSchedule : Fragment() {

    private lateinit var layoutSchedule: LinearLayout
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private val TAG = "MedsSchedule"

    private val allMedsList = mutableListOf<Medication>()
    private var medsToFetch = 0
    private var medsFetched = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.meds_fragment_schedule, container, false)
        layoutSchedule = view.findViewById(R.id.scheduleRoot)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://anamaya-41e41e-default-rtdb.asia-southeast1.firebasedatabase.app/")

        loadAndGroupMeds()

        return view
    }

    private fun loadAndGroupMeds() {
        val uid = auth.currentUser?.uid ?: return
        val userMedsRef = database.getReference("users/$uid/user_meds")
        val medsRef = database.getReference("meds")

        userMedsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(userMedsSnapshot: DataSnapshot) {
                if (!userMedsSnapshot.exists()) {
                    Log.d(TAG, "No user meds found.")
                    showEmptyState()
                    return
                }

                medsToFetch = userMedsSnapshot.childrenCount.toInt()
                medsFetched = 0
                allMedsList.clear()

                for (medSnapshot in userMedsSnapshot.children) {
                    val rawMedId = medSnapshot.child("med_id").getValue(String::class.java)
                    val amt = medSnapshot.child("amt").getValue(String::class.java)
                    val time = medSnapshot.child("time").getValue(String::class.java) ?: continue
                    val mealOption = medSnapshot.child("meal_option").getValue(String::class.java)

                    if (rawMedId.isNullOrBlank()) {
                        Log.w(TAG, "Skipping blank med ID")
                        medsFetched++
                        maybeRender()
                        continue
                    }

                    medsRef.child(rawMedId).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(medDetailsSnapshot: DataSnapshot) {
                            val name = medDetailsSnapshot.child("name").getValue(String::class.java) ?: rawMedId
                            val description = medDetailsSnapshot.child("description").getValue(String::class.java) ?: ""
                            val manufacturer = medDetailsSnapshot.child("manufacturer").getValue(String::class.java) ?: ""

                            val med = Medication(
                                name = name,
                                quantity = amt?.toIntOrNull() ?: 0,
                                id = rawMedId,
                                description = description,
                                manufacturer = manufacturer,
                                time = time,
                                mealOption = mealOption ?: ""
                            )

                            Log.d(TAG, "Fetched med: $med")
                            allMedsList.add(med)

                            medsFetched++
                            maybeRender()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e(TAG, "Error fetching med details: ${error.message}")
                            medsFetched++
                            maybeRender()
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "user_meds fetch failed: ${error.message}")
                showEmptyState()
            }
        })
    }

    private fun maybeRender() {
        Log.d(TAG, "Fetched $medsFetched of $medsToFetch")
        if (medsFetched >= medsToFetch) {
            if (allMedsList.isEmpty()) {
                showEmptyState()
            } else {
                val grouped = allMedsList.groupBy { it.time }.toSortedMap()
                Log.d(TAG, "Rendering grouped meds: $grouped")
                renderGroupedSchedule(grouped)
            }
        }
    }

    private fun showEmptyState() {
        layoutSchedule.removeAllViews()
        val emptyText = TextView(requireContext()).apply {
            text = "No meds scheduled."
            textSize = 16f
            setTextColor(ContextCompat.getColor(context, R.color.app_secondary_text_dark_grey))
            setPadding(32, 64, 32, 32)
        }
        layoutSchedule.addView(emptyText)
    }

    private fun renderGroupedSchedule(groupedMeds: Map<String, List<Medication>>) {
        layoutSchedule.removeAllViews()

        for ((time, medsAtTime) in groupedMeds) {
            val sectionView = layoutInflater.inflate(R.layout.item_meds_schedule, layoutSchedule, false)
            val tvTimeHeader = sectionView.findViewById<TextView>(R.id.tvTimeHeader)
            val container = sectionView.findViewById<LinearLayout>(R.id.containerMedEntries)

            tvTimeHeader.text = time

            for (med in medsAtTime) {
                val entryView = TextView(requireContext()).apply {
                    text = "${med.name} - ${med.quantity} pcs (${med.mealOption})"
                    textSize = 16f
                    setTextColor(ContextCompat.getColor(context, R.color.app_secondary_text_dark_grey))
                    setPadding(16, 8, 8, 8)
                }
                container.addView(entryView)
            }

            layoutSchedule.addView(sectionView)
        }
    }
}
