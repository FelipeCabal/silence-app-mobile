package com.example.silenceapp.data.remote.dto

data class RegisterRequest(
    val nombre: String,
    val email: String,
    val password: String,
    val sexo: String,
    val fechaNto: String,
    val pais: String
)