package com.example.anamaya.shop

import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.anamaya.R
import com.example.anamaya.shop.Order
import com.example.anamaya.shop.OrderItem

class FragmentOrdersHistory : Fragment() {

    private lateinit var ordersContainer: LinearLayout
    private val dummyOrders = listOf(
        Order(
            orderId = "ORD123456",
            transactionId = "TXN987654",
            date = "2025-07-17",
            time = "12:30 PM",
            amount = 820,
            deliveryDate = "2025-07-18",
            items = listOf(
                OrderItem("Paracetamol", 2, 50),
                OrderItem("Vitamin C", 1, 80),
                OrderItem("Amoxicillin", 1, 150)
            )
        ),
        Order(
            orderId = "ORD789012",
            transactionId = "TXN765432",
            date = "2025-06-30",
            time = "10:45 AM",
            amount = 620,
            deliveryDate = "2025-07-01",
            items = listOf(
                OrderItem("Cough Syrup", 1, 120),
                OrderItem("Ibuprofen", 1, 100),
                OrderItem("Antacid", 2, 200)
            )
        )
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.shop_fragment_orders_history, container, false)
        ordersContainer = view.findViewById(R.id.ordersContainer)
        displayOrders()
        return view
    }

    private fun displayOrders() {
        ordersContainer.removeAllViews()
        for (order in dummyOrders) {
            val itemView = layoutInflater.inflate(R.layout.item_order, ordersContainer, false)
            itemView.findViewById<TextView>(R.id.tvOrderId).text = "Order ID: ${order.orderId}"
            itemView.findViewById<TextView>(R.id.tvOrderDate).text = "Date: ${order.date}"
            itemView.findViewById<TextView>(R.id.tvOrderAmount).text = "Amount: ₹${order.amount}"

            itemView.setOnClickListener {
                val dialog = OrderDetailsDialogFragment(order)
                dialog.show(parentFragmentManager, "OrderDetailsDialog")
            }
            ordersContainer.addView(itemView)
        }
    }
}
