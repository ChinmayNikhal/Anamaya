package com.example.anamaya.appointments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.anamaya.R

class AppointmentDialogFragment : DialogFragment() {

    private var appointment: Appointment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appointment = arguments?.getParcelable("appointment")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_appointment, null)

        appointment?.let {
            view.findViewById<TextView>(R.id.tvDialogTime).text = "${it.time}"
            view.findViewById<TextView>(R.id.tvDialogDate).text = "${it.date}"
            view.findViewById<TextView>(R.id.tvDialogDoctor).text = "Doctor: ${it.doctor}"
            view.findViewById<TextView>(R.id.tvDialogSpecialization).text = "Specialization: ${it.specialization}"
            view.findViewById<TextView>(R.id.tvDialogPurpose).text = "Purpose: ${it.purpose}"
            view.findViewById<TextView>(R.id.tvDialogNotes).text = "Notes: ${it.notes}"
        }

        view.findViewById<TextView>(R.id.btnDialogClose).setOnClickListener {
            dismiss()
        }

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
    }

    companion object {
        fun newInstance(appointment: Appointment): AppointmentDialogFragment {
            val fragment = AppointmentDialogFragment()
            val args = Bundle()
            args.putParcelable("appointment", appointment)
            fragment.arguments = args
            return fragment
        }
    }
}
