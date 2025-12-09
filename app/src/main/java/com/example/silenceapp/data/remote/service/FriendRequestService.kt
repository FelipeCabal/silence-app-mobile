package com.example.silenceapp.data.remote.service

import com.example.silenceapp.data.remote.response.FriendRequestResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Path

interface FriendRequestService {
    
    @GET("friend-request/user/received")
    suspend fun getFriendRequests(
        @Header("Authorization") token: String
    ): Response<List<FriendRequestResponse>>
    
    // TODO: Actualizar cuando los endpoints estén disponibles
    // Probablemente serán POST y DELETE como group-invitations
    @PATCH("friend-request/{id}/accept")
    suspend fun acceptFriendRequest(
        @Path("id") requestId: String,
        @Header("Authorization") token: String
    ): Response<Unit>
    
    @PATCH("friend-request/{id}/reject")
    suspend fun rejectFriendRequest(
        @Path("id") requestId: String,
        @Header("Authorization") token: String
    ): Response<Unit>
}
