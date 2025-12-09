package com.example.silenceapp.data.remote.service

import com.example.silenceapp.data.remote.dto.ApiResponse
import com.example.silenceapp.data.remote.dto.FriendDto
import com.example.silenceapp.data.remote.response.GroupInvitationResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface GroupInvitationService {
    
    @GET("users/friends")
    suspend fun getFriends(
        @Header("Authorization") token: String
    ): Response<List<FriendDto>>
    
    @GET("groups/{groupId}/members")
    suspend fun getGroupMembers(
        @Header("Authorization") token: String,
        @Path("groupId") groupId: String
    ): Response<ApiResponse<List<FriendDto>>>
    
    @GET("group-invitations/group/{groupId}/pending")
    suspend fun getPendingInvitations(
        @Header("Authorization") token: String,
        @Path("groupId") groupId: String
    ): Response<ApiResponse<List<FriendDto>>>
    
    @POST("group-invitations/{groupId}/send/{receiverId}")
    suspend fun sendGroupInvitation(
        @Header("Authorization") token: String,
        @Path("groupId") groupId: String,
        @Path("receiverId") receiverId: String
    ): Response<ApiResponse<Unit>>
    
    @GET("group-invitations")
    suspend fun getGroupInvitations(
        @Header("Authorization") token: String
    ): Response<List<GroupInvitationResponse>>
    
    @POST("group-invitations/{id}/accept")
    suspend fun acceptGroupInvitation(
        @Path("id") invitationId: String,
        @Header("Authorization") token: String
    ): Response<Unit>
    
    @DELETE("group-invitations/{id}/reject")
    suspend fun rejectGroupInvitation(
        @Path("id") invitationId: String,
        @Header("Authorization") token: String
    ): Response<Unit>
}
