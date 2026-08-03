package com.cognevance.foodapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cognevance.foodapp.data.model.CartItem
import com.cognevance.foodapp.data.model.FoodItem
import com.cognevance.foodapp.data.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: FoodRepository
) : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    private val _subtotal = MutableStateFlow(0.0)
    val subtotal: StateFlow<Double> = _subtotal

    val gstRate = 0.05
    val deliveryFee = 2.0

    private val _total = MutableStateFlow(0.0)
    val total: StateFlow<Double> = _total

    init {
        viewModelScope.launch {
            repository.getCartItems().collectLatest { items ->
                _cartItems.value = items
                calculateTotals(items)
            }
        }
    }

    private fun calculateTotals(items: List<CartItem>) {
        val st = items.sumOf { it.price * it.quantity }
        _subtotal.value = st
        _total.value = st + (st * gstRate) + (if (st > 0) deliveryFee else 0.0)
    }

    fun addToCart(foodItem: FoodItem, quantity: Int = 1) {
        viewModelScope.launch {
            val existing = _cartItems.value.find { it.id == foodItem.id }
            if (existing != null) {
                repository.updateCartItem(existing.copy(quantity = existing.quantity + quantity))
            } else {
                repository.addToCart(
                    CartItem(
                        id = foodItem.id,
                        title = foodItem.title,
                        price = foodItem.price,
                        quantity = quantity,
                        thumbnail = foodItem.thumbnail,
                        category = foodItem.category
                    )
                )
            }
        }
    }

    fun increaseQuantity(item: CartItem) {
        viewModelScope.launch {
            repository.updateCartItem(item.copy(quantity = item.quantity + 1))
        }
    }

    fun decreaseQuantity(item: CartItem) {
        if (item.quantity > 1) {
            viewModelScope.launch {
                repository.updateCartItem(item.copy(quantity = item.quantity - 1))
            }
        } else {
            removeItem(item.id)
        }
    }

    fun removeItem(itemId: Int) {
        viewModelScope.launch {
            repository.removeCartItem(itemId)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }
}
