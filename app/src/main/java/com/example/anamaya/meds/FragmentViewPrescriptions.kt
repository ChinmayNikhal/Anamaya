package com.example.anamaya.meds

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.anamaya.`class`.Prescription
import com.example.anamaya.databinding.MedsFragmentViewPrescriptionsBinding
import com.example.anamaya.databinding.ItemPrescriptionBinding

class FragmentViewPrescriptions : Fragment() {

    private lateinit var binding: MedsFragmentViewPrescriptionsBinding
    private val prescriptionList = mutableListOf<Prescription>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MedsFragmentViewPrescriptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadDummyPrescriptions()
        renderPrescriptions()

        binding.btnAddNewPrescription.setOnClickListener {
            val newPrescription = Prescription(
                date = "",
                doctorName = "",
                medications = listOf(),
                imageUri = null
            )
            val dialog = PrescriptionDialogFragment.newInstance(newPrescription, isViewMode = false)
            dialog.setTargetFragment(this, 0)
            dialog.show(parentFragmentManager, "AddPrescriptionDialog")
        }
    }

    fun addNewPrescription(prescription: Prescription) {
        prescriptionList.add(prescription)
        renderPrescriptions()
        showToast("Added prescription: ${prescription.doctorName}")
    }

    private fun renderPrescriptions() {
        binding.layoutPrescriptionList.removeAllViews()

        for (prescription in prescriptionList) {
            val itemBinding = ItemPrescriptionBinding.inflate(layoutInflater)

            itemBinding.tvPrescriptionDate.text = prescription.date
            itemBinding.tvDoctorName.text = prescription.doctorName

            itemBinding.root.setOnClickListener {
                val dialog = PrescriptionDialogFragment.newInstance(prescription, isViewMode = true)
                dialog.show(parentFragmentManager, "ViewPrescriptionDialog")
            }

            binding.layoutPrescriptionList.addView(itemBinding.root)
        }
    }

    private fun loadDummyPrescriptions() {
        prescriptionList.add(
            Prescription(
                date = "18 Jul 2025",
                doctorName = "Dr. Anaya Roy",
                medications = listOf("Paracetamol", "Amoxicillin"),
                imageUri = null
            )
        )
        prescriptionList.add(
            Prescription(
                date = "10 Jun 2025",
                doctorName = "Dr. Kamath",
                medications = listOf("Ibuprofen"),
                imageUri = null
            )
        )
    }

    private fun showToast(message: String) {
        if (isAdded) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }
}
