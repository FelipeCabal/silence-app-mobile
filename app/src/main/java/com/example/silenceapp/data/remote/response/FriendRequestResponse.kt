package com.example.silenceapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class FriendRequestResponse(
    val id: String,
    val status: String, // P = pending, A = accepted, R = rejected
    val sender: FriendRequestSender
)

data class FriendRequestSender(
    val id: String,
    val nombre: String,
    val imagen: String?,
    val descripcion: String? = "",
    val email: String? = ""
)
