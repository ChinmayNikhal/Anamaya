package com.example.anamaya

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    // Declare UI elements
    private lateinit var profilePic: ImageView
    private lateinit var welcomeLabel: TextView
    private lateinit var fullNameTv: TextView
    private lateinit var upcomingMedsTv: TextView
    private lateinit var notificationBtn: ImageButton
    private lateinit var imageGalleryBanner: ImageView
    private lateinit var bottomNavigationView: BottomNavigationView

    // Center buttons
    private lateinit var btnDoctors: Button
    private lateinit var btnOrderedMeds: Button
    private lateinit var btnChat: Button
    private lateinit var btnPharmacySearch: Button
    private lateinit var btnMedsSchedule: Button
    private lateinit var btnViewPrescriptions: Button
    private lateinit var btnTrackOrders: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Your main app screen layout

        // Initialize UI elements

        welcomeLabel = findViewById(R.id.welcome_label)
        fullNameTv = findViewById(R.id.full_name)
        upcomingMedsTv = findViewById(R.id.upcoming_meds)
        notificationBtn = findViewById(R.id.notification_btn)
        imageGalleryBanner = findViewById(R.id.image_gallery_banner)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        btnChat = findViewById(R.id.btn_chat)
        btnPharmacySearch = findViewById(R.id.btn_pharmacy_search)
        btnMedsSchedule = findViewById(R.id.btn_meds_schedule)
        btnViewPrescriptions = findViewById(R.id.btn_view_prescriptions)


        // --- Set up initial data (simulated) ---
        // In a real app, you would fetch this from user preferences or a database
        fullNameTv.text = "Jane Doe" // Example user name
        upcomingMedsTv.text = "Next: 3:30 PM - Vitamin D" // Example upcoming medication

        // --- Click Listeners for Buttons ---
        notificationBtn.setOnClickListener {
            Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show()
            Log.d("HomeActivity", "Notification button clicked")
            // TODO: Navigate to Notifications screen
        }

        imageGalleryBanner.setOnClickListener {
            Toast.makeText(this, "Banner clicked", Toast.LENGTH_SHORT).show()
            Log.d("HomeActivity", "Image banner clicked")
            // TODO: Navigate to a promotional/info page or image gallery
        }

        btnDoctors.setOnClickListener {
            Toast.makeText(this, "Doctors clicked", Toast.LENGTH_SHORT).show()
            Log.d("HomeActivity", "Doctors button clicked")
            // TODO: Navigate to Doctors list/booking screen
        }

        btnOrderedMeds.setOnClickListener {
            Toast.makeText(this, "Ordered Meds clicked", Toast.LENGTH_SHORT).show()
            Log.d("HomeActivity", "Ordered Meds button clicked")
            // TODO: Navigate to Ordered Medications history
        }

        btnChat.setOnClickListener {
            Toast.makeText(this, "Chat clicked", Toast.LENGTH_SHORT).show()
            Log.d("HomeActivity", "Chat button clicked")
            // TODO: Navigate to Chat screen
        }

        btnPharmacySearch.setOnClickListener {
            Toast.makeText(this, "Pharmacy Search clicked", Toast.LENGTH_SHORT).show()
            Log.d("HomeActivity", "Pharmacy Search button clicked")
            // TODO: Navigate to Pharmacy Search screen
        }

        btnMedsSchedule.setOnClickListener {
            Toast.makeText(this, "Meds Schedule clicked", Toast.LENGTH_SHORT).show()
            Log.d("HomeActivity", "Meds Schedule button clicked")
            // TODO: Navigate to Medication Schedule screen
        }

        btnViewPrescriptions.setOnClickListener {
            Toast.makeText(this, "View Prescriptions clicked", Toast.LENGTH_SHORT).show()
            Log.d("HomeActivity", "View Prescriptions button clicked")
            // TODO: Navigate to View Prescriptions screen
        }

        btnTrackOrders.setOnClickListener {
            Toast.makeText(this, "Track Orders clicked", Toast.LENGTH_SHORT).show()
            Log.d("HomeActivity", "Track Orders button clicked")
            // TODO: Navigate to Order Tracking screen
        }

        // --- Bottom Navigation Logic ---
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Home selected", Toast.LENGTH_SHORT).show()
                    Log.d("HomeActivity", "Bottom Nav: Home")
                    // Already on Home, so no navigation needed, maybe refresh content
                    true
                }
                R.id.nav_appointments -> {
                    Toast.makeText(this, "Appointments selected", Toast.LENGTH_SHORT).show()
                    Log.d("HomeActivity", "Bottom Nav: Appointments")
                    // TODO: Navigate to Appointments screen
                    true
                }
                R.id.nav_shop -> {
                    Toast.makeText(this, "Shop selected", Toast.LENGTH_SHORT).show()
                    Log.d("HomeActivity", "Bottom Nav: Shop")
                    // TODO: Navigate to Shop screen
                    true
                }
                R.id.nav_profile -> {
                    Toast.makeText(this, "Profile selected", Toast.LENGTH_SHORT).show()
                    Log.d("HomeActivity", "Bottom Nav: Profile")
                    // TODO: Navigate to Profile screen
                    true
                }
                else -> false
            }
        }
        // Set default selected item
        bottomNavigationView.selectedItemId = R.id.nav_home
    }
}
