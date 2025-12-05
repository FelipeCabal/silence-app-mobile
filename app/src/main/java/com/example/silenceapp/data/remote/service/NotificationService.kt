package com.example.silenceapp.data.remote.service

import com.example.silenceapp.data.remote.response.NotificationResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Path

interface NotificationService {
    
    @GET("notifications")
    suspend fun getNotifications(
        @Header("Authorization") token: String
    ): Response<List<NotificationResponse>>
    
    @PATCH("notifications/{id}/read")
    suspend fun markNotificationAsRead(
        @Path("id") notificationId: String,
        @Header("Authorization") token: String
    ): Response<Unit>
}
