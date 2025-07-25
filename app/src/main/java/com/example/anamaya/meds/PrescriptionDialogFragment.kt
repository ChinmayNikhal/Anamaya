package com.example.anamaya.meds

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.anamaya.`class`.Prescription
import com.example.anamaya.databinding.DialogPrescriptionBinding
import java.util.Calendar

class PrescriptionDialogFragment : DialogFragment() {

    private var _binding: DialogPrescriptionBinding? = null
    private val binding get() = _binding!!

    private var prescription: Prescription? = null
    private var isViewMode: Boolean = true
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            prescription = it.getParcelable("prescription")
            isViewMode = it.getBoolean("isViewMode", true)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogPrescriptionBinding.inflate(LayoutInflater.from(requireContext()))
        setupUI()
        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
    }

    private fun setupUI() {
        val isAddMode = !isViewMode

        // Disable manual input for date
        binding.etDate.isFocusable = false
        binding.etDate.isClickable = true

        // Enable or disable text fields
        binding.tilDate.isEnabled = isAddMode
        binding.tilDoctor.isEnabled = isAddMode

        // Set up date picker
        if (isAddMode) {
            binding.etDate.setOnClickListener {
                val calendar = Calendar.getInstance()
                val datePicker = DatePickerDialog(
                    requireContext(),
                    { _, year, month, dayOfMonth ->
                        val formattedDate = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
                        binding.etDate.setText(formattedDate)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                datePicker.show()
            }
        }

        // Toggle visibility of buttons/sections
        binding.btnAddMed.visibility = if (isAddMode) View.VISIBLE else View.GONE
        binding.btnUploadImage.visibility = if (isAddMode) View.VISIBLE else View.GONE
        binding.addButtons.visibility = if (isAddMode) View.VISIBLE else View.GONE
        binding.viewButtons.visibility = if (isViewMode) View.VISIBLE else View.GONE

        // Populate fields if prescription exists
        prescription?.let {
            binding.etDate.setText(it.date)
            binding.etDoctor.setText(it.doctorName)

            binding.medsListContainer.removeAllViews()
            for (med in it.medications) {
                val medView = LayoutInflater.from(context).inflate(
                    android.R.layout.simple_list_item_1,
                    binding.medsListContainer,
                    false
                ) as TextView
                medView.text = med
                binding.medsListContainer.addView(medView)
            }

            val imageUri: Uri? = it.imageUri
            if (imageUri != null) {
                binding.ivPrescriptionImage.visibility = View.VISIBLE
                binding.ivPrescriptionImage.setImageURI(imageUri)
            } else {
                binding.ivPrescriptionImage.visibility = View.GONE
            }
        }

        binding.btnClose.setOnClickListener { dismiss() }
        binding.btnCancel.setOnClickListener { dismiss() }

        binding.btnSave.setOnClickListener {
            val newPrescription = Prescription(
                date = binding.etDate.text.toString(),
                doctorName = binding.etDoctor.text.toString(),
                medications = listOf("DummyMed1", "DummyMed2"),
                imageUri = selectedImageUri
            )
            (parentFragment as? FragmentViewPrescriptions)?.addNewPrescription(newPrescription)
            dismiss()
        }

        binding.btnAddMed.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "You can add medications after saving this prescription.",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.btnUploadImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 101)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                selectedImageUri = uri
                binding.ivPrescriptionImage.setImageURI(uri)
                binding.ivPrescriptionImage.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(prescription: Prescription, isViewMode: Boolean): PrescriptionDialogFragment {
            val fragment = PrescriptionDialogFragment()
            val args = Bundle().apply {
                putParcelable("prescription", prescription)
                putBoolean("isViewMode", isViewMode)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
