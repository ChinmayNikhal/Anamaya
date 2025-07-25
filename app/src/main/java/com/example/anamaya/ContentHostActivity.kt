package com.example.anamaya // IMPORTANT: Ensure this matches your actual package name

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.anamaya.profile.FragmentMyInfo
import com.example.anamaya.profile.FragmentNotifications

import com.example.anamaya.profile.FragmentSettings
import com.example.anamaya.profile.FragmentSupport
import com.example.anamaya.meds.FragmentMyMedications
import com.example.anamaya.meds.FragmentAddToSchedule
import com.example.anamaya.meds.FragmentViewPrescriptions
import com.example.anamaya.shop.FragmentMyOrders
import com.example.anamaya.shop.FragmentOrdersHistory
import com.example.anamaya.shop.FragmentShoppingCart
import com.example.anamaya.meds.FragmentOrderNow
import com.example.anamaya.appointments.FragmentMyAppointments
import com.example.anamaya.appointments.FragmentBookAppointments
import com.example.anamaya.appointments.FragmentMyAppointmentsToday
import com.example.anamaya.appointments.FragmentMedicineSchedule
import com.example.anamaya.shop.FragmentOrderResult
import com.example.anamaya.shop.FragmentPaymentGateway
import com.google.android.material.bottomnavigation.BottomNavigationView

class ContentHostActivity : AppCompatActivity() {

    private lateinit var contentHostTopTitle: TextView
    private lateinit var backButton: ImageButton
    private lateinit var contentHostBottomNavigation: BottomNavigationView

    // Store the current menu and selected item IDs passed to this instance
    private var currentMenuResId: Int = R.menu.main_bottom_nav_menu // Default to main nav
    private var currentSelectedNavItemId: Int = R.id.nav_home // Default to home

    companion object {
        const val EXTRA_FRAGMENT_NAME = "extra_fragment_name"
        const val EXTRA_FRAGMENT_TITLE = "extra_fragment_title"
        const val EXTRA_MENU_RES_ID = "extra_menu_res_id"
        const val EXTRA_SELECTED_NAV_ITEM_ID = "extra_selected_nav_item_id"

        // Constants for Profile section fragments
        const val FRAGMENT_MY_INFO = "MyInfoFragment"
        const val FRAGMENT_PRESCRIPTIONS = "PrescriptionsFragment" // Profile Prescriptions
        const val FRAGMENT_NOTIFICATIONS = "NotificationsDetailFragment"
        const val FRAGMENT_SUPPORT = "SupportFragment"
        const val FRAGMENT_SETTINGS = "SettingsFragment"

        // Constants for Meds section fragments
        const val FRAGMENT_MEDS_MY_MEDICATIONS = "FragmentMyMedications"
        const val FRAGMENT_MEDS_ADD_TO_SCHEDULE = "FragmentAddToSchedule"
        const val FRAGMENT_MEDS_ORDER_NEW = "FragmentOrderNow"
        const val FRAGMENT_MEDS_VIEW_PRESCRIPTIONS = "FragmentViewPrescriptions" // Meds View Prescriptions

        // Constants for Shop section fragments
        const val FRAGMENT_SHOP_MY_ORDERS = "MyOrdersFragment"
        const val FRAGMENT_SHOP_ORDERS_HISTORY = "OrdersHistoryFragment"
        const val FRAGMENT_SHOP_ORDER_NOW = "FragmentOrderNow"
        const val FRAGMENT_SHOP_SHOPPING_CART = "ShoppingCartFragment"
        const val FRAGMENT_PAYMENT_GATEWAY = "FragmentPaymentGateway"
        const val FRAGMENT_ORDER_RESULT = "FragmentOrderResult"


        // Constants for Appointments section fragments
        const val FRAGMENT_APPOINTMENTS_MY_APPOINTMENTS = "FragmentMyAppointments"
        const val FRAGMENT_APPOINTMENTS_BOOK_APPOINTMENTS = "FragmentBookAppointments"
        const val FRAGMENT_APPOINTMENTS_MY_APPOINTMENTS_TODAY = "FragmentMyAppointmentsToday"
        const val FRAGMENT_APPOINTMENTS_MEDICINE_SCHEDULE = "FragmentMedicineSchedule"

        // Placeholder fragment name for main dashboard (if needed as a fragment)
        const val FRAGMENT_MAIN_DASHBOARD = "MainDashboardFragment"
    }

    // --- Mappings for each menu's internal fragments ---
    private val profileFragmentMap = mapOf(
        R.id.nav_my_info to Pair(FRAGMENT_MY_INFO, "My Info"),
        R.id.nav_view_prescriptions to Pair(FRAGMENT_MEDS_VIEW_PRESCRIPTIONS, "Prescriptions"),
        R.id.nav_notifications to Pair(FRAGMENT_NOTIFICATIONS, "Notifications"),
        R.id.nav_settings to Pair(FRAGMENT_SETTINGS, "Settings"),
        R.id.nav_support to Pair(FRAGMENT_SUPPORT, "Support")
    )

