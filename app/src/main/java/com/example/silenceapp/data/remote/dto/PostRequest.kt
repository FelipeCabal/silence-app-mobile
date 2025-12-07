package com.example.silenceapp.data.remote.dto

data class PostRequest(
    val description: String?,
    val imagen: List<String>?,
    val esAnonimo: Boolean?
)