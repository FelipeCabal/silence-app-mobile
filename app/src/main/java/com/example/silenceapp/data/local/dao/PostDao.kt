package com.example.silenceapp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.silenceapp.data.local.entity.Post

@Dao
interface PostDao {
    @Query("SELECT * FROM posts ORDER BY createdAt ASC")
    suspend fun  getPosts():List<Post>
}