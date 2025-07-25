package com.example.anamaya.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.anamaya.R

class FragmentNotifications : Fragment() {

    private lateinit var notificationsContainer: LinearLayout
    private lateinit var dismissAllButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_fragment_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notificationsContainer = view.findViewById(R.id.notifications_container)
        dismissAllButton = view.findViewById(R.id.dismiss_all_button)

        loadDummyNotifications()

        dismissAllButton.setOnClickListener {
            dismissAllNotifications()
        }
    }

    private fun loadDummyNotifications() {
        notificationsContainer.removeAllViews()

        val dummyNotifications = listOf(
            "Your appointment with Dr. Smith is tomorrow at 10:00 AM.",
            "New prescription for Ibuprofen has been added to your profile.",
            "Reminder: Take your medication (Vitamin D) at 8:00 PM.",
            "Your order #12345 has been shipped and will arrive soon.",
            "Important health update: Flu season precautions.",
            "You have a new message from your doctor.",
            "Your blood test results are available."
        )

        val textColor = ContextCompat.getColor(requireContext(), R.color.app_text_black)
        val backgroundColor = ContextCompat.getColor(requireContext(), R.color.app_content_white)
        val cornerRadius = resources.getDimensionPixelSize(R.dimen.card_corner_radius)

        dummyNotifications.forEach { notificationText ->
            val notificationCard = LayoutInflater.from(context).inflate(R.layout.notification_card_item, notificationsContainer, false)
            val notificationTextView = notificationCard.findViewById<TextView>(R.id.notification_card_text)
            notificationTextView.text = notificationText

            notificationCard.setOnClickListener {
                Toast.makeText(context, "Notification clicked: $notificationText", Toast.LENGTH_SHORT).show()
            }

            notificationsContainer.addView(notificationCard)

            val layoutParams = notificationCard.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.bottomMargin = resources.getDimensionPixelSize(R.dimen.notification_item_margin_bottom)
            notificationCard.layoutParams = layoutParams
        }

        if (dummyNotifications.isEmpty()) {
            val noNotificationsText = TextView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                text = "No new notifications."
                textSize = 18f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.app_secondary_text_dark_grey))
                typeface = resources.getFont(R.font.roboto_mono)
                gravity = View.TEXT_ALIGNMENT_CENTER
                setPadding(0, 32, 0, 0)
            }
            notificationsContainer.addView(noNotificationsText)
            dismissAllButton.visibility = View.GONE
        } else {
            dismissAllButton.visibility = View.VISIBLE
        }
    }

    private fun dismissAllNotifications() {
        notificationsContainer.removeAllViews()
        Toast.makeText(context, "All notifications dismissed!", Toast.LENGTH_SHORT).show()
        dismissAllButton.visibility = View.GONE

        val noNotificationsText = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            text = "No new notifications."
            textSize = 18f
            setTextColor(ContextCompat.getColor(requireContext(), R.color.app_secondary_text_dark_grey))
            typeface = resources.getFont(R.font.roboto_mono)
            gravity = View.TEXT_ALIGNMENT_CENTER
            setPadding(0, 32, 0, 0)
        }
        notificationsContainer.addView(noNotificationsText)
    }
}