    private val medsFragmentMap = mapOf(
        R.id.nav_my_meds to Pair(FRAGMENT_MEDS_MY_MEDICATIONS, "My Medications"),
        R.id.nav_add_to_schedule to Pair(FRAGMENT_MEDS_ADD_TO_SCHEDULE, "Add to Schedule"),
        R.id.nav_order_new to Pair(FRAGMENT_MEDS_ORDER_NEW, "Order New"),
        R.id.nav_view_prescriptions to Pair(FRAGMENT_MEDS_VIEW_PRESCRIPTIONS, "View Prescriptions")
    )

    private val appointmentsFragmentMap = mapOf(
        R.id.nav_my_appointments to Pair(FRAGMENT_APPOINTMENTS_MY_APPOINTMENTS, "My Appointments"),
        R.id.nav_my_appointments_today to Pair(FRAGMENT_APPOINTMENTS_MY_APPOINTMENTS_TODAY, "Appointments Today"),
        R.id.nav_book_appointment to Pair(FRAGMENT_APPOINTMENTS_BOOK_APPOINTMENTS, "Book Appointment"),
        R.id.nav_medicine_schedule to Pair(FRAGMENT_APPOINTMENTS_MEDICINE_SCHEDULE, "Medicine Schedule")
    )

    private val shopFragmentMap = mapOf(
        R.id.nav_my_orders to Pair(FRAGMENT_SHOP_MY_ORDERS, "My Orders"),
        R.id.nav_order_history to Pair(FRAGMENT_SHOP_ORDERS_HISTORY, "Order History"),
        R.id.nav_order_new to Pair(FRAGMENT_SHOP_ORDER_NOW, "Order New"),
        R.id.nav_cart to Pair(FRAGMENT_SHOP_SHOPPING_CART, "Shopping Cart")
    )

    // Main bottom nav map (for navigation *from* ContentHostActivity *to* MainActivity)
    // This map is less about fragments and more about mapping icon IDs to their target Activity/Section
    private val mainNavMap = mapOf(
        R.id.nav_home to Pair("com.example.anamaya.MainActivity", R.id.nav_home),
        R.id.nav_profile to Pair("com.example.anamaya.ProfileActivity", R.id.nav_profile),
        R.id.nav_appointments to Pair("com.example.anamaya.ContentHostActivity", R.id.nav_appointments),
        R.id.nav_meds to Pair("com.example.anamaya.ContentHostActivity", R.id.nav_meds),
        R.id.nav_shop to Pair("com.example.anamaya.ContentHostActivity", R.id.nav_shop)
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content_host)

        contentHostTopTitle = findViewById(R.id.content_host_top_title)
        backButton = findViewById(R.id.back_button)
        contentHostBottomNavigation = findViewById(R.id.content_host_bottom_navigation)

        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val fragmentName = intent.getStringExtra(EXTRA_FRAGMENT_NAME)
        val fragmentTitle = intent.getStringExtra(EXTRA_FRAGMENT_TITLE) ?: "Details"
        currentMenuResId = intent.getIntExtra(EXTRA_MENU_RES_ID, R.menu.main_bottom_nav_menu) // Use main_bottom_nav as ultimate default
        currentSelectedNavItemId = intent.getIntExtra(EXTRA_SELECTED_NAV_ITEM_ID, R.id.nav_home)

        Log.d("ContentHostActivity", "Received Fragment Name: $fragmentName")
        Log.d("ContentHostActivity", "Received Fragment Title: $fragmentTitle")
        Log.d("ContentHostActivity", "Received Menu Res ID: ${resources.getResourceEntryName(currentMenuResId)}")
        Log.d("ContentHostActivity", "Received Selected Nav Item ID: ${resources.getResourceEntryName(currentSelectedNavItemId)}")

        contentHostTopTitle.text = fragmentTitle

        // --- Set the dynamic menu for the BottomNavigationView ---
        contentHostBottomNavigation.menu.clear()
        contentHostBottomNavigation.inflateMenu(currentMenuResId)
        contentHostBottomNavigation.post {
            contentHostBottomNavigation.menu.findItem(currentSelectedNavItemId)?.isChecked = true
        }

        if (savedInstanceState == null) { // Only add fragment if it's the first time
            val fragment = getFragmentInstance(fragmentName)
            if (fragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.content_host_fragment_container, fragment)
                    .commit()
            } else {
                Toast.makeText(this, "Error: Fragment not found", Toast.LENGTH_LONG).show()
                Log.e("ContentHostActivity", "Unknown fragment name: $fragmentName")
                finish()
            }
        }

        // --- Bottom Navigation View Setup: Handle clicks to navigate between main sections or within current section ---
        contentHostBottomNavigation.setOnItemSelectedListener { item ->
            // Check if the clicked item is one of the main navigation items (Home, Profile, Appointments, Meds, Shop)
            val isMainNavItem = when (item.itemId) {
                R.id.nav_home, R.id.nav_profile, R.id.nav_appointments, R.id.nav_meds, R.id.nav_shop -> true
                else -> false
            }

            if (isMainNavItem) {
                // Navigate to a different main section (or back to MainActivity/ProfileActivity)
                val targetClassAndId = mainNavMap[item.itemId]
                targetClassAndId?.let { (className, selectedId) ->
                    val intent = Intent(this, Class.forName(className)).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        // If navigating to another ContentHostActivity, pass its default fragment/menu/selected item
                        if (className == "com.example.anamaya.ContentHostActivity") {
                            when (selectedId) {
                                R.id.nav_appointments -> {
                                    putExtra(EXTRA_FRAGMENT_NAME, FRAGMENT_APPOINTMENTS_MY_APPOINTMENTS)
                                    putExtra(EXTRA_FRAGMENT_TITLE, "My Appointments")
                                    putExtra(EXTRA_MENU_RES_ID, R.menu.appointment_menu)
                                    putExtra(EXTRA_SELECTED_NAV_ITEM_ID, R.id.nav_appointments)
                                }
                                R.id.nav_meds -> {
                                    putExtra(EXTRA_FRAGMENT_NAME, FRAGMENT_MEDS_MY_MEDICATIONS)
                                    putExtra(EXTRA_FRAGMENT_TITLE, "My Medications")
                                    putExtra(EXTRA_MENU_RES_ID, R.menu.meds_menu)
                                    putExtra(EXTRA_SELECTED_NAV_ITEM_ID, R.id.nav_meds)
                                }
                                R.id.nav_shop -> {
                                    putExtra(EXTRA_FRAGMENT_NAME, FRAGMENT_MEDS_ORDER_NEW)
                                    putExtra(EXTRA_FRAGMENT_TITLE, "Order New")
                                    putExtra(EXTRA_MENU_RES_ID, R.menu.shop_menu)
                                    putExtra(EXTRA_SELECTED_NAV_ITEM_ID, R.id.nav_shop)
                                }
                            }
                        }
                    }
                    startActivity(intent)
                    finish() // Finish current ContentHostActivity if navigating to a new main section
                    true
                } ?: false
            } else {
                // Navigate within the current section (replace fragment)
                val fragmentInfo = when (currentMenuResId) {
                    R.menu.profile_menu -> profileFragmentMap[item.itemId]
                    R.menu.meds_menu -> medsFragmentMap[item.itemId]
                    R.menu.appointment_menu -> appointmentsFragmentMap[item.itemId]
                    R.menu.shop_menu -> shopFragmentMap[item.itemId]
                    else -> null
                }

                fragmentInfo?.let { (fragmentName, fragmentTitle) ->
                    val fragment = getFragmentInstance(fragmentName)
                    if (fragment != null) {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.content_host_fragment_container, fragment)
                            .commit()
                        contentHostTopTitle.text = fragmentTitle // Update top bar title
                        true // Item handled
                    } else {
                        Log.e("ContentHostActivity", "Fragment instance not found for $fragmentName")
                        false // Item not handled
                    }
                } ?: run {
                    Log.w("ContentHostActivity", "No fragment mapping found for item ID: ${item.itemId} in menu ${resources.getResourceEntryName(currentMenuResId)}")
                    false // Item not handled
                }
            }
        }
    }

//
//    Helper function to get the correct Fragment instance based on the provided name.
//
    private fun getFragmentInstance(fragmentName: String?): Fragment? {
        return when (fragmentName) {
            // Profile Section Fragments
            FRAGMENT_MY_INFO -> FragmentMyInfo()
            FRAGMENT_NOTIFICATIONS -> FragmentNotifications()
            FRAGMENT_SUPPORT -> FragmentSupport()
            FRAGMENT_SETTINGS -> FragmentSettings()

            // Meds Section Fragments
            FRAGMENT_MEDS_MY_MEDICATIONS -> FragmentMyMedications()
            FRAGMENT_MEDS_ADD_TO_SCHEDULE -> FragmentAddToSchedule()
            FRAGMENT_MEDS_ORDER_NEW -> FragmentOrderNow()
            FRAGMENT_MEDS_VIEW_PRESCRIPTIONS -> FragmentViewPrescriptions()

            // Shop Section Fragments
            FRAGMENT_SHOP_MY_ORDERS -> FragmentMyOrders()
            FRAGMENT_SHOP_ORDERS_HISTORY -> FragmentOrdersHistory()
            FRAGMENT_SHOP_ORDER_NOW -> FragmentOrderNow()
            FRAGMENT_SHOP_SHOPPING_CART -> FragmentShoppingCart()
            FRAGMENT_PAYMENT_GATEWAY -> FragmentPaymentGateway()
            FRAGMENT_ORDER_RESULT -> FragmentOrderResult()


            // Appointments Section Fragments
            FRAGMENT_APPOINTMENTS_MY_APPOINTMENTS -> FragmentMyAppointments()
            FRAGMENT_APPOINTMENTS_BOOK_APPOINTMENTS -> FragmentBookAppointments()
            FRAGMENT_APPOINTMENTS_MY_APPOINTMENTS_TODAY -> FragmentMyAppointmentsToday()
            FRAGMENT_APPOINTMENTS_MEDICINE_SCHEDULE -> FragmentMedicineSchedule()

            else -> null
        }
    }
}
