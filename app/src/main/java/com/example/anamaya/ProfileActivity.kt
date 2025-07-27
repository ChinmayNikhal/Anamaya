package com.example.anamaya

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.anamaya.`class`.UserSession
import com.example.anamaya.profile.UserInfo
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileActivity : AppCompatActivity() {

    private lateinit var profilePic: ImageView
    private lateinit var welcomeLabel: TextView
    private lateinit var fullNameTv: TextView
    private lateinit var notificationBtn: ImageButton
    private lateinit var memberOptionsContainer: LinearLayout
    private lateinit var doctorOptionsContainer: LinearLayout
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var userSession: UserSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        userSession = UserSession(this)
        val isDoctorUser = userSession.isDoctor()

        profilePic = findViewById(R.id.profile_pic)
        welcomeLabel = findViewById(R.id.welcome_label)
        fullNameTv = findViewById(R.id.full_name)
        notificationBtn = findViewById(R.id.notification_btn)
        memberOptionsContainer = findViewById(R.id.member_options_container)
        doctorOptionsContainer = findViewById(R.id.doctor_options_container)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        val fullName = UserInfo.fullName
        fullNameTv.text = fullName ?: "User!"

        // Toggle visibility based on user type
        if (isDoctorUser) {
            memberOptionsContainer.visibility = View.GONE
            doctorOptionsContainer.visibility = View.VISIBLE
        } else {
            memberOptionsContainer.visibility = View.VISIBLE
            doctorOptionsContainer.visibility = View.GONE
        }

        // --- Member Options ---
        findViewById<LinearLayout>(R.id.option_my_info_member).setOnClickListener {
            launchContentHostActivity(
                ContentHostActivity.FRAGMENT_MY_INFO, "My Info",
                R.menu.profile_menu, R.id.nav_my_info
            )
        }

        findViewById<LinearLayout>(R.id.option_prescriptions_member).setOnClickListener {
            launchContentHostActivity(
                ContentHostActivity.FRAGMENT_PRESCRIPTIONS, "Prescriptions",
                R.menu.profile_menu, R.id.nav_view_prescriptions
            )
        }

        findViewById<LinearLayout>(R.id.option_notifications_member).setOnClickListener {
            launchContentHostActivity(
                ContentHostActivity.FRAGMENT_NOTIFICATIONS, "Notifications",
                R.menu.profile_menu, R.id.nav_notifications
            )
        }

        findViewById<LinearLayout>(R.id.option_support_member).setOnClickListener {
            launchContentHostActivity(
                ContentHostActivity.FRAGMENT_SUPPORT, "Support",
                R.menu.profile_menu, R.id.nav_support
            )
        }

        findViewById<LinearLayout>(R.id.option_settings_member).setOnClickListener {
            launchContentHostActivity(
                ContentHostActivity.FRAGMENT_SETTINGS, "Settings",
                R.menu.profile_menu, R.id.nav_settings
            )
        }

        findViewById<LinearLayout>(R.id.option_logout_member).setOnClickListener {
            handleLogout()
        }

        // --- Doctor Options ---
        findViewById<LinearLayout>(R.id.option_my_info_doctor).setOnClickListener {
            launchContentHostActivity(
                ContentHostActivity.FRAGMENT_MY_INFO, "My Info (Doctor)",
                R.menu.profile_menu, R.id.nav_my_info
            )
        }

        findViewById<LinearLayout>(R.id.option_notifications_doctor).setOnClickListener {
            launchContentHostActivity(
                ContentHostActivity.FRAGMENT_NOTIFICATIONS, "Notifications (Doctor)",
                R.menu.profile_menu, R.id.nav_notifications
            )
        }

        findViewById<LinearLayout>(R.id.option_support_doctor).setOnClickListener {
            launchContentHostActivity(
                ContentHostActivity.FRAGMENT_SUPPORT, "Support (Doctor)",
                R.menu.profile_menu, R.id.nav_support
            )
        }

        findViewById<LinearLayout>(R.id.option_settings_doctor).setOnClickListener {
            launchContentHostActivity(
                ContentHostActivity.FRAGMENT_SETTINGS, "Settings (Doctor)",
                R.menu.profile_menu, R.id.nav_settings
            )
        }

        findViewById<LinearLayout>(R.id.option_logout_doctor).setOnClickListener {
            handleLogout()
        }

        // --- Notification Bell ---
        notificationBtn.setOnClickListener {
            launchContentHostActivity(
                ContentHostActivity.FRAGMENT_NOTIFICATIONS, "Notifications",
                R.menu.profile_menu, R.id.nav_notifications
            )
        }

        // --- Bottom Nav ---
        bottomNavigationView.menu.clear()
        bottomNavigationView.inflateMenu(R.menu.main_bottom_nav_menu)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }
                R.id.nav_profile -> true
                R.id.nav_appointments -> {
                    launchContentHostActivity(
                        ContentHostActivity.FRAGMENT_APPOINTMENTS_MY_APPOINTMENTS, "My Appointments",
                        R.menu.appointment_menu, R.id.nav_appointments
                    )
                    true
                }
                R.id.nav_meds -> {
                    launchContentHostActivity(
                        ContentHostActivity.FRAGMENT_MEDS_MY_MEDICATIONS, "My Medications",
                        R.menu.meds_menu, R.id.nav_meds
                    )
                    true
                }
                R.id.nav_shop -> {
                    launchContentHostActivity(
                        ContentHostActivity.FRAGMENT_SHOP_MY_ORDERS, "My Orders",
                        R.menu.shop_menu, R.id.nav_shop
                    )
                    true
                }
                else -> false
            }
        }

        bottomNavigationView.menu.findItem(R.id.nav_profile)?.isChecked = true
    }

    private fun handleLogout() {
        Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show()

        userSession.clearSession()

        val splashIntent = Intent(this, SplashActivity::class.java)
        splashIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(splashIntent)
        finish()
    }

    private fun launchContentHostActivity(fragmentName: String, title: String, menuResId: Int, selectedNavItemId: Int) {
        val intent = Intent(this, ContentHostActivity::class.java).apply {
            putExtra(ContentHostActivity.EXTRA_FRAGMENT_NAME, fragmentName)
            putExtra(ContentHostActivity.EXTRA_FRAGMENT_TITLE, title)
            putExtra(ContentHostActivity.EXTRA_MENU_RES_ID, menuResId)
            putExtra(ContentHostActivity.EXTRA_SELECTED_NAV_ITEM_ID, selectedNavItemId)
        }
        startActivity(intent)
    }
}
