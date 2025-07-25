package com.example.anamaya.shop

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.anamaya.ContentHostActivity
import com.example.anamaya.R

class FragmentOrderResult : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.shop_fragment_order_result, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tvStatus = view.findViewById<TextView>(R.id.tvPaymentStatus)
        val tvOrderId = view.findViewById<TextView>(R.id.tvOrderId)
        val tvTransactionId = view.findViewById<TextView>(R.id.tvTransactionId)
        val tvDeliveryAddress = view.findViewById<TextView>(R.id.tvDeliveryAddress)
        val tvArrivalDate = view.findViewById<TextView>(R.id.tvArrivalDate)
        val btnBackToCart = view.findViewById<Button>(R.id.btnBackToCart)

        val intent = requireActivity().intent
        val success = intent.getBooleanExtra("success", false)
        val orderId = intent.getStringExtra("orderId") ?: ""
        val transactionId = intent.getStringExtra("transactionId") ?: ""
        val address = intent.getStringExtra("address") ?: ""
        val arrivalDate = intent.getStringExtra("arrivalDate") ?: ""

        tvStatus.text = if (success) "Order Placed Successfully" else "Payment Failed"
        tvOrderId.text = "Order ID: $orderId"
        tvTransactionId.text = "Transaction ID: $transactionId"

        if (success) {
            tvDeliveryAddress.visibility = View.VISIBLE
            tvArrivalDate.visibility = View.VISIBLE
            tvDeliveryAddress.text = "Delivered to: $address"
            tvArrivalDate.text = "Arriving on: $arrivalDate"
        } else {
            tvDeliveryAddress.visibility = View.GONE
            tvArrivalDate.visibility = View.GONE
        }

        btnBackToCart.setOnClickListener {
            val intent = Intent(requireContext(), ContentHostActivity::class.java).apply {
                putExtra(ContentHostActivity.EXTRA_FRAGMENT_NAME, ContentHostActivity.FRAGMENT_SHOP_SHOPPING_CART)
                putExtra(ContentHostActivity.EXTRA_FRAGMENT_TITLE, "Shopping Cart")
                putExtra(ContentHostActivity.EXTRA_MENU_RES_ID, R.menu.shop_menu)
                putExtra(ContentHostActivity.EXTRA_SELECTED_NAV_ITEM_ID, R.id.nav_cart)
            }
            startActivity(intent)
            requireActivity().finish()
        }
    }
}