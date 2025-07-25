package com.example.anamaya.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.anamaya.R
import com.example.anamaya.ContentHostActivity
import com.example.anamaya.GenericAlertDialogFragment

class FragmentSettings : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.profile_fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val profileInfoOption = view.findViewById<LinearLayout>(R.id.option_profile_info)
        val themeSwitch = view.findViewById<Switch>(R.id.theme_switch)
        val addressesOption = view.findViewById<LinearLayout>(R.id.option_addresses)
        val changePasswordOption = view.findViewById<LinearLayout>(R.id.option_change_password)

        // 🔁 REDIRECT TO "My Info" via ContentHostActivity
        profileInfoOption.setOnClickListener {
            val intent = Intent(requireContext(), ContentHostActivity::class.java).apply {
                putExtra(ContentHostActivity.EXTRA_FRAGMENT_NAME, ContentHostActivity.FRAGMENT_MY_INFO)
                putExtra(ContentHostActivity.EXTRA_FRAGMENT_TITLE, "My Info")
                putExtra(ContentHostActivity.EXTRA_MENU_RES_ID, R.menu.profile_menu)
                putExtra(ContentHostActivity.EXTRA_SELECTED_NAV_ITEM_ID, R.id.nav_my_info)
            }
            startActivity(intent)
        }

        // Theme toggle toast (placeholder)
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(
                requireContext(),
                if (isChecked) "Dark mode enabled" else "Light mode enabled",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Addresses dialog
        addressesOption.setOnClickListener {
            val dialog = GenericAlertDialogFragment.newInstance(
                title = "Edit Addresses",
                content = "Please update your saved addresses below:",
                mode = GenericAlertDialogFragment.MODE_ADDRESSES
            )
            dialog.show(parentFragmentManager, "AddressDialog")
        }

        // Change Password dialog
        changePasswordOption.setOnClickListener {
            val dialog = GenericAlertDialogFragment.newInstance(
                title = "Change Password",
                content = "Please enter your old and new passwords:",
                mode = GenericAlertDialogFragment.MODE_CHANGE_PASSWORD
            )
            dialog.show(parentFragmentManager, "ChangePasswordDialog")
        }

    }
}
