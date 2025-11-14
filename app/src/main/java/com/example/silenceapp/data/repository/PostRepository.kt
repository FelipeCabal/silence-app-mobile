package com.example.silenceapp.data.repository

import com.example.silenceapp.data.local.dao.PostDao
import com.example.silenceapp.data.local.entity.Post

class PostRepository(private val postDao: PostDao) {

    suspend fun getPosts():List<Post>{
        return postDao.getPosts()
    }
}