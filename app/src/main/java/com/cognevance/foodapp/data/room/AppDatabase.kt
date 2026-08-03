package com.cognevance.foodapp.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cognevance.foodapp.data.model.CartItem
import com.cognevance.foodapp.data.model.FavoriteItem
import com.cognevance.foodapp.data.model.RecentlyViewedItem

@Database(entities = [CartItem::class, FavoriteItem::class, RecentlyViewedItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun recentlyViewedDao(): RecentlyViewedDao
}
