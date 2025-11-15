package com.example.silenceapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("access_token")
    val access_token: String
)

data class UserResponse(
    val id: String,
    val name: String,
    val email: String
)
