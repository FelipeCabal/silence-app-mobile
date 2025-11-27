package com.example.silenceapp.data.remote.dto

import com.google.gson.annotations.SerializedName

// Respuestas envueltas en el formato del backend
data class ApiResponse<T>(
    @SerializedName("err")
    val error: Boolean,
    
    @SerializedName("msg")
    val message: String,
    
    @SerializedName("data")
    val data: T?
)

// DTO para chats privados (PENDIENTE - API no disponible aún)
// TODO: Descomentar cuando el endpoint /chat-privado esté disponible
/*
data class PrivateChatDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("participants")
    val participants: List<ParticipantDto>,
    
    @SerializedName("lastMessage")
    val lastMessage: String?,
    
    @SerializedName("lastMessageDate")
    val lastMessageDate: String
)

data class ParticipantDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("username")
    val username: String,
    
    @SerializedName("profileImage")
    val profileImage: String?
)
*/

// DTO para comunidades
data class CommunityDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("nombre")
    val nombre: String,
    
    @SerializedName("imagen")
    val imagen: String?,
    
    @SerializedName("lastMessage")
    val lastMessage: String?,
    
    @SerializedName("lastMessageDate")
    val lastMessageDate: String
)

// DTO para grupos
data class GroupDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("nombre")
    val nombre: String,
    
    @SerializedName("imagen")
    val imagen: String?,
    
    @SerializedName("lastMessage")
    val lastMessage: String?,
    
    @SerializedName("lastMessageDate")
    val lastMessageDate: String
)
