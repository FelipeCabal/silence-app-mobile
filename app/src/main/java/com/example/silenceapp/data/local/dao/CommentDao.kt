package com.example.silenceapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.silenceapp.data.local.entity.Comment
import com.example.silenceapp.data.remote.response.ComentarioResponse

@Dao
interface CommentDao {

    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY createdAt ASC")
    fun getCommentsForPost(postId: Int): List<Comment>

    @Insert
    fun insertComments(comments: List<Comment>)

    @Query("DELETE FROM comments WHERE postId = :postId")
    fun deleteCommentsForPost(postId: Int)
}