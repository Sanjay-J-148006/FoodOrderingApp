package com.cognevance.foodapp

import android.app.Application
import com.google.firebase.FirebaseApp

class FoodApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Explicitly initialize Firebase context
        try {
            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
