package com.example.anamaya.shop

data class OrderItem(
    val name: String,
    val quantity: Int,
    val price: Int,
    val imageUrl: String? = null,
    val productId: String = ""
)
