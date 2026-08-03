package com.cognevance.foodapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cognevance.foodapp.data.model.Order
import com.cognevance.foodapp.firebase.FirebaseAuthManager
import com.cognevance.foodapp.firebase.FirestoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val firestoreManager: FirestoreManager,
    private val authManager: FirebaseAuthManager
) : ViewModel() {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            val user = authManager.getCurrentUser()
            if (user != null) {
                val result = firestoreManager.getOrdersForUser(user.uid)
                if (result.isSuccess) {
                    _orders.value = result.getOrNull() ?: emptyList()
                } else {
                    _error.value = result.exceptionOrNull()?.message
                }
            } else {
                _error.value = "User not logged in"
            }
            _isLoading.value = false
        }
    }
}
