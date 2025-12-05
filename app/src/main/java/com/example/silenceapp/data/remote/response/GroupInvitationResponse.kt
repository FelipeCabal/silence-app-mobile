package com.example.silenceapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class GroupInvitationResponse(
    @SerializedName("_id") val id: String,
    val user: InvitedUserInfo,  // Usuario invitado
    val group: GroupInfo,       // Grupo al que fue invitado
    val status: String,         // P = pending, A = accepted, R = rejected
    val createdAt: String,
    val updatedAt: String
)

data class GroupInfo(
    @SerializedName("_id") val id: String,
    val nombre: String,
    val imagen: String?  // Cambiado a String? porque viene "" o null
)

data class InvitedUserInfo(
    @SerializedName("_id") val id: String,
    val nombre: String,
    val imagen: String?  // Cambiado a String? porque viene null
)
