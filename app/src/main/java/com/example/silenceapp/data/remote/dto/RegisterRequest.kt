package com.example.silenceapp.data.remote.dto

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val phone: String
)