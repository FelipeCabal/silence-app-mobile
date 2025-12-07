package com.example.silenceapp.data.remote.service

import com.example.silenceapp.data.local.entity.Chat
import com.example.silenceapp.data.local.entity.Members
import com.example.silenceapp.data.local.entity.Message
import com.example.silenceapp.data.remote.dto.ApiResponse
import com.example.silenceapp.data.remote.dto.ChatMessagesResponse
import com.example.silenceapp.data.remote.dto.CommunityDto
import com.example.silenceapp.data.remote.dto.CreateChatDto
import com.example.silenceapp.data.remote.dto.CreateChatResponse
import com.example.silenceapp.data.remote.dto.CreatePrivateChatDto
import com.example.silenceapp.data.remote.dto.GroupDto
import com.example.silenceapp.data.remote.dto.PrivateChatDto
import retrofit2.Response
import retrofit2.http.*

interface ChatService {

    // ========== OBTENER CHATS ==========
    
    @GET("chat-privado")
    suspend fun getPrivateChats(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<PrivateChatDto>>>

    @GET("chat-privado/{id}")
    suspend fun getPrivateChatById(
        @Header("Authorization") token: String,
        @Path("id") chatId: String
    ): Response<ApiResponse<PrivateChatDto>>

    @GET("community")
    suspend fun getCommunities(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<CommunityDto>>>

    @GET("groups")
    suspend fun getGroups(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<GroupDto>>>

    // ========== CREAR CHATS ==========
    
    @POST("chat-privado")
    suspend fun createPrivateChat(
        @Header("Authorization") token: String,
        @Body chatData: CreatePrivateChatDto
    ): Response<ApiResponse<PrivateChatDto>>
    
    @POST("groups")
    suspend fun createGroup(
        @Header("Authorization") token: String,
        @Body chatData: CreateChatDto
    ): Response<CreateChatResponse>

    @POST("community")
    suspend fun createCommunity(
        @Header("Authorization") token: String,
        @Body chatData: CreateChatDto
    ): Response<CreateChatResponse>
    
    // ========== ELIMINAR CHATS ==========
    
    @DELETE("chat-privado/{id}")
    suspend fun deletePrivateChat(
        @Header("Authorization") token: String,
        @Path("id") chatId: String
    ): Response<ApiResponse<Unit>>

    // ========== OPERACIONES DE CHAT ESPECÍFICO ==========
    
    @GET("chats/{chatId}")
    suspend fun getChatMessages(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: String,
        @Query("chatType") chatType: String,
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null
    ): Response<ChatMessagesResponse>

    @POST("chats")
    suspend fun createChat(
        @Header("Authorization") token: String,
        @Body chat: Chat
    ): Response<Chat>

    @PATCH("chats/{chatId}")
    suspend fun updateChat(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: String,
        @Body chat: Chat
    ): Response<Chat>

    @DELETE("chats/{chatId}")
    suspend fun deleteChat(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: String
    ): Response<Unit>
}
