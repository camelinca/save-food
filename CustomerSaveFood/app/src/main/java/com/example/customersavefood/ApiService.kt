package com.example.customersavefood

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/get_recommendations")
    fun getRecommendations(@Body requestBody: Map<String, String>): Call<RecommendationsResponse>
}