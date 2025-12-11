package com.example.silenceapp.data.remote.response

data class UserResponse(
    val _id: String,
    val nombre: String,
    val imagen: String?
)

data class ComentarioResponse(
    val id: String,
    val comentario: String,
    val usuario: UserResponse,
    val createdAt: String
)