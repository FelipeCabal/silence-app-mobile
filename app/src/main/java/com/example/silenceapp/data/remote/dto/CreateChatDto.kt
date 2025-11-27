package com.example.silenceapp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para crear un nuevo grupo o comunidad
 */
data class CreateChatDto(
    @SerializedName("nombre")
    val nombre: String,
    
    @SerializedName("descripcion")
    val descripcion: String,
    
    @SerializedName("imagen")
    val imagen: String
)

/**
 * Respuesta al crear un chat
 */
data class CreateChatResponse(
    @SerializedName("err")
    val error: Boolean,
    
    @SerializedName("msg")
    val message: String,
    
    @SerializedName("data")
    val data: ChatCreatedData?
)

data class ChatCreatedData(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("nombre")
    val nombre: String,
    
    @SerializedName("descripcion")
    val descripcion: String,
    
    @SerializedName("imagen")
    val imagen: String?
)
