package com.cognevance.foodapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey val id: Int,
    val title: String,
    val price: Double,
    var quantity: Int,
    val thumbnail: String,
    val category: String = ""
)
