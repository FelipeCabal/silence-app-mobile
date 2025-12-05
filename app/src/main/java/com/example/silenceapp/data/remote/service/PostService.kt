package com.example.silenceapp.data.remote.service

import com.example.silenceapp.data.remote.response.PostDetailResponse
import com.example.silenceapp.data.remote.response.PostResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface PostService {

    @GET("posts")
    suspend fun getAllPosts(): List<PostResponse>

    @GET("posts/{id}")
    suspend fun getPostById(
        @Path("id") postId: String
    ): PostDetailResponse
}