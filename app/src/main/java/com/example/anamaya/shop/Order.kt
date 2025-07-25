package com.example.anamaya.shop

data class Order(
    val orderId: String,
    val transactionId: String,
    val date: String,
    val time: String,
    val amount: Int,
    val deliveryDate: String,
    val items: List<OrderItem>,
    val orderedBy: String = "",
    val shippingAddress: String = "",
)
