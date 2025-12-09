package com.example.silenceapp.data.remote.response

data class ComentarioResponse(
    val id: String,
    val usuario: ProfileResponse,
    val comentario: String,
    val createdAt: String
)
