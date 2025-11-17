package com.example.silenceapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp
@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val remoteId: String? = null,
    val user: UserEntity,
    val description: String? = null,
    val imagen: String? = null,
    val cantlikes: Int = 0,
    val comentarios: List<Comment>? = emptyList<Comment>(),
    val cantComentarios: Int = 0,
    val esAnonimo: Boolean? = false,
    val createdAt: Timestamp? = null

)