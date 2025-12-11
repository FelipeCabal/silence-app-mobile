package com.example.silenceapp.data.repository

import com.example.silenceapp.data.local.dao.CommentDao
import com.example.silenceapp.data.local.entity.Comment
import com.example.silenceapp.data.remote.response.ComentarioResponse

class CommentRepository(private val commentDao: CommentDao){

    fun getComments(postId: Int): List<Comment>{
        return commentDao.getCommentsForPost(postId)
    }
}