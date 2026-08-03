package com.cognevance.foodapp.data.repository

import com.cognevance.foodapp.data.api.FoodApi
import com.cognevance.foodapp.data.model.CartItem
import com.cognevance.foodapp.data.model.FavoriteItem
import com.cognevance.foodapp.data.model.FoodItem
import com.cognevance.foodapp.data.model.RecentlyViewedItem
import com.cognevance.foodapp.data.room.CartDao
import com.cognevance.foodapp.data.room.FavoriteDao
import com.cognevance.foodapp.data.room.RecentlyViewedDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoodRepository @Inject constructor(
    private val api: FoodApi,
    private val cartDao: CartDao,
    private val favoriteDao: FavoriteDao,
    private val recentlyViewedDao: RecentlyViewedDao
) {
    suspend fun getGroceries(): Result<List<FoodItem>> {
        return try {
            val response = api.getGroceries()
            if (response.isSuccessful) {
                Result.success(response.body()?.products ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch groceries"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductById(id: Int): Result<FoodItem> {
        return try {
            val response = api.getProductById(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Product not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchProducts(query: String): Result<List<FoodItem>> {
        return try {
            val response = api.searchProducts(query)
            if (response.isSuccessful) {
                Result.success(response.body()?.products ?: emptyList())
            } else {
                Result.failure(Exception("Failed to search products"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Cart Operations
    fun getCartItems(): Flow<List<CartItem>> = cartDao.getAllCartItems()

    suspend fun addToCart(cartItem: CartItem) {
        cartDao.insertItem(cartItem)
    }

    suspend fun updateCartItem(cartItem: CartItem) {
        cartDao.updateItem(cartItem)
    }

    suspend fun removeCartItem(itemId: Int) {
        cartDao.deleteItemById(itemId)
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }

    // Favorite Operations
    fun getFavorites(): Flow<List<FavoriteItem>> = favoriteDao.getAllFavorites()

    suspend fun addFavorite(favoriteItem: FavoriteItem) {
        favoriteDao.insertFavorite(favoriteItem)
    }

    suspend fun removeFavorite(itemId: Int) {
        favoriteDao.removeFavorite(itemId)
    }

    fun isFavorite(itemId: Int): Flow<Boolean> = favoriteDao.isFavorite(itemId)

    // Recently Viewed Operations
    fun getRecentlyViewed(): Flow<List<RecentlyViewedItem>> = recentlyViewedDao.getRecentlyViewed()

    suspend fun addRecentlyViewed(foodItem: FoodItem) {
        recentlyViewedDao.insertRecentlyViewed(
            RecentlyViewedItem(
                id = foodItem.id,
                title = foodItem.title,
                price = foodItem.price,
                rating = foodItem.rating,
                category = foodItem.category,
                thumbnail = foodItem.thumbnail,
                timestamp = System.currentTimeMillis()
            )
        )
    }
}
