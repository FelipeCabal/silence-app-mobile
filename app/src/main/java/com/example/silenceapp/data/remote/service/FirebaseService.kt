package com.example.silenceapp.data.remote.service

import com.example.silenceapp.data.remote.response.UploadImageResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface FirebaseService {
    @Multipart
    @POST("firebase/upload")
    suspend fun uploadImage(
        @Header("Authorization") authorization: String,
        @Part image: MultipartBody.Part,
        @Part("folder") folder: RequestBody? = null
    ): UploadImageResponse
}
