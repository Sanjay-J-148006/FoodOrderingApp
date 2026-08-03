package com.cognevance.foodapp.data.api

import com.cognevance.foodapp.data.model.ProductResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FoodApiService {
    @GET("products/category/groceries")
    suspend fun getGroceries(): ProductResponse

    @GET("products/search")
    suspend fun searchProducts(
        @Query("q") query: String
    ): ProductResponse
}
