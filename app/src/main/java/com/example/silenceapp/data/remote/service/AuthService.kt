package com.example.silenceapp.data.remote.service

import com.example.silenceapp.data.remote.dto.LoginRequest
import com.example.silenceapp.data.remote.dto.RegisterRequest
import com.example.silenceapp.data.remote.response.AuthResponse
import com.example.silenceapp.data.remote.response.ProfileResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.GET

interface AuthService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @GET("auth/profile")
    suspend fun profile(@Header("Authorization") authorization: String): ProfileResponse
}
