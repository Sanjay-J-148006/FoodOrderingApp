package com.cognevance.foodapp.data.model

data class ProductResponse(
    val products: List<FoodItem>,
    val total: Int,
    val skip: Int,
    val limit: Int
)
