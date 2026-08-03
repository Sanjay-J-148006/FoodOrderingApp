package com.cognevance.foodapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cognevance.foodapp.data.model.Order
import com.cognevance.foodapp.data.model.OrderItem
import com.cognevance.foodapp.data.repository.FoodRepository
import com.cognevance.foodapp.firebase.FirebaseAuthManager
import com.cognevance.foodapp.firebase.FirestoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val firestoreManager: FirestoreManager,
    private val authManager: FirebaseAuthManager,
    private val foodRepository: FoodRepository
) : ViewModel() {

    private val _checkoutState = MutableStateFlow<CheckoutState>(CheckoutState.Idle)
    val checkoutState: StateFlow<CheckoutState> = _checkoutState

    fun placeOrder(
        fullName: String,
        phone: String,
        address: String,
        city: String,
        state: String,
        pincode: String,
        paymentMethod: String
    ) {
        viewModelScope.launch {
            _checkoutState.value = CheckoutState.Loading
            val user = authManager.getCurrentUser()
            if (user == null) {
                _checkoutState.value = CheckoutState.Error("You must be logged in to place an order")
                return@launch
            }
            val userId = user.uid

            val items = foodRepository.getCartItems().first()
            if (items.isEmpty()) {
                _checkoutState.value = CheckoutState.Error("Cart is empty")
                return@launch
            }

            val subtotal = items.sumOf { it.price * it.quantity }
            val gstRate = 0.05
            val deliveryFee = 2.0
            val total = subtotal + (subtotal * gstRate) + (if (subtotal > 0) deliveryFee else 0.0)

            val orderItems = items.map {
                OrderItem(
                    id = it.id,
                    title = it.title,
                    price = it.price,
                    quantity = it.quantity,
                    thumbnail = it.thumbnail
                )
            }

            val order = Order(
                orderId = UUID.randomUUID().toString(),
                uid = userId,
                items = orderItems,
                total = total,
                paymentMethod = paymentMethod,
                timestamp = System.currentTimeMillis(),
                status = "Pending",
                fullName = fullName,
                phone = phone,
                address = address,
                city = city,
                state = state,
                pincode = pincode
            )

            // Save to Firestore (caches locally instantly and syncs to backend)
            firestoreManager.saveOrderAsync(order)

            // Instant UI feedback with zero network latency delay
            foodRepository.clearCart()
            _checkoutState.value = CheckoutState.Success(order.orderId)
        }
    }
}

sealed class CheckoutState {
    object Idle : CheckoutState()
    object Loading : CheckoutState()
    data class Success(val orderId: String) : CheckoutState()
    data class Error(val message: String) : CheckoutState()
}
