package com.cognevance.foodapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cognevance.foodapp.data.model.User
import com.cognevance.foodapp.firebase.FirebaseAuthManager
import com.cognevance.foodapp.firebase.FirestoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firestoreManager: FirestoreManager,
    private val authManager: FirebaseAuthManager
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            val currentUser = authManager.getCurrentUser()
            if (currentUser != null) {
                val result = firestoreManager.getUser(currentUser.uid)
                if (result.isSuccess && result.getOrNull() != null) {
                    _user.value = result.getOrNull()
                } else {
                    _user.value = User(uid = currentUser.uid, name = currentUser.displayName ?: "User", email = currentUser.email ?: "", phone = "")
                }
            } else {
                _user.value = null
            }
            _isLoading.value = false
        }
    }

    fun logout() {
        authManager.logout()
    }
}
