package com.example.anamaya // IMPORTANT: Ensure this matches your actual package name

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileActivity : AppCompatActivity() {

    // UI elements from activity_profile_main.xml
    private lateinit var profilePic: ImageView
    private lateinit var welcomeLabel: TextView
    private lateinit var fullNameTv: TextView
    private lateinit var notificationBtn: ImageButton
    private lateinit var memberOptionsContainer: LinearLayout
    private lateinit var doctorOptionsContainer: LinearLayout
    private lateinit var bottomNavigationView: BottomNavigationView

    // User type (for demonstration; in a real app, this would come from login/session)
    private var isDoctorUser: Boolean = false // Set to true for doctor, false for member

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile) // Corrected to activity_profile_main

        // Initialize UI elements
        profilePic = findViewById(R.id.profile_pic)
        welcomeLabel = findViewById(R.id.welcome_label)
        fullNameTv = findViewById(R.id.full_name)
        notificationBtn = findViewById(R.id.notification_btn)
        memberOptionsContainer = findViewById(R.id.member_options_container)
        doctorOptionsContainer = findViewById(R.id.doctor_options_container)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // --- Simulate User Type (Replace with actual logic later) ---
        // isDoctorUser = true // Uncomment to test as a doctor
        // isDoctorUser = intent.getBooleanExtra("IS_DOCTOR_USER", false)

        // Set user name (simulated)
        fullNameTv.text = if (isDoctorUser) "Dr. Jane Smith" else "John Doe"

        // Show/Hide options based on user type
        if (isDoctorUser) {
            memberOptionsContainer.visibility = View.GONE
            doctorOptionsContainer.visibility = View.VISIBLE
        } else {
            memberOptionsContainer.visibility = View.VISIBLE
            doctorOptionsContainer.visibility = View.GONE
        }

        // --- Click Listeners for Profile Options ---
        // Member Options
        findViewById<LinearLayout>(R.id.option_my_info_member).setOnClickListener {
            launchContentHostActivity(
                ContentHostActivity.FRAGMENT_MY_INFO,
                "My Info",
                R.menu.profile_menu, // Pass the menu for Profile section
                R.id.nav_my_info // Highlight My Info icon within profile_menu
            )
        }
        findViewById<LinearLayout>(R.id.option_prescriptions_member).setOnClickListener {
            launchContentHostActivity(
                ContentHostActivity.FRAGMENT_PRESCRIPTIONS,
                "Prescriptions",
                R.menu.profile_menu, // Pass the menu for Profile section
                R.id.nav_view_prescriptions // Highlight View Prescriptions icon (as it's the closest match)
            )
        }
        findViewById<LinearLayout>(R.id.option_notifications_member).setOnClickListener {
            launchContentHostActivity(
                ContentHostActivity.FRAGMENT_NOTIFICATIONS,
                "Notifications",
                R.menu.profile_menu, // Pass the menu for Profile section
                R.id.nav_notifications // Highlight Notifications icon
            )
        }
        findViewById<LinearLayout>(R.id.option_support_member).setOnClickListener {
            launchContentHostActivity(
                ContentHostActivity.FRAGMENT_SUPPORT,
                "Support",
                R.menu.profile_menu, // Pass the menu for Profile section
                R.id.nav_support // Highlight Support icon
            )
        }
        findViewById<LinearLayout>(R.id.option_settings_member).setOnClickListener {
            launchContentHostActivity(
                ContentHostActivity.FRAGMENT_SETTINGS,
                "Settings",
                R.menu.profile_menu, // Pass the menu for Profile section
                R.id.nav_settings // Highlight Settings icon
            )
        }
        findViewById<LinearLayout>(R.id.option_logout_member).setOnClickListener {
            Toast.makeText(this, "Logout clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Class.forName("com.example.anamaya.LoginActivity"))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Doctor Options (These options are already defined in activity_profile_main.xml without "Prescriptions")
        findViewById<LinearLayout>(R.id.option_my_info_doctor).setOnClickListener {
            launchContentHostActivity(
                ContentHostActivity.FRAGMENT_MY_INFO,
                "My Info (Doctor)",
                R.menu.profile_menu, // Pass the menu for Profile section
                R.id.nav_my_info // Highlight My Info icon
            )
        }
        findViewById<LinearLayout>(R.id.option_notifications_doctor).setOnClickListener {
            launchContentHostActivity(
                ContentHostActivity.FRAGMENT_NOTIFICATIONS,
                "Notifications (Doctor)",
                R.menu.profile_menu, // Pass the menu for Profile section
                R.id.nav_notifications // Highlight Notifications icon
            )
        }
        findViewById<LinearLayout>(R.id.option_support_doctor).setOnClickListener {
            launchContentHostActivity(
                ContentHostActivity.FRAGMENT_SUPPORT,
                "Support (Doctor)",
                R.menu.profile_menu, // Pass the menu for Profile section
                R.id.nav_support // Highlight Support icon
            )
        }
        findViewById<LinearLayout>(R.id.option_settings_doctor).setOnClickListener {
            launchContentHostActivity(
                ContentHostActivity.FRAGMENT_SETTINGS,
                "Settings (Doctor)",
                R.menu.profile_menu, // Pass the menu for Profile section
                R.id.nav_settings // Highlight Settings icon
            )
        }
        findViewById<LinearLayout>(R.id.option_logout_doctor).setOnClickListener {
            Toast.makeText(this, "Logout clicked (Doctor)", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Class.forName("com.example.anamaya.LoginActivity"))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // --- Top Right Notification Button ---
        notificationBtn.setOnClickListener {
            Toast.makeText(this, "Notifications button clicked from Profile", Toast.LENGTH_SHORT).show()
            launchContentHostActivity(
                ContentHostActivity.FRAGMENT_NOTIFICATIONS,
                "Notifications",
                R.menu.profile_menu, // Pass the menu for Profile section
                R.id.nav_notifications // Highlight Notifications icon
            )
        }

        // --- Bottom Navigation Logic for ProfileActivity itself ---
        // Inflate main_bottom_nav_menu for ProfileActivity's bottom navigation
        bottomNavigationView.menu.clear()
        bottomNavigationView.inflateMenu(R.menu.main_bottom_nav_menu) // Use the main dashboard menu

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, Class.forName("com.example.anamaya.MainActivity"))
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }
                R.id.nav_profile -> {
                    true // Already on Profile main, no re-navigation needed
                }
                R.id.nav_appointments -> {

                    launchContentHostActivity(
                        ContentHostActivity.FRAGMENT_APPOINTMENTS_MY_APPOINTMENTS,
                        "My Appointments",
                        R.menu.appointment_menu, // Menu for Appointments section
                        R.id.nav_appointments // Highlight Appointments icon in its own menu
                    )
                    true
                }
                R.id.nav_meds -> {

                    launchContentHostActivity(
                        ContentHostActivity.FRAGMENT_MEDS_MY_MEDICATIONS,
                        "My Medications",
                        R.menu.meds_menu, // Menu for Meds section
                        R.id.nav_meds // Highlight Meds icon in its own menu
                    )
                    true
                }
                R.id.nav_shop -> {

                    launchContentHostActivity(
                        ContentHostActivity.FRAGMENT_SHOP_MY_ORDERS,
                        "My Orders",
                        R.menu.shop_menu, // Menu for Shop section
                        R.id.nav_shop // Highlight Shop icon in its own menu
                    )
                    true
                }
                else -> false
            }
        }
        // Set default selected item for bottom nav to Profile when on ProfileActivity
        bottomNavigationView.menu.findItem(R.id.nav_profile)?.isChecked = true
    }

    /**
     * Helper function to launch ContentHostActivity with a specific fragment, menu, and selected item.
     * @param fragmentName The name of the fragment to load (from ContentHostActivity.Companion).
     * @param title The title to pass to ContentHostActivity.
     * @param menuResId The resource ID of the menu to inflate in ContentHostActivity's BottomNavigationView.
     * @param selectedNavItemId The ID of the item to be selected in ContentHostActivity's BottomNavigationView.
     */
    private fun launchContentHostActivity(fragmentName: String, title: String, menuResId: Int, selectedNavItemId: Int) {
        val intent = Intent(this, Class.forName("com.example.anamaya.ContentHostActivity")).apply {
            putExtra(ContentHostActivity.EXTRA_FRAGMENT_NAME, fragmentName)
            putExtra(ContentHostActivity.EXTRA_FRAGMENT_TITLE, title)
            putExtra(ContentHostActivity.EXTRA_MENU_RES_ID, menuResId)
            putExtra(ContentHostActivity.EXTRA_SELECTED_NAV_ITEM_ID, selectedNavItemId)
        }
        startActivity(intent)
    }
}
