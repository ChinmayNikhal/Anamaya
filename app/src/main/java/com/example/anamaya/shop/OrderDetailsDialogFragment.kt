package com.example.anamaya.shop

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.anamaya.R
import com.example.anamaya.shop.Order
import com.example.anamaya.shop.OrderItem

class OrderDetailsDialogFragment(
    private val order: Order,
    private val isMyOrders: Boolean = false
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_order_details, null)

        view.findViewById<TextView>(R.id.tvDialogOrderId).text = "Order ID: ${order.orderId}"
        view.findViewById<TextView>(R.id.tvTransactionId).text = "Transaction ID: ${order.transactionId}"
        view.findViewById<TextView>(R.id.tvOrderTime).text = "Time: ${order.time}"
        view.findViewById<TextView>(R.id.tvOrderDateDetail).text = "Date: ${order.date}"
        view.findViewById<TextView>(R.id.tvTotalAmount).text = "Total: ₹${order.amount}"
        view.findViewById<TextView>(R.id.tvDeliveryDate).text = "Delivered on: ${order.deliveryDate}"

        // Extra views for My Orders
        val addressView = view.findViewById<TextView>(R.id.tvShippingAddress)
        val downloadBtn = view.findViewById<Button>(R.id.btnDownloadInvoice)
        if (isMyOrders) {
            addressView.text = "Address: ${order.shippingAddress}"
            addressView.visibility = View.VISIBLE
            downloadBtn.visibility = View.VISIBLE
        }

        val container = view.findViewById<LinearLayout>(R.id.medsListContainer)
        order.items.forEach {
            val item = TextView(requireContext()).apply {
                text = "- ${it.name} x${it.quantity} ₹${it.price}"
                setPadding(0, 4, 0, 4)
                typeface = resources.getFont(R.font.roboto_mono)
            }
            container.addView(item)
        }

        return Dialog(requireContext()).apply {
            setContentView(view)
            window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }
}
