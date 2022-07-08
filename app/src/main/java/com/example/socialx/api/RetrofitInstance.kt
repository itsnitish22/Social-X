package com.example.socialx.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val BASE_URL = "https://newsapi.org/"

object RetrofitInstance {
    //for logging
    var loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    var clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder().addInterceptor(
        loggingInterceptor
    )

    //boilerplate
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(clientBuilder.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: NewsAPI by lazy {
        retrofit.create(NewsAPI::class.java)
    }
}