package com.example.anamaya
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var imageGalleryBanner: ImageView
    private lateinit var adImages: List<Int>
    private var currentImageIndex = 0
    private val handler = Handler(Looper.getMainLooper())
    private val DELAY_MILLISECONDS = 5000L

    private val imageChanger = object : Runnable {
        override fun run() {
            currentImageIndex = (currentImageIndex + 1) % adImages.size
            imageGalleryBanner.setImageResource(adImages[currentImageIndex])
            handler.postDelayed(this, DELAY_MILLISECONDS)
        }
    }

    private lateinit var welcomeLabel: TextView
    private lateinit var fullNameTv: TextView
    private lateinit var upcomingMedsTv: TextView
    private lateinit var notificationBtn: ImageButton
    private lateinit var bottomNavigationView: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageGalleryBanner = findViewById(R.id.image_gallery_banner)

        welcomeLabel = findViewById(R.id.welcome_label)
        fullNameTv = findViewById(R.id.full_name)
        upcomingMedsTv = findViewById(R.id.upcoming_meds)
        notificationBtn = findViewById(R.id.notification_btn)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        adImages = listOf(
            R.drawable.ad_1,
            R.drawable.ad_2,
            R.drawable.ad_3,
            R.drawable.ad_4
        )

        if (adImages.isNotEmpty()) {
            imageGalleryBanner.setImageResource(adImages[currentImageIndex])
        }

        fullNameTv.text = "John Doe"
        upcomingMedsTv.text = "Next: 2:00 PM - Paracetamol"

        notificationBtn.setOnClickListener {
            Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show()
            Log.d("MainActivity", "Notification button clicked")
        }

        imageGalleryBanner.setOnClickListener {
            Toast.makeText(this, "Banner clicked", Toast.LENGTH_SHORT).show()
            Log.d("MainActivity", "Image banner clicked")
        }

        // --- Bottom Navigation View Setup ---
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {

                    Log.d("MainActivity", "Bottom Nav: Home")
                    true // Already on Home screen
                }
                R.id.nav_profile -> {

                    Log.d("MainActivity", "Bottom Nav: Profile")
                    val intent = Intent(this, Class.forName("com.example.anamaya.ProfileActivity"))
                    startActivity(intent)
                    true
                }
                R.id.nav_appointments -> {

                    Log.d("MainActivity", "Bottom Nav: Appointments")
                    val intent = Intent(this, Class.forName("com.example.anamaya.ContentHostActivity")).apply {
                        putExtra(ContentHostActivity.EXTRA_FRAGMENT_NAME, ContentHostActivity.FRAGMENT_APPOINTMENTS_MY_APPOINTMENTS)
                        putExtra(ContentHostActivity.EXTRA_FRAGMENT_TITLE, "My Appointments")
                        putExtra(ContentHostActivity.EXTRA_MENU_RES_ID, R.menu.appointment_menu) // Pass the correct menu
                        putExtra(ContentHostActivity.EXTRA_SELECTED_NAV_ITEM_ID, R.id.nav_appointments) // Pass the correct selected item
                    }
                    startActivity(intent)
                    true
                }
                R.id.nav_meds -> {

                    Log.d("MainActivity", "Bottom Nav: Meds")
                    val intent = Intent(this, Class.forName("com.example.anamaya.ContentHostActivity")).apply {
                        putExtra(ContentHostActivity.EXTRA_FRAGMENT_NAME, ContentHostActivity.FRAGMENT_MEDS_MY_MEDICATIONS)
                        putExtra(ContentHostActivity.EXTRA_FRAGMENT_TITLE, "My Medications")
                        putExtra(ContentHostActivity.EXTRA_MENU_RES_ID, R.menu.meds_menu) // Pass the correct menu
                        putExtra(ContentHostActivity.EXTRA_SELECTED_NAV_ITEM_ID, R.id.nav_meds) // Pass the correct selected item
                    }
                    startActivity(intent)
                    true
                }
                R.id.nav_shop -> {

                    Log.d("MainActivity", "Bottom Nav: Shop")
                    val intent = Intent(this, Class.forName("com.example.anamaya.ContentHostActivity")).apply {
                        putExtra(ContentHostActivity.EXTRA_FRAGMENT_NAME, ContentHostActivity.FRAGMENT_MEDS_ORDER_NEW)
                        putExtra(ContentHostActivity.EXTRA_FRAGMENT_TITLE, "Meds Shop")
                        putExtra(ContentHostActivity.EXTRA_MENU_RES_ID, R.menu.shop_menu) // Pass the correct menu
                        putExtra(ContentHostActivity.EXTRA_SELECTED_NAV_ITEM_ID, R.id.nav_shop) // Pass the correct selected item
                    }
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        bottomNavigationView.menu.findItem(R.id.nav_home)?.isChecked = true
    }

    override fun onResume() {
        super.onResume()
        if (adImages.isNotEmpty()) {
            handler.postDelayed(imageChanger, DELAY_MILLISECONDS)
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(imageChanger)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(imageChanger)
    }
}
