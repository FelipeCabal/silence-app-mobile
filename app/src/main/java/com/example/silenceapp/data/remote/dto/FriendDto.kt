package com.example.silenceapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FriendDto(
    @SerializedName("_id")
    val id: String,
    
    @SerializedName("nombre")
    val nombre: String,
    
    @SerializedName("imagen")
    val imagen: String?
)
