package com.cognevance.foodapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cognevance.foodapp.data.model.FoodItem
import com.cognevance.foodapp.data.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: FoodRepository
) : ViewModel() {

    private val _products = MutableStateFlow<List<FoodItem>>(emptyList())
    val products: StateFlow<List<FoodItem>> = _products

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        fetchGroceries()
    }

    fun fetchGroceries() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repository.getGroceries()
            if (result.isSuccess) {
                _products.value = result.getOrNull() ?: emptyList()
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Unknown error"
            }
            _isLoading.value = false
        }
    }
    
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        if (query.isEmpty()) {
            fetchGroceries()
        } else {
            searchProducts(query)
        }
    }

    private fun searchProducts(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.searchProducts(query)
            if (result.isSuccess) {
                _products.value = result.getOrNull() ?: emptyList()
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }
}
