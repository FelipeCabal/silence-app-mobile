package com.example.silenceapp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.silenceapp.data.local.entity.Comment

@Dao
interface CommentDao {

    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY createdAt ASC")
    fun getCommentsForPost(postId: Int): List<Comment>
}