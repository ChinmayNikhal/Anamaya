package com.example.anamaya

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.anamaya.appointments.*
import com.example.anamaya.meds.*
import com.example.anamaya.profile.*
import com.example.anamaya.shop.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ContentHostActivity : AppCompatActivity() {

    private lateinit var contentHostTopTitle: TextView
    private lateinit var backButton: ImageButton
    private lateinit var contentHostBottomNavigation: BottomNavigationView

    private var currentMenuResId: Int = R.menu.main_bottom_nav_menu
    private var currentSelectedNavItemId: Int = R.id.nav_home

    private val dbRef by lazy {
        FirebaseDatabase.getInstance("https://anamaya-41e41e-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("users")
    }
    private val currentUserUid by lazy { FirebaseAuth.getInstance().currentUser?.uid.orEmpty() }

    companion object {
        const val EXTRA_FRAGMENT_NAME = "extra_fragment_name"
        const val EXTRA_FRAGMENT_TITLE = "extra_fragment_title"
        const val EXTRA_MENU_RES_ID = "extra_menu_res_id"
        const val EXTRA_SELECTED_NAV_ITEM_ID = "extra_selected_nav_item_id"

        const val FRAGMENT_MY_INFO = "MyInfoFragment"
        const val FRAGMENT_PRESCRIPTIONS = "PrescriptionsFragment"
        const val FRAGMENT_NOTIFICATIONS = "NotificationsDetailFragment"
        const val FRAGMENT_SUPPORT = "SupportFragment"
        const val FRAGMENT_SETTINGS = "SettingsFragment"

        const val FRAGMENT_MEDS_MY_MEDICATIONS = "FragmentMyMedications"
        const val FRAGMENT_MEDS_SCHEDULE = "FragmentMedsSchedule"
        const val FRAGMENT_MEDS_ASSIGN_PRESCRIPTION = "FragmentAssignPrescription"
        const val FRAGMENT_MEDS_VIEW_PRESCRIPTIONS = "FragmentViewPrescriptions"

        const val FRAGMENT_SHOP_MY_ORDERS = "MyOrdersFragment"
        const val FRAGMENT_SHOP_ORDERS_HISTORY = "OrdersHistoryFragment"
        const val FRAGMENT_SHOP_ORDER_NOW = "FragmentOrderNow"
        const val FRAGMENT_SHOP_SHOPPING_CART = "ShoppingCartFragment"
        const val FRAGMENT_PAYMENT_GATEWAY = "FragmentPaymentGateway"
        const val FRAGMENT_ORDER_RESULT = "FragmentOrderResult"

        const val FRAGMENT_APPOINTMENTS_MY_APPOINTMENTS = "FragmentMyAppointments"
        const val FRAGMENT_APPOINTMENTS_BOOK_APPOINTMENTS = "FragmentBookAppointments"
        const val FRAGMENT_APPOINTMENTS_MY_APPOINTMENTS_TODAY = "FragmentMyAppointmentsToday"
        const val FRAGMENT_APPOINTMENTS_DOCTOR_REQUESTS = "FragmentDoctorRequests"
    }

    private val profileFragmentMap = mapOf(
        R.id.nav_my_info to Pair(FRAGMENT_MY_INFO, "My Info"),
        R.id.nav_view_prescriptions to Pair(FRAGMENT_MEDS_VIEW_PRESCRIPTIONS, "Prescriptions"),
        R.id.nav_notifications to Pair(FRAGMENT_NOTIFICATIONS, "Notifications"),
        R.id.nav_settings to Pair(FRAGMENT_SETTINGS, "Settings"),
        R.id.nav_support to Pair(FRAGMENT_SUPPORT, "Support")
    )

    private var medsFragmentMap = mapOf(
        R.id.nav_my_meds to Pair(FRAGMENT_MEDS_MY_MEDICATIONS, "My Medications"),
        R.id.nav_meds_schedule to Pair(FRAGMENT_MEDS_SCHEDULE, "Add to Schedule"),
        R.id.nav_view_prescriptions to Pair(FRAGMENT_MEDS_VIEW_PRESCRIPTIONS, "View Prescriptions")
    )

    private val medsFragmentMapDoctor = medsFragmentMap + mapOf(
        R.id.nav_assign_prescription to Pair(FRAGMENT_MEDS_ASSIGN_PRESCRIPTION, "Assign Prescriptions")
    )

    private val appointmentsFragmentMapDefault = mapOf(
        R.id.nav_my_appointments to Pair(FRAGMENT_APPOINTMENTS_MY_APPOINTMENTS, "My Appointments"),
        R.id.nav_my_appointments_today to Pair(FRAGMENT_APPOINTMENTS_MY_APPOINTMENTS_TODAY, "Appointments Today"),
        R.id.nav_book_appointment to Pair(FRAGMENT_APPOINTMENTS_BOOK_APPOINTMENTS, "Book Appointment")
    )

    private val appointmentsFragmentMapDoctor = appointmentsFragmentMapDefault + mapOf(
        R.id.nav_doctor_requests to Pair(FRAGMENT_APPOINTMENTS_DOCTOR_REQUESTS, "Doctor Requests")
    )

    private val shopFragmentMap = mapOf(
        R.id.nav_my_orders to Pair(FRAGMENT_SHOP_MY_ORDERS, "My Orders"),
        R.id.nav_order_history to Pair(FRAGMENT_SHOP_ORDERS_HISTORY, "Order History"),
        R.id.nav_order_new to Pair(FRAGMENT_SHOP_ORDER_NOW, "Order New"),
        R.id.nav_cart to Pair(FRAGMENT_SHOP_SHOPPING_CART, "Shopping Cart")
    )

    private val mainNavMap = mapOf(
        R.id.nav_home to Pair("com.example.anamaya.MainActivity", R.id.nav_home),
        R.id.nav_profile to Pair("com.example.anamaya.ProfileActivity", R.id.nav_profile),
        R.id.nav_appointments to Pair("com.example.anamaya.ContentHostActivity", R.id.nav_appointments),
        R.id.nav_meds to Pair("com.example.anamaya.ContentHostActivity", R.id.nav_meds),
        R.id.nav_shop to Pair("com.example.anamaya.ContentHostActivity", R.id.nav_shop)
    )

    private var appointmentsFragmentMap: Map<Int, Pair<String, String>> = appointmentsFragmentMapDefault

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content_host)

        contentHostTopTitle = findViewById(R.id.content_host_top_title)
        backButton = findViewById(R.id.back_button)
        contentHostBottomNavigation = findViewById(R.id.content_host_bottom_navigation)

        backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val fragmentName = intent.getStringExtra(EXTRA_FRAGMENT_NAME)
        val fragmentTitle = intent.getStringExtra(EXTRA_FRAGMENT_TITLE) ?: "Details"
        currentMenuResId = intent.getIntExtra(EXTRA_MENU_RES_ID, R.menu.main_bottom_nav_menu)
        currentSelectedNavItemId = intent.getIntExtra(EXTRA_SELECTED_NAV_ITEM_ID, R.id.nav_home)

        contentHostTopTitle.text = fragmentTitle

        if (currentMenuResId == R.menu.appointment_menu) {
            dbRef.child(currentUserUid).child("isDoctor").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val isDoctor = snapshot.getValue(Boolean::class.java) == true
                    appointmentsFragmentMap = if (isDoctor) appointmentsFragmentMapDoctor else appointmentsFragmentMapDefault
                    currentMenuResId = if (isDoctor) R.menu.appointment_menu_docs else R.menu.appointment_menu
                    setupBottomNav(fragmentName)
                }

                override fun onCancelled(error: DatabaseError) {
                    appointmentsFragmentMap = appointmentsFragmentMapDefault
                    currentMenuResId = R.menu.appointment_menu
                    setupBottomNav(fragmentName)
                }
            })
        }
        else if (currentMenuResId == R.menu.meds_menu) {
            dbRef.child(currentUserUid).child("isDoctor").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val isDoctor = snapshot.getValue(Boolean::class.java) == true

                    medsFragmentMap = if (isDoctor) mapOf(
                        R.id.nav_my_meds to Pair(FRAGMENT_MEDS_MY_MEDICATIONS, "My Medications"),
                        R.id.nav_meds_schedule to Pair(FRAGMENT_MEDS_SCHEDULE, "Meds Schedule"),
                        R.id.nav_view_prescriptions to Pair(FRAGMENT_MEDS_VIEW_PRESCRIPTIONS, "View Prescriptions"),
                        R.id.nav_assign_prescription to Pair(FRAGMENT_MEDS_ASSIGN_PRESCRIPTION, "Assign Prescription")
                    ) else mapOf(
                        R.id.nav_my_meds to Pair(FRAGMENT_MEDS_MY_MEDICATIONS, "My Medications"),
                        R.id.nav_meds_schedule to Pair(FRAGMENT_MEDS_SCHEDULE, "Meds Schedule"),
                        R.id.nav_view_prescriptions to Pair(FRAGMENT_MEDS_VIEW_PRESCRIPTIONS, "View Prescriptions")
                    )

                    currentMenuResId = if (isDoctor) R.menu.meds_menu_docs else R.menu.meds_menu
                    setupBottomNav(fragmentName)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Fallback to default user menu if check fails
                    medsFragmentMap = mapOf(
                        R.id.nav_my_meds to Pair(FRAGMENT_MEDS_MY_MEDICATIONS, "My Medications"),
                        R.id.nav_meds_schedule to Pair(FRAGMENT_MEDS_SCHEDULE, "Meds Schedule"),
                        R.id.nav_view_prescriptions to Pair(FRAGMENT_MEDS_VIEW_PRESCRIPTIONS, "View Prescriptions")
                    )
                    currentMenuResId = R.menu.meds_menu
                    setupBottomNav(fragmentName)
                }
            })
        }
        else {
            setupBottomNav(fragmentName)
        }
    }

    private fun setupBottomNav(fragmentName: String?) {
        contentHostBottomNavigation.menu.clear()
        contentHostBottomNavigation.inflateMenu(currentMenuResId)

        contentHostBottomNavigation.post {
            contentHostBottomNavigation.menu.findItem(currentSelectedNavItemId)?.isChecked = true
        }

        if (supportFragmentManager.findFragmentById(R.id.content_host_fragment_container) == null) {
            val fragment = getFragmentInstance(fragmentName)
            if (fragment != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.content_host_fragment_container, fragment)
                    .commit()
            } else {
                Toast.makeText(this, "Error loading screen", Toast.LENGTH_LONG).show()
                finish()
            }
        }

        contentHostBottomNavigation.setOnItemSelectedListener { item ->
            val isMainNav = mainNavMap.containsKey(item.itemId)

            if (isMainNav) {
                mainNavMap[item.itemId]?.let { (className, selectedId) ->
                    val intent = Intent(this, Class.forName(className)).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
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
                                    putExtra(EXTRA_FRAGMENT_NAME, FRAGMENT_MEDS_VIEW_PRESCRIPTIONS)
                                    putExtra(EXTRA_FRAGMENT_TITLE, "Order New")
                                    putExtra(EXTRA_MENU_RES_ID, R.menu.shop_menu)
                                    putExtra(EXTRA_SELECTED_NAV_ITEM_ID, R.id.nav_shop)
                                }
                            }
                        }
                    }
                    startActivity(intent)
                    finish()
                    true
                } ?: false
            } else {
                val fragmentInfo = when (currentMenuResId) {
                    R.menu.profile_menu -> profileFragmentMap[item.itemId]
                    R.menu.meds_menu, R.menu.meds_menu_docs -> medsFragmentMap[item.itemId]
                    R.menu.appointment_menu, R.menu.appointment_menu_docs -> appointmentsFragmentMap[item.itemId]
                    R.menu.shop_menu -> shopFragmentMap[item.itemId]
                    else -> null
                }

                fragmentInfo?.let { (name, title) ->
                    val fragment = getFragmentInstance(name)
                    if (fragment != null) {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.content_host_fragment_container, fragment)
                            .commit()
                        contentHostTopTitle.text = title
                        true
                    } else false
                } ?: false
            }
        }
    }

    private fun getFragmentInstance(fragmentName: String?): Fragment? = when (fragmentName) {
        FRAGMENT_MY_INFO -> FragmentMyInfo()
        FRAGMENT_NOTIFICATIONS -> FragmentNotifications()
        FRAGMENT_SUPPORT -> FragmentSupport()
        FRAGMENT_SETTINGS -> FragmentSettings()

        FRAGMENT_MEDS_MY_MEDICATIONS -> FragmentMyMedications()
        FRAGMENT_MEDS_SCHEDULE -> FragmentMedsSchedule()
        FRAGMENT_MEDS_ASSIGN_PRESCRIPTION -> FragmentAssignPrescription()
        FRAGMENT_MEDS_VIEW_PRESCRIPTIONS -> FragmentViewPrescriptions()

        FRAGMENT_SHOP_MY_ORDERS -> FragmentMyOrders()
        FRAGMENT_SHOP_ORDERS_HISTORY -> FragmentOrdersHistory()
        FRAGMENT_SHOP_ORDER_NOW -> FragmentOrderNow()
        FRAGMENT_SHOP_SHOPPING_CART -> FragmentShoppingCart()
        FRAGMENT_PAYMENT_GATEWAY -> FragmentPaymentGateway()
        FRAGMENT_ORDER_RESULT -> FragmentOrderResult()

        FRAGMENT_APPOINTMENTS_MY_APPOINTMENTS -> FragmentMyAppointments()
        FRAGMENT_APPOINTMENTS_BOOK_APPOINTMENTS -> FragmentBookAppointments()
        FRAGMENT_APPOINTMENTS_MY_APPOINTMENTS_TODAY -> FragmentMyAppointmentsToday()
        FRAGMENT_APPOINTMENTS_DOCTOR_REQUESTS -> FragmentDoctorRequests()

        else -> null
    }
}
