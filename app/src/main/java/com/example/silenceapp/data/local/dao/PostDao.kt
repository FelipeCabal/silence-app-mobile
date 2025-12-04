package com.example.silenceapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.silenceapp.data.local.entity.Post

@Dao
interface PostDao {
    @Query("SELECT * FROM posts ORDER BY createdAt DESC")
    suspend fun getPosts(): List<Post>
    
    @Insert
    suspend fun insertPost(post: Post): Long
    
    @Update
    suspend fun updatePost(post: Post)
    
    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePost(postId: Int)
}