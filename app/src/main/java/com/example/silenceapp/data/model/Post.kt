package com.example.silenceapp.data.model

data class Post(
    val id: Int,
    val userName: String,
    val content: String? = null,
    val imagenUrl: String? = null,
    val likes: Int = 0,
    val comments: Int = 0
)
