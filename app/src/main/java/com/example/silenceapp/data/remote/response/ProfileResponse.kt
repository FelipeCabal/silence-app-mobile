package com.example.silenceapp.data.remote.response

import com.google.gson.JsonElement
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @SerializedName("_id") val id: String,
    val nombre: String,
    val email: String,
    val fechaNto: String,
    val sexo: String,
    val pais: String,
    @JsonAdapter(ImagenDeserializer::class)
    val imagen: List<String?>?,
    val username: String? = null,
    val descripcion: String? = null,
    val seguidores: Int = 0,
    val amigos: Int = 0,
    val publicacionesCount: Int = 0,
    val relationshipStatus: RelationshipStatusResponse? = null,
    @SerializedName("ShowLikes") val showLikes: Boolean,
    val publicaciones: List<PostResponse> = emptyList(),
    val comunidades: List<JsonElement> = emptyList(),
    val grupos: List<JsonElement> = emptyList(),
    val solicitudesAmistad: List<FriendRequests> = emptyList(),
    val likes: List<LikeResponse> = emptyList(),
    val createdAt: String,
    val updatedAt: String,
    @SerializedName("__v") val v: Int
)

data class FriendRequests(
    val enviadas: List<JsonElement> = emptyList(),
    val recibidas: List<JsonElement> = emptyList()
)

data class LikeResponse(
    @SerializedName("_id") val id: String,
    val description: String,
    @JsonAdapter(ImagenDeserializer::class)
    val imagen: List<String?>?,
    val cantLikes: Int,
    val cantComentarios: Int,
    val esAnonimo: Boolean,
    val createdAt: String
)
