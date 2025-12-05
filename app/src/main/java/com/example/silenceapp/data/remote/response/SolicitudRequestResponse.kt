package com.example.silenceapp.data.remote.response

data class User(
    val id: String,
    val nombre: String = "",
    val imagen: String? = null,
    val email: String = "",
    val fechaNto: String = "",
    val sexo: String = "",
    val pais: String = "",
)

data class Community(
    val id: String,
    val nombre: String,
    val imagen: String,
    val lastMessage: String,
    val lastMessageDate: String,
)

data class ResponseFriendRequestResponse(
    val userEnvia: String,
    val userRecibe: String,
    val status: String,
    val _id: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)


data class Community_Member_RequestResponse(
    val userEnvia: String,
    val userRecibe: String,
    val status: String,
    val _id: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)

data class RequestUser(
    val _id: String,
    val nombre: String,
    val imagen: String?
)

data class FriendRequest(
    val _id: String,
    val userEnvia: RequestUser,
    val userRecibe: RequestUser,
    val status: String,
    val createdAt: String,
    val updatedAt: String
)

data class CommunityRequest(
    val err: Boolean,
    val msg: String,
    val data: List<Community>
)
