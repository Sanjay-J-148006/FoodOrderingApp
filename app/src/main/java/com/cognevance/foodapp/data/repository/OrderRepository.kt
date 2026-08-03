package com.cognevance.foodapp.data.repository

import com.cognevance.foodapp.data.model.Order
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class OrderRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun saveOrder(order: Order): String {
        val documentRef = firestore.collection("orders").document()
        val orderWithId = order.copy(orderId = documentRef.id)
        documentRef.set(orderWithId).await()
        return documentRef.id
    }

    suspend fun getOrderHistory(uid: String): List<Order> {
        val querySnapshot = firestore.collection("orders")
            .whereEqualTo("uid", uid)
            .get()
            .await()
        
        // Sort locally in memory to avoid index requirements in Firestore console.
        return querySnapshot.toObjects(Order::class.java).sortedByDescending { it.timestamp }
    }
}
