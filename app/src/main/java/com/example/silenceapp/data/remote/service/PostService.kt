package com.example.silenceapp.data.remote.service

import com.example.silenceapp.data.remote.dto.PostRequest
import com.example.silenceapp.data.remote.response.PostDetailResponse
import com.example.silenceapp.data.remote.response.PostResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface PostService {

    @GET("posts")
    suspend fun getAllPosts(): List<PostResponse>

    @GET("posts/{id}")
    suspend fun getPostById(
        @Path("id") postId: String
    ): PostDetailResponse

    @POST("posts")
    suspend fun createPost(
        @Header("Authorization") token: String,
        @Body post: PostRequest
    ): PostDetailResponse
}