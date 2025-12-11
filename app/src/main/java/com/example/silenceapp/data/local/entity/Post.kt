package com.example.silenceapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.silenceapp.data.remote.response.ComentarioResponse

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val remoteId: String? = null,
    val userId: String,
    val userName: String,  // Nombre del usuario (desnormalizado para performance)
    val userImageProfile: String? = null,
    val description: String? = null,
    val images: List<String>,
    val cantLikes: Int = 0,
    val cantComentarios: Int = 0,
    val comentarios: List<ComentarioResponse> = emptyList(),
    val esAnonimo: Boolean = false,
    val hasLiked: Boolean = false,  // Indica si el usuario actual dio like
    val createdAt: Long = System.currentTimeMillis()  // Timestamp en millis
)