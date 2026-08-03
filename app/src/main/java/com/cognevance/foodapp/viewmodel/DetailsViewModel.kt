package com.cognevance.foodapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cognevance.foodapp.data.model.CartItem
import com.cognevance.foodapp.data.model.FavoriteItem
import com.cognevance.foodapp.data.model.FoodItem
import com.cognevance.foodapp.data.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: FoodRepository
) : ViewModel() {

    private val _product = MutableStateFlow<FoodItem?>(null)
    val product: StateFlow<FoodItem?> = _product

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    fun fetchProductDetails(productId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getProductById(productId)
            if (result.isSuccess) {
                _product.value = result.getOrNull()
            }
            _isLoading.value = false
            
            checkIfFavorite(productId)
        }
    }

    private fun checkIfFavorite(productId: Int) {
        viewModelScope.launch {
            repository.isFavorite(productId).collect {
                _isFavorite.value = it
            }
        }
    }

    fun toggleFavorite() {
        val currentProduct = _product.value ?: return
        viewModelScope.launch {
            if (_isFavorite.value) {
                repository.removeFavorite(currentProduct.id)
            } else {
                repository.addFavorite(
                    FavoriteItem(
                        id = currentProduct.id,
                        title = currentProduct.title,
                        price = currentProduct.price,
                        rating = currentProduct.rating,
                        thumbnail = currentProduct.thumbnail,
                        description = currentProduct.description,
                        category = currentProduct.category
                    )
                )
            }
        }
    }

    fun addToCart(quantity: Int) {
        val currentProduct = _product.value ?: return
        viewModelScope.launch {
            val cartItem = CartItem(
                id = currentProduct.id,
                title = currentProduct.title,
                price = currentProduct.price,
                thumbnail = currentProduct.thumbnail,
                quantity = quantity,
                category = currentProduct.category
            )
            repository.addToCart(cartItem)
        }
    }
}
