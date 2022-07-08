package com.example.socialx.api

import com.example.socialx.api.models.News
import retrofit2.http.GET

interface NewsAPI {
    @GET("v2/top-headlines?country=in&apiKey=619d45f4e8ce4f66b8bdd2db720f24f3")
    suspend fun getNews(): News
}