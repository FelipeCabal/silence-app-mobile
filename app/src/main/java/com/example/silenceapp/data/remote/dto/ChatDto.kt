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

// DTO para chats privados
data class PrivateChatDto(
    @SerializedName(value = "_id", alternate = ["id"])
    val id: String,
    
    @SerializedName("nombre")
    val nombre: String? = null,
    
    @SerializedName("imagen")
    val imagen: String? = null,
    
    @SerializedName("participants")
    val participants: List<ParticipantDto>? = null,
    
    @SerializedName("lastMessage")
    val lastMessage: String? = null,
    
    @SerializedName("updatedAt")
    val updatedAt: String,
    
    @SerializedName("createdAt")
    val createdAt: String
)

data class ParticipantDto(
    @SerializedName("_id")
    val id: String,
    
    @SerializedName("nombre")
    val nombre: String,
    
    @SerializedName("imagen")
    val imagen: List<String>?
)

data class LastMessageDto(
    @SerializedName("content")
    val content: String,
    
    @SerializedName("timestamp")
    val timestamp: String
)

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
