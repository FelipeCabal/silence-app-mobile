package com.example.silenceapp.data.remote.service

import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface LikeService {
    @POST("likes/like/{postId}")
    suspend fun likePost(
        @Header("Authorization") authorization: String,
        @Path("postId") postId: String
    )

    @POST("likes/unlike/{postId}")
    suspend fun unlikePost(
        @Header("Authorization") authorization: String,
        @Path("postId") postId: String
    )
}
