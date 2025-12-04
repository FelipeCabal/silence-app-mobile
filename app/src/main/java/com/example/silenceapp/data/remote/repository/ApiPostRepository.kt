package com.example.silenceapp.data.remote.repository

import com.example.silenceapp.data.local.entity.Post
import com.example.silenceapp.data.remote.client.ApiClient
import com.example.silenceapp.data.mappers.toLocalPost
import com.example.silenceapp.data.mappers.toLocalPostDetail

class ApiPostRepository () {

    private val api = ApiClient.postService

    suspend fun getAllPosts(currentUserId: String? = null): List<Post> {
        val response = api.getAllPosts()
        return response.map{ it.toLocalPost(currentUserId) }
    }

    suspend fun getPostById(id: String, currentUserId: String? = null): Post {
        val response = api.getPostById(id)
        return response.toLocalPostDetail(currentUserId)
    }
}