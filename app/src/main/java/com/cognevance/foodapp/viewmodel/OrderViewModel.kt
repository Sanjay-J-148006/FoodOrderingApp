package com.cognevance.foodapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cognevance.foodapp.data.model.CartItem
import com.cognevance.foodapp.data.model.Order
import com.cognevance.foodapp.data.model.OrderItem
import com.cognevance.foodapp.data.repository.CartRepository
import com.cognevance.foodapp.data.repository.OrderRepository
import com.cognevance.foodapp.utils.ValidationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface OrderUiState {
    object Idle : OrderUiState
    object Loading : OrderUiState
    data class Success(val orderId: String) : OrderUiState
    data class Error(val message: String) : OrderUiState
}

sealed interface HistoryUiState {
    object Loading : HistoryUiState
    data class Success(val orders: List<Order>) : HistoryUiState
    data class Error(val message: String) : HistoryUiState
}

class OrderViewModel(
    private val orderRepository: OrderRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _checkoutState = MutableStateFlow<OrderUiState>(OrderUiState.Idle)
    val checkoutState: StateFlow<OrderUiState> = _checkoutState.asStateFlow()

    private val _historyState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val historyState: StateFlow<HistoryUiState> = _historyState.asStateFlow()

    // Form Fields
    val fullName = MutableStateFlow("")
    val phone = MutableStateFlow("")
    val address = MutableStateFlow("")
    val city = MutableStateFlow("")
    val state = MutableStateFlow("")
    val pincode = MutableStateFlow("")
    val paymentMethod = MutableStateFlow("Cash on Delivery")

    fun placeOrder(uid: String, cartItems: List<CartItem>, total: Double) {
        if (!ValidationUtils.isNotEmpty(fullName.value)) {
            _checkoutState.value = OrderUiState.Error("Full Name is required")
            return
        }
        if (!ValidationUtils.isValidPhone(phone.value)) {
            _checkoutState.value = OrderUiState.Error("Valid Phone Number is required")
            return
        }
        if (!ValidationUtils.isNotEmpty(address.value)) {
            _checkoutState.value = OrderUiState.Error("Delivery Address is required")
            return
        }
        if (!ValidationUtils.isNotEmpty(city.value)) {
            _checkoutState.value = OrderUiState.Error("City is required")
            return
        }
        if (!ValidationUtils.isNotEmpty(state.value)) {
            _checkoutState.value = OrderUiState.Error("State is required")
            return
        }
        if (pincode.value.trim().length < 5) {
            _checkoutState.value = OrderUiState.Error("Enter a valid Pincode")
            return
        }
        if (cartItems.isEmpty()) {
            _checkoutState.value = OrderUiState.Error("Your cart is empty")
            return
        }

        viewModelScope.launch {
            _checkoutState.value = OrderUiState.Loading
            try {
                // Simulate payment gateway delays if not Cash on Delivery
                if (paymentMethod.value != "Cash on Delivery") {
                    kotlinx.coroutines.delay(1500)
                }

                val orderItems = cartItems.map {
                    OrderItem(
                        id = it.id,
                        title = it.title,
                        price = it.price,
                        quantity = it.quantity,
                        thumbnail = it.thumbnail
                    )
                }

                val order = Order(
                    uid = uid,
                    items = orderItems,
                    total = total,
                    paymentMethod = paymentMethod.value,
                    status = "Pending",
                    fullName = fullName.value,
                    phone = phone.value,
                    address = address.value,
                    city = city.value,
                    state = state.value,
                    pincode = pincode.value
                )

                val orderId = orderRepository.saveOrder(order)
                
                // Clear SQLite Room Cart
                cartRepository.clearCart()
                
                // Clear address fields
                resetForm()
                
                _checkoutState.value = OrderUiState.Success(orderId)
            } catch (e: Exception) {
                _checkoutState.value = OrderUiState.Error(e.message ?: "Failed to place order")
            }
        }
    }

    fun loadOrderHistory(uid: String) {
        viewModelScope.launch {
            _historyState.value = HistoryUiState.Loading
            try {
                val orders = orderRepository.getOrderHistory(uid)
                _historyState.value = HistoryUiState.Success(orders)
            } catch (e: Exception) {
                _historyState.value = HistoryUiState.Error(e.message ?: "Failed to load orders")
            }
        }
    }

    fun resetCheckoutState() {
        _checkoutState.value = OrderUiState.Idle
    }

    private fun resetForm() {
        fullName.value = ""
        phone.value = ""
        address.value = ""
        city.value = ""
        state.value = ""
        pincode.value = ""
        paymentMethod.value = "Cash on Delivery"
    }
}
