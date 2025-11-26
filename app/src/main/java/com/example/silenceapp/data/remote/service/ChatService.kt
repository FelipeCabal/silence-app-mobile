package com.example.silenceapp.data.remote.service

import com.example.silenceapp.data.local.entity.Chat
import com.example.silenceapp.data.local.entity.Members
import com.example.silenceapp.data.local.entity.Message
import retrofit2.Response
import retrofit2.http.*

interface ChatService {

    @GET("chats")
    suspend fun getChats(
        @Header("Authorization") token: String
    ): Response<List<Chat>>

    @GET("chats/{chatId}")
    suspend fun getChatById(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: String
    ): Response<Chat>

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

    @GET("chats/{chatId}/messages")
    suspend fun getMessages(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: String
    ): Response<List<Message>>

    @POST("chats/{chatId}/messages")
    suspend fun sendMessage(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: String,
        @Body message: Message
    ): Response<Message>

    @GET("chats/{chatId}/members")
    suspend fun getChatMembers(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: String
    ): Response<List<Members>>

    @POST("chats/{chatId}/members")
    suspend fun addMember(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: String,
        @Body member: Members
    ): Response<Members>

    @DELETE("chats/{chatId}/members/{userId}")
    suspend fun removeMember(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: String,
        @Path("userId") userId: String
    ): Response<Unit>

    @PATCH("messages/{messageId}/read")
    suspend fun markMessageAsRead(
        @Header("Authorization") token: String,
        @Path("messageId") messageId: String
    ): Response<Unit>
}
