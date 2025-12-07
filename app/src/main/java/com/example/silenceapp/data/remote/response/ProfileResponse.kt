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
    @SerializedName("ShowLikes") val showLikes: Boolean = false,
    val publicaciones: List<PublicacionSummary> = emptyList(),
    val comunidades: List<ComunidadInfo> = emptyList(),
    val grupos: List<GrupoInfo> = emptyList(),
    val solicitudesAmistad: List<FriendRequests> = emptyList(),
    val likes: List<LikeResponse> = emptyList(),
    val pubAnonimas: List<JsonElement> = emptyList(),
    val createdAt: String,
    val updatedAt: String,
    @SerializedName("__v") val v: Int
)

data class PublicacionSummary(
    val id: String,
    val summary: PostSummary
)

data class PostSummary(
    val esAnonimo: Boolean,
    val description: String,
    @JsonAdapter(ImagenDeserializer::class)
    val imagen: List<String?>?
)

data class FriendRequests(
    val enviadas: List<FriendRequestItem> = emptyList(),
    val recibidas: List<FriendRequestItem> = emptyList()
)

data class FriendRequestItem(
    @SerializedName("_id") val id: String,
    val from: String,
    val estado: String, // "P" = Pending, "A" = Accepted, "R" = Rejected
    val fecha: String
)

data class ComunidadInfo(
    @SerializedName("_id") val id: String,
    val nombre: String
)

data class GrupoInfo(
    @SerializedName("_id") val id: String
)

data class LikeResponse(
    @SerializedName("_id") val id: String,
    val description: String,
    @JsonAdapter(ImagenDeserializer::class)
    val imagen: List<String?>?,
    val cantLikes: Int,
    val cantComentarios: Int,
    val esAnonimo: Boolean,
    val createdAt: String,
    val owner: LikeOwner? = null
)

data class LikeOwner(
    @SerializedName("_id") val id: String,
    val nombre: String,
    @JsonAdapter(ImagenDeserializer::class)
    val imagen: List<String?>?,
    val userId: String
)
