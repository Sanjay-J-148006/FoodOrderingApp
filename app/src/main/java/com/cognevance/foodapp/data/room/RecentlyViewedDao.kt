package com.cognevance.foodapp.data.room

import androidx.room.*
import com.cognevance.foodapp.data.model.RecentlyViewedItem
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentlyViewedDao {
    @Query("SELECT * FROM recently_viewed ORDER BY timestamp DESC LIMIT 10")
    fun getRecentlyViewed(): Flow<List<RecentlyViewedItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentlyViewed(item: RecentlyViewedItem)

    @Query("DELETE FROM recently_viewed")
    suspend fun clearHistory()
}
