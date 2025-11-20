package com.example.silenceapp.data.remote.service

import com.example.silenceapp.data.remote.dto.UpdateProfileRequest
import com.example.silenceapp.data.remote.response.ProfileResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Path

interface UserService {
    @PATCH("users/{id}")
    suspend fun update(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
        @Body user: UpdateProfileRequest
    ): ProfileResponse
}