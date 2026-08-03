package com.cognevance.foodapp.utils

import android.util.Patterns

object ValidationUtils {
    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    fun isValidPhone(phone: String): Boolean {
        // Simple validation: should be digits and length between 7 and 15
        val phoneRegex = "^[0-9]{7,15}$".toRegex()
        return phone.isNotBlank() && phoneRegex.matches(phone)
    }

    fun isNotEmpty(value: String): Boolean {
        return value.trim().isNotEmpty()
    }
}
