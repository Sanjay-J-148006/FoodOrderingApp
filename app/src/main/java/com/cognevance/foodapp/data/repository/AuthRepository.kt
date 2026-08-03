package com.cognevance.foodapp.data.repository

import com.cognevance.foodapp.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    val isUserLoggedIn: Boolean
        get() = firebaseAuth.currentUser != null

    suspend fun login(email: String, password: String): FirebaseUser {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        return result.user ?: throw Exception("Login failed: User is null")
    }

    suspend fun signUp(email: String, password: String, name: String, phone: String): FirebaseUser {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val firebaseUser = result.user ?: throw Exception("Registration failed: User is null")
        
        // Save user details to Firestore
        val user = User(
            uid = firebaseUser.uid,
            name = name,
            email = email,
            phone = phone
        )
        
        firestore.collection("users")
            .document(firebaseUser.uid)
            .set(user)
            .await()

        return firebaseUser
    }

    suspend fun resetPassword(email: String) {
        firebaseAuth.sendPasswordResetEmail(email).await()
    }

    fun logout() {
        firebaseAuth.signOut()
    }

    suspend fun getUserProfile(uid: String): User? {
        val documentSnapshot = firestore.collection("users")
            .document(uid)
            .get()
            .await()
        return documentSnapshot.toObject(User::class.java)
    }

    suspend fun updateUserProfile(uid: String, name: String, phone: String) {
        firestore.collection("users")
            .document(uid)
            .update(
                mapOf(
                    "name" to name,
                    "phone" to phone
                )
            )
            .await()
    }
}
