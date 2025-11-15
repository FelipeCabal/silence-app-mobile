package com.example.silenceapp.data.remote.service

import com.example.silenceapp.data.remote.dto.LoginRequest
import com.example.silenceapp.data.remote.dto.RegisterRequest
import com.example.silenceapp.data.remote.response.AuthResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response

interface AuthService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse
}
