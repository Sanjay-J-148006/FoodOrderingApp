package com.cognevance.foodapp.data.model

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: Double,
    @SerializedName("discountPercentage") val discountPercentage: Double = 0.0,
    @SerializedName("rating") val rating: Double = 0.0,
    @SerializedName("stock") val stock: Int = 0,
    @SerializedName("brand") val brand: String? = null,
    @SerializedName("category") val category: String = "",
    @SerializedName("thumbnail") val thumbnail: String = "",
    @SerializedName("images") val images: List<String> = emptyList()
)
