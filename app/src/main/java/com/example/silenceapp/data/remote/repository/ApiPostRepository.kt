package com.example.silenceapp.data.remote.repository

import com.example.silenceapp.data.local.entity.Post
import com.example.silenceapp.data.remote.client.ApiClient
import com.example.silenceapp.data.mappers.toLocalPost
import com.example.silenceapp.data.mappers.toLocalPostDetail

class ApiPostRepository () {

    private val api = ApiClient.postService

    suspend fun getAllPosts(): List<Post> {
        val response = api.getAllPosts()
        return response.map{ it.toLocalPost() }
    }

    suspend fun getPostById(id: String): Post {
        val response = api.getPostById(id)
        return response.toLocalPostDetail()
    }
}