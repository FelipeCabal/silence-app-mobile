package com.example.silenceapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class PostResponse(
    @SerializedName("_id") val id: String = "",
    @SerializedName("userId") val userId: String = "",
    @SerializedName("userName") val userName: String = "",
    val description: String? = null,
    val imagen: String? = null,
    val cantLikes: Int = 0,
    val cantComentarios: Int = 0,
    val esAnonimo: Boolean = false,
    val createdAt: String? = null
)