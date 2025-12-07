package com.example.silenceapp.data.remote.service

import com.example.silenceapp.data.remote.dto.FriendRequestDto
import com.example.silenceapp.data.remote.dto.ReportUserRequest
import com.example.silenceapp.data.remote.response.PostResponse
import com.example.silenceapp.data.remote.response.ProfileResponse
import com.example.silenceapp.data.remote.response.RelationshipStatusResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ProfileService {
    @GET("users/{id}")
    suspend fun getUserProfile(
        @Header("Authorization") authorization: String,
        @Path("id") userId: String
    ): ProfileResponse

    @GET("users/{id}/posts")
    suspend fun getUserPosts(
        @Header("Authorization") authorization: String,
        @Path("id") userId: String
    ): List<PostResponse>

    @GET("friends/status/{id}")
    suspend fun getRelationshipStatus(
        @Header("Authorization") authorization: String,
        @Path("id") userId: String
    ): RelationshipStatusResponse

    @POST("friend-request/request/{id}")
    suspend fun sendFriendRequest(
        @Header("Authorization") authorization: String,
        @Path("id") userId: String
    ): RelationshipStatusResponse

    @DELETE("friends/request/{id}")
    suspend fun cancelFriendRequest(
        @Header("Authorization") authorization: String,
        @Path("id") userId: String
    ): RelationshipStatusResponse

    @DELETE("friends/{id}")
    suspend fun removeFriend(
        @Header("Authorization") authorization: String,
        @Path("id") userId: String
    ): RelationshipStatusResponse

    @POST("reports/user")
    suspend fun reportUser(
        @Header("Authorization") authorization: String,
        @Body body: ReportUserRequest
    )
}