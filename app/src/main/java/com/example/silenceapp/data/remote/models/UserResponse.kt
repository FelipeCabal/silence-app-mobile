package com.example.silenceapp.data.remote.models

import com.example.silenceapp.data.remote.response.User

data class UserResponse(
    val err: Boolean,
    val msg: String,
    val total: Int,
    val page: Int,
    val limit: Int,
    val pages: Int,
    val results: List<User>
)
