package com.example.silenceapp.data.remote.service

import com.example.silenceapp.data.remote.response.GroupInvitationResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface GroupInvitationService {
    
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
