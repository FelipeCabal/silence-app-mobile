package com.example.silenceapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.silenceapp.data.remote.response.ComentarioResponse

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val remoteId: String? = null,
    val userId: String,  // ID del usuario que creó el post (del backend)
    val userName: String,  // Nombre del usuario (desnormalizado para performance)
    val userImageProfile: String? = null,
    val description: String? = null,
    val images: String?,  // JSON string array de URIs: ["uri1", "uri2"]
    val cantLikes: Int = 0,
    val cantComentarios: Int = 0,
    val comentarios: String? = null,
    val esAnonimo: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()  // Timestamp en millis
)