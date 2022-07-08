package com.example.socialx.api.models

data class News(
    val status: String,
    val totalResults: Int,
    val articles: ArrayList<Articles>
)
