package com.example.silenceapp.data.remote.service

import com.example.silenceapp.data.remote.response.Community
import com.example.silenceapp.data.remote.response.User
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import com.example.silenceapp.data.remote.models.CommunityResponse
import retrofit2.http.Header
import retrofit2.http.POST
import com.example.silenceapp.data.remote.response.FriendRequestResponse
import com.example.silenceapp.data.remote.response.Community_Member_RequestResponse
import com.example.silenceapp.data.remote.response.FriendRequest
import com.example.silenceapp.data.remote.response.CommunityRequest

interface SearchService {

    @GET("/api/users/{id}")
    suspend fun getUserById(@Path("id") id: String): User?

    @GET("/api/community/{id}")
    suspend fun getCommunityById(@Path("id") id: String): Community?

    @GET("/api/community/all")
    suspend fun getAllCommunities(
        @Query("search") search: String = "",
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): CommunityResponse

    @GET("/api/users/others")
    suspend fun getAllUsers(
        @Header("Authorization") authorization: String,
    ): List<User>

    @POST("/api/friend-request/request/{id}")
    suspend fun postFriend_Request(
        @Path("id") id: String,
        @Header("Authorization") authorization: String
    ): FriendRequestResponse

    @POST("/api/community/{id}/members")
    suspend fun postCommunity_Request(
        @Path("id") id: String,
        @Header("Authorization") authorization: String
    ): Community_Member_RequestResponse

    @GET("/api/friend-request/user")
    suspend fun getMyFriendRequests(
        @Header("Authorization") authorization: String
    ): List<FriendRequest>

    @GET("/api/community")
    suspend fun getMyCommunitiesRequests(
        @Header("Authorization") authorization: String
    ): CommunityRequest

}
