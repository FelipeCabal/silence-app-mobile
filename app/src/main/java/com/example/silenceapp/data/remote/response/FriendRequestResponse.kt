package com.example.silenceapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class FriendRequestResponse(
    @SerializedName("_id") val id: String,
    @SerializedName("userEnvia") val senderId: String,  // Solo ID del que envía
    @SerializedName("userRecibe") val receiverId: String,  // Solo ID del que recibe
    val status: String, // P = pending, A = accepted, R = rejected
    val createdAt: String,
    val updatedAt: String,
    val chatPrivado: String? = null
)
