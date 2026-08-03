package com.cognevance.foodapp.data.repository

import com.cognevance.foodapp.data.model.CartItem
import com.cognevance.foodapp.data.model.FoodItem
import com.cognevance.foodapp.data.room.CartDao
import kotlinx.coroutines.flow.Flow

class CartRepository(private val cartDao: CartDao) {
    val cartItems: Flow<List<CartItem>> = cartDao.getAllCartItems()

    suspend fun addToCart(foodItem: FoodItem, quantity: Int = 1) {
        val existing = cartDao.getCartItemById(foodItem.id)
        if (existing != null) {
            existing.quantity += quantity
            cartDao.updateItem(existing)
        } else {
            val item = CartItem(
                id = foodItem.id,
                title = foodItem.title,
                price = foodItem.price,
                quantity = quantity,
                thumbnail = foodItem.thumbnail,
                category = foodItem.category
            )
            cartDao.insertItem(item)
        }
    }

    suspend fun incrementQuantity(id: Int) {
        val existing = cartDao.getCartItemById(id)
        if (existing != null) {
            existing.quantity += 1
            cartDao.updateItem(existing)
        }
    }

    suspend fun decrementQuantity(id: Int) {
        val existing = cartDao.getCartItemById(id)
        if (existing != null) {
            if (existing.quantity > 1) {
                existing.quantity -= 1
                cartDao.updateItem(existing)
            } else {
                cartDao.deleteItemById(existing.id)
            }
        }
    }

    suspend fun removeFromCart(cartItem: CartItem) {
        cartDao.deleteItemById(cartItem.id)
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }
}
