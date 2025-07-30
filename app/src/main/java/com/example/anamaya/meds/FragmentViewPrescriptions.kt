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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FragmentViewPrescriptions : Fragment() {

    private lateinit var binding: MedsFragmentViewPrescriptionsBinding
    private val prescriptionList = mutableListOf<Prescription>()

    private val databaseRef by lazy {
        FirebaseDatabase.getInstance("https://anamaya-41e41e-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("users")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = MedsFragmentViewPrescriptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchUserPrescriptions()
    }

    private fun fetchUserPrescriptions() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        databaseRef.child(uid).child("user_prescription")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    prescriptionList.clear()
                    for (prescriptionSnap in snapshot.children) {
                        val presc = prescriptionSnap.getValue(Prescription::class.java)
                        if (presc != null) {
                            prescriptionList.add(presc)
                        }
                    }
                    renderPrescriptions()
                }

                override fun onCancelled(error: DatabaseError) {
                    showToast("Failed to load prescriptions: ${error.message}")
                }
            })
    }

    private fun renderPrescriptions() {
        binding.layoutPrescriptionList.removeAllViews()

        for (prescription in prescriptionList) {
            val itemBinding = ItemPrescriptionBinding.inflate(layoutInflater)

            itemBinding.tvPrescriptionDate.text = prescription.date
            itemBinding.tvDoctorName.text = prescription.doctorName

            itemBinding.root.setOnClickListener {
                val dialog = PrescriptionDialogFragment.newInstance(prescription) // View-only mode
                dialog.show(parentFragmentManager, "ViewPrescriptionDialog")
            }

            binding.layoutPrescriptionList.addView(itemBinding.root)
        }
    }

    private fun showToast(message: String) {
        if (isAdded) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }
}
