package com.example.anamaya.shop

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.anamaya.ContentHostActivity
import com.example.anamaya.R
import java.text.SimpleDateFormat
import java.util.*

class FragmentShoppingCart : Fragment() {

    private lateinit var cartItemsContainer: LinearLayout
    private lateinit var tvItemCount: TextView
    private lateinit var tvTotalCost: TextView
    private lateinit var btnSelectAddress: Button
    private lateinit var btnPay: Button
    private lateinit var layoutPaymentSummary: LinearLayout
    private lateinit var tvOrderId: TextView
    private lateinit var tvTransactionId: TextView
    private lateinit var tvDeliveryAddress: TextView
    private lateinit var tvArrivalDate: TextView
    private lateinit var tvPaymentStatus: TextView

    private var selectedAddress: String = "-"
    private var totalCost = 0.0

    // Dummy cart
    private val cartItems = listOf(
        OrderItem("Paracetamol", 2, 20, "", "p1"),
        OrderItem("Cough Syrup", 1, 80, "", "p2"),
        OrderItem("Vitamin C", 3, 10, "", "p3")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.shop_fragment_shopping_cart, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        cartItemsContainer = view.findViewById(R.id.cartItemsContainer)
        tvItemCount = view.findViewById(R.id.tvItemCount)
        tvTotalCost = view.findViewById(R.id.tvTotalCost)
        btnSelectAddress = view.findViewById(R.id.btnSelectAddress)
        btnPay = view.findViewById(R.id.btnPay)
        layoutPaymentSummary = view.findViewById(R.id.layoutPaymentSummary)
        tvOrderId = view.findViewById(R.id.tvOrderId)
        tvTransactionId = view.findViewById(R.id.tvTransactionId)
        tvDeliveryAddress = view.findViewById(R.id.tvDeliveryAddress)
        tvArrivalDate = view.findViewById(R.id.tvArrivalDate)
        tvPaymentStatus = view.findViewById(R.id.tvPaymentStatus)

        populateCart()

        btnSelectAddress.setOnClickListener {
            showAddressDialog()
        }

        btnPay.setOnClickListener {
            navigateToPaymentGateway()
        }

        // Check for payment result
        val result = arguments?.getString("payment_result")
        val txnId = arguments?.getString("transaction_id") ?: "N/A"
        if (result != null) {
            onPaymentResult(result == "success", txnId)
        }
    }

    private fun populateCart() {
        cartItemsContainer.removeAllViews()
        totalCost = 0.0

        for (item in cartItems) {
            val itemView = layoutInflater.inflate(R.layout.item_cart_med, cartItemsContainer, false)
            itemView.findViewById<TextView>(R.id.tvMedName).text = item.name
            itemView.findViewById<TextView>(R.id.tvMedQty).text = "x${item.quantity}"
            itemView.findViewById<TextView>(R.id.tvMedPrice).text = "₹${item.price * item.quantity}"
            totalCost += item.price * item.quantity
            cartItemsContainer.addView(itemView)
        }

        val gst = totalCost * 0.18
        val finalCost = totalCost + gst
        tvItemCount.text = "Items in Cart: ${cartItems.sumOf { it.quantity }}"
        tvTotalCost.text = "Total Cost (incl. GST): ₹${"%.2f".format(finalCost)}"
        btnPay.text = "Pay ₹${"%.2f".format(finalCost)}"
    }

    private fun showAddressDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_select_address, null)
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radioGroupAddresses)
        val btnSelect = dialogView.findViewById<Button>(R.id.btnSelect)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        btnSelect.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            if (selectedId != -1) {
                val selectedRadio = dialogView.findViewById<RadioButton>(selectedId)
                selectedAddress = selectedRadio.text.toString()
                Toast.makeText(requireContext(), "Delivering to: $selectedAddress", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Please select an address", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun navigateToPaymentGateway() {
        val context = requireContext()
        val intent = Intent(context, ContentHostActivity::class.java).apply {
            putExtra(ContentHostActivity.EXTRA_FRAGMENT_NAME, ContentHostActivity.FRAGMENT_PAYMENT_GATEWAY)
            putExtra(ContentHostActivity.EXTRA_FRAGMENT_TITLE, "Payment Gateway")
            putExtra(ContentHostActivity.EXTRA_MENU_RES_ID, R.menu.shop_menu)
            putExtra(ContentHostActivity.EXTRA_SELECTED_NAV_ITEM_ID, R.id.nav_cart)
            putExtra("payableAmount", "%.2f".format(totalCost * 1.18))
        }
        startActivity(intent)
    }

    private fun onPaymentResult(success: Boolean, transactionId: String) {
        layoutPaymentSummary.isVisible = true
        tvPaymentStatus.text = if (success) "Order Placed Successfully" else "Payment Failed"
        tvTransactionId.text = "Transaction ID: $transactionId"
        tvOrderId.text = "Order ID: ORD${System.currentTimeMillis() % 100000}"
        tvDeliveryAddress.text = "Delivered to: $selectedAddress"

        val arrivalDate = Calendar.getInstance().apply {
            add(Calendar.DATE, 4)
        }
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        tvArrivalDate.text = "Arriving on: ${sdf.format(arrivalDate.time)}"
    }
}
