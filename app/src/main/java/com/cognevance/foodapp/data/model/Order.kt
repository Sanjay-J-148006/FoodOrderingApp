package com.cognevance.foodapp.data.model

data class OrderItem(
    val id: Int = 0,
    val title: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0,
    val thumbnail: String = ""
)

data class Order(
    val orderId: String = "",
    val uid: String = "",
    val items: List<OrderItem> = emptyList(),
    val total: Double = 0.0,
    val paymentMethod: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "Pending", // Pending, Delivered, Cancelled
    val fullName: String = "",
    val phone: String = "",
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val pincode: String = ""
)
