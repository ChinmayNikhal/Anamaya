package com.example.anamaya.shop

import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.anamaya.R

class FragmentMyOrders : Fragment() {

    private lateinit var ordersContainer: LinearLayout

    private val dummyOrders = listOf(
        Order(
            "ORD001", "TXN001", "2025-07-10", "11:30 AM", 350,
            "2025-07-20",
            listOf(OrderItem("Paracetamol", 2, 100), OrderItem("Cough Syrup", 1, 150)),
            orderedBy = "Rahul", shippingAddress = "123 St, Mumbai"
        ),
        Order(
            "ORD002", "TXN002", "2025-07-12", "9:15 AM", 500,
            "2025-07-22",
            listOf(OrderItem("Vitamin D", 1, 200), OrderItem("Insulin", 2, 150)),
            orderedBy = "Rahul", shippingAddress = "456 Lane, Delhi"
        )
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.shop_fragment_my_orders, container, false)
        ordersContainer = view.findViewById(R.id.myOrdersContainer)

        dummyOrders.forEach { order ->
            val itemView = inflater.inflate(R.layout.item_order, ordersContainer, false)

            // Set all common views
            itemView.findViewById<TextView>(R.id.tvOrderId).text = "Order ID: ${order.orderId}"
            itemView.findViewById<TextView>(R.id.tvOrderDate).text = "Arriving: ${order.deliveryDate}"
            itemView.findViewById<TextView>(R.id.tvOrderAmount).text = "₹${order.amount}"

            // Show MyOrders specific layout
            itemView.findViewById<TextView>(R.id.tvOrderId).visibility = View.GONE
            itemView.findViewById<TextView>(R.id.tvOrderName).apply {
                text = "Name: ${order.orderedBy}"
                visibility = View.VISIBLE
            }
            itemView.findViewById<TextView>(R.id.tvOrderArrivingDay).apply {
                text = "Arrives: ${order.deliveryDate}"
                visibility = View.VISIBLE
            }

            itemView.setOnClickListener {
                OrderDetailsDialogFragment(order, isMyOrders = true).show(parentFragmentManager, "orderDetail")
            }

            ordersContainer.addView(itemView)
        }

        return view
    }
}
