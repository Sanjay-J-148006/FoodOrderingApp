package com.cognevance.foodapp.firebase

import com.cognevance.foodapp.data.model.Order
import com.cognevance.foodapp.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreManager @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    companion object {
        const val USERS_COLLECTION = "users"
        const val ORDERS_COLLECTION = "orders"
    }

    suspend fun saveUser(user: User): Result<Unit> {
        return try {
            firestore.collection(USERS_COLLECTION).document(user.uid).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUser(uid: String): Result<User?> {
        return try {
            val document = firestore.collection(USERS_COLLECTION).document(uid).get().await()
            if (document.exists()) {
                Result.success(document.toObject(User::class.java))
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveOrder(order: Order): Result<Unit> {
        return try {
            firestore.collection(ORDERS_COLLECTION).document(order.orderId).set(order).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun saveOrderAsync(order: Order) {
        firestore.collection(ORDERS_COLLECTION).document(order.orderId).set(order)
    }

    suspend fun getOrdersForUser(uid: String): Result<List<Order>> {
        return try {
            val snapshot = firestore.collection(ORDERS_COLLECTION)
                .whereEqualTo("uid", uid)
                .get()
                .await()
            val orders = snapshot.documents.mapNotNull { it.toObject(Order::class.java) }
            Result.success(orders.sortedByDescending { it.timestamp })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
