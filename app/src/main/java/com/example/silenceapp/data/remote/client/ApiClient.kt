package com.example.silenceapp.data.remote.client

import com.example.silenceapp.BuildConfig
import com.example.silenceapp.data.remote.service.AuthService
import android.util.Log
import com.example.silenceapp.data.remote.response.ImagenDeserializer
import com.example.silenceapp.data.remote.service.FirebaseService
import com.example.silenceapp.data.remote.service.LikeService
import com.example.silenceapp.data.remote.service.PostService
import com.example.silenceapp.data.remote.service.UserService
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = BuildConfig.BASE_URL
    private const val TAG = "ApiClient"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = GsonBuilder()
        .registerTypeAdapter(
            object: TypeToken<List<String?>?>() {}.type,
            ImagenDeserializer()
        )
        .create()
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val authService: AuthService = retrofit.create(AuthService::class.java)
    val userService: UserService = retrofit.create(UserService::class.java)
    val firebaseService: FirebaseService = retrofit.create(FirebaseService::class.java)

    val postService: PostService = retrofit.create(PostService::class.java)
    val likeService: LikeService = retrofit.create(LikeService::class.java)

    init {
        Log.d(TAG, "Using BASE_URL: $BASE_URL")
    }
}
