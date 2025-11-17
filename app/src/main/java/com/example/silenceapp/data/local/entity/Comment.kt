package com.example.silenceapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity(tableName = "comments",
    foreignKeys = [
        ForeignKey(
            entity = Post::class,
            parentColumns = ["id"],
            childColumns = ["postId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["postId"])]
)
data class Comment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val remoteId: String? = null,
    val usuario: UserEntity,
    val comentario: String,
    val postId: Int,
    val CreatedAt: Timestamp? = null
)