package com.example.anamaya.profile // IMPORTANT: Ensure this matches your actual package name

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.anamaya.GenericAlertDialogFragment
import com.google.android.material.card.MaterialCardView
import com.example.anamaya.R // Ensure R is imported

class FragmentSupport : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_fragment_support, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialCardView>(R.id.option_support_chat).setOnClickListener {
            showGenericPopup(
                getString(R.string.popup_chat_title),
                getString(R.string.popup_chat_content)
            )
        }

        view.findViewById<MaterialCardView>(R.id.option_support_call).setOnClickListener {
            showGenericPopup(
                getString(R.string.popup_contact_number_title),
                getString(R.string.popup_contact_number_content)
            )
        }

        view.findViewById<MaterialCardView>(R.id.option_support_email).setOnClickListener {
            showGenericPopup(
                getString(R.string.popup_email_title),
                getString(R.string.popup_email_content)
            )
        }
    }

    // Renamed for generic use
    private fun showGenericPopup(title: String, content: String) {
        val dialogFragment = GenericAlertDialogFragment.newInstance(title, content)
        dialogFragment.show(parentFragmentManager, "GenericAlertDialog") // Changed tag
    }
}
