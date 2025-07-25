package com.example.anamaya.shop

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.anamaya.ContentHostActivity
import com.example.anamaya.R
import java.util.UUID

class FragmentPaymentGateway : Fragment() {

    private val dummyAddress = "123 Main Street, New Delhi"
    private val dummyArrivalDate = "25 July 2025"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.shop_fragment_payment_gateway, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btn_payment_success).setOnClickListener {
            navigateToOrderResult(success = true)
        }

        view.findViewById<Button>(R.id.btn_payment_failure).setOnClickListener {
            navigateToOrderResult(success = false)
        }
    }

    private fun navigateToOrderResult(success: Boolean) {
        val orderId = UUID.randomUUID().toString().take(8).uppercase()
        val transactionId = UUID.randomUUID().toString().take(12).uppercase()

        val intent = Intent(requireContext(), ContentHostActivity::class.java).apply {
            putExtra(ContentHostActivity.EXTRA_FRAGMENT_NAME, "FragmentOrderResult")
            putExtra(ContentHostActivity.EXTRA_FRAGMENT_TITLE, "Order Status")
            putExtra(ContentHostActivity.EXTRA_MENU_RES_ID, R.menu.shop_menu)
            putExtra(ContentHostActivity.EXTRA_SELECTED_NAV_ITEM_ID, R.id.nav_cart)

            putExtra("success", success)
            putExtra("orderId", orderId)
            putExtra("transactionId", transactionId)
            putExtra("address", dummyAddress)
            putExtra("arrivalDate", dummyArrivalDate)
        }
        startActivity(intent)
        requireActivity().finish()
    }
}
