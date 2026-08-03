package com.cognevance.foodapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteItem(
    @PrimaryKey val id: Int,
    val title: String,
    val price: Double,
    val rating: Double,
    val category: String,
    val thumbnail: String,
    val description: String = ""
)
