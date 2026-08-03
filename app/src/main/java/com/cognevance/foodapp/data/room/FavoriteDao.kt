package com.cognevance.foodapp.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cognevance.foodapp.data.model.FavoriteItem
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): Flow<List<FavoriteItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favoriteItem: FavoriteItem)

    @Query("DELETE FROM favorites WHERE id = :itemId")
    suspend fun removeFavorite(itemId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :itemId LIMIT 1)")
    fun isFavorite(itemId: Int): Flow<Boolean>
}
