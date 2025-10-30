package com.example.silenceapp.data.remote.response

data class AuthResponse(
    val success: Boolean,
    val message: String?,
    val token: String?,
    val user: UserResponse?
)

data class UserResponse(
    val id: String,
    val name: String,
    val email: String
)
