package com.example.anamaya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.example.anamaya.`class`.UserSession
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class GenericAlertDialogFragment : DialogFragment() {

    private var popupTitle: String? = null
    private var popupContent: String? = null
    private var mode: String = MODE_DEFAULT

    private lateinit var userSession: UserSession
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            popupTitle = it.getString(ARG_POPUP_TITLE)
            popupContent = it.getString(ARG_POPUP_CONTENT)
            mode = it.getString(ARG_MODE) ?: MODE_DEFAULT
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_generic_alert, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userSession = UserSession(requireContext())
        firebaseAuth = FirebaseAuth.getInstance()

        val titleTextView = view.findViewById<TextView>(R.id.popup_title)
        val contentTextView = view.findViewById<TextView>(R.id.popup_content)
        val closeButton = view.findViewById<Button>(R.id.popup_close_button)

        val addressFieldsContainer = view.findViewById<LinearLayout>(R.id.address_fields_container)
        val address1Input = view.findViewById<EditText>(R.id.address_input_1)
        val address2Input = view.findViewById<EditText>(R.id.address_input_2)
        val address3Input = view.findViewById<EditText>(R.id.address_input_3)

        val passwordFieldsContainer = view.findViewById<LinearLayout>(R.id.password_fields_container)
        val passwordOldInput = view.findViewById<EditText>(R.id.password_input_old)
        val passwordNewInput = view.findViewById<EditText>(R.id.password_input_new)
        val passwordConfirmInput = view.findViewById<EditText>(R.id.password_input_confirm)

        titleTextView.text = popupTitle
        contentTextView.text = popupContent

        when (mode) {
            MODE_ADDRESSES -> {
                addressFieldsContainer.visibility = View.VISIBLE
                passwordFieldsContainer.visibility = View.GONE
                contentTextView.visibility = View.GONE

                closeButton.text = "Save"

                val uid = firebaseAuth.currentUser?.uid
                if (uid == null) {
                    Toast.makeText(requireContext(), "User not logged in!", Toast.LENGTH_SHORT).show()
                    return
                }

                val dbRef = FirebaseDatabase.getInstance("https://anamaya-41e41e-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("users")
                    .child(uid)
                    .child("address")

                // Load saved address if any
                dbRef.get().addOnSuccessListener { snapshot ->
                    address1Input.setText(snapshot.child("address1").getValue(String::class.java) ?: "")
                    address2Input.setText(snapshot.child("address2").getValue(String::class.java) ?: "")
                    address3Input.setText(snapshot.child("address3").getValue(String::class.java) ?: "")
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to load saved address", Toast.LENGTH_SHORT).show()
                }

                closeButton.setOnClickListener {
                    val addr1 = address1Input.text.toString()
                    val addr2 = address2Input.text.toString()
                    val addr3 = address3Input.text.toString()

                    val addressMap = mapOf(
                        "address1" to addr1,
                        "address2" to addr2,
                        "address3" to addr3
                    )

                    dbRef.setValue(addressMap).addOnSuccessListener {
                        Toast.makeText(requireContext(), "Address saved successfully", Toast.LENGTH_SHORT).show()
                        dismiss()
                    }.addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to save address", Toast.LENGTH_SHORT).show()
                    }
                }
            }


            MODE_CHANGE_PASSWORD -> {
                addressFieldsContainer.visibility = View.GONE
                passwordFieldsContainer.visibility = View.VISIBLE
                contentTextView.visibility = View.GONE
                closeButton.text = "Save"

                closeButton.setOnClickListener {
                    val oldPass = passwordOldInput.text.toString()
                    val newPass = passwordNewInput.text.toString()
                    val confirmPass = passwordConfirmInput.text.toString()

                    when {
                        oldPass.isBlank() || newPass.isBlank() || confirmPass.isBlank() -> {
                            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                        }

                        newPass != confirmPass -> {
                            Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
                        }

                        else -> {
                            Toast.makeText(requireContext(), "Password updated successfully!", Toast.LENGTH_SHORT).show()
                            dismiss()
                        }
                    }
                }
            }

            else -> {
                addressFieldsContainer.visibility = View.GONE
                passwordFieldsContainer.visibility = View.GONE
                contentTextView.visibility = View.VISIBLE

                closeButton.setOnClickListener { dismiss() }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    companion object {
        private const val ARG_POPUP_TITLE = "popup_title"
        private const val ARG_POPUP_CONTENT = "popup_content"
        private const val ARG_MODE = "popup_mode"

        const val MODE_DEFAULT = "default"
        const val MODE_ADDRESSES = "addresses"
        const val MODE_CHANGE_PASSWORD = "change_password"

        @JvmStatic
        fun newInstance(title: String, content: String, mode: String = MODE_DEFAULT) =
            GenericAlertDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_POPUP_TITLE, title)
                    putString(ARG_POPUP_CONTENT, content)
                    putString(ARG_MODE, mode)
                }
            }
    }
}
