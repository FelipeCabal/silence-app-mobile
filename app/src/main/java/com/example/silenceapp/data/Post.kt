package com.example.silenceapp.data

data class Post(
    val id: Long,
    val author: String,
    val avatarUrl: String?,
    val timeAgo: String,
    val text: String,
    val imageUrl: String?
)
