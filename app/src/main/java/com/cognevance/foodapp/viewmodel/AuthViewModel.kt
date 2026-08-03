package com.cognevance.foodapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cognevance.foodapp.firebase.FirebaseAuthManager
import com.cognevance.foodapp.firebase.FirestoreManager
import com.cognevance.foodapp.data.model.User
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authManager: FirebaseAuthManager,
    private val firestoreManager: FirestoreManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        val user = authManager.getCurrentUser()
        if (user != null) {
            _authState.value = AuthState.Authenticated(user)
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val trimmedEmail = email.trim()
            val result = authManager.login(trimmedEmail, password)
            if (result.isSuccess) {
                _authState.value = AuthState.Authenticated(result.getOrNull()!!)
            } else {
                val rawMsg = result.exceptionOrNull()?.message ?: "Login failed"
                val friendlyMsg = when {
                    rawMsg.contains("supplied auth credential", ignoreCase = true) ||
                    rawMsg.contains("invalid credential", ignoreCase = true) ||
                    rawMsg.contains("user-not-found", ignoreCase = true) ||
                    rawMsg.contains("wrong-password", ignoreCase = true) ->
                        "Account not found or password incorrect. If you haven't created an account yet, please tap 'Sign Up' below to register!"
                    rawMsg.contains("badly formatted", ignoreCase = true) ||
                    rawMsg.contains("invalid-email", ignoreCase = true) ->
                        "Please enter a valid email address."
                    else -> rawMsg
                }
                _authState.value = AuthState.Error(friendlyMsg)
            }
        }
    }

    fun signup(name: String, email: String, phone: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val trimmedEmail = email.trim()
            val result = authManager.signup(trimmedEmail, password)
            if (result.isSuccess) {
                val user = result.getOrNull()!!
                val newUser = User(uid = user.uid, name = name.trim(), email = trimmedEmail, phone = phone.trim())
                firestoreManager.saveUser(newUser)
                _authState.value = AuthState.Authenticated(user)
            } else {
                val rawMsg = result.exceptionOrNull()?.message ?: "Signup failed"
                val friendlyMsg = when {
                    rawMsg.contains("email-already-in-use", ignoreCase = true) ||
                    rawMsg.contains("already in use", ignoreCase = true) ->
                        "An account with this email already exists. Please tap 'Login' to sign in."
                    rawMsg.contains("weak-password", ignoreCase = true) ||
                    rawMsg.contains("at least 6 characters", ignoreCase = true) ->
                        "Password must be at least 6 characters long."
                    else -> rawMsg
                }
                _authState.value = AuthState.Error(friendlyMsg)
            }
        }
    }

    fun resetPassword(email: String) {
        if (email.isBlank()) {
            _authState.value = AuthState.Error("Please enter your email address")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authManager.resetPassword(email)
            if (result.isSuccess) {
                _authState.value = AuthState.Success("Password reset email sent successfully. Check your inbox!")
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Failed to send reset email")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    fun logout() {
        authManager.logout()
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Authenticated(val user: FirebaseUser) : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}
