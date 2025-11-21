package com.example.silenceapp.data.repository

import com.example.silenceapp.data.local.dao.PostDao
import com.example.silenceapp.data.local.entity.Post

class PostRepository(private val postDao: PostDao) {

    suspend fun getPosts(): List<Post> {
        return postDao.getPosts()
    }
    
    suspend fun createPost(post: Post): Boolean {
        return try {
            postDao.insertPost(post)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun deletePost(postId: Int): Boolean {
        return try {
            postDao.deletePost(postId)
            true
        } catch (e: Exception) {
            false
        }
    }
}