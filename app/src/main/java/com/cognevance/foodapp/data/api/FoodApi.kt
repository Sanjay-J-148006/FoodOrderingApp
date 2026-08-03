package com.cognevance.foodapp.data.api

import com.cognevance.foodapp.data.model.FoodItem
import com.cognevance.foodapp.data.model.ProductResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FoodApi {
    @GET("products/category/groceries")
    suspend fun getGroceries(): Response<ProductResponse>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): Response<FoodItem>
    
    @GET("products/search")
    suspend fun searchProducts(@Query("q") query: String): Response<ProductResponse>
}
