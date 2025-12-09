package com.example.silenceapp.data.remote.dto

data class ReportUserRequest(
    val userId: String,
    val reason: String? = null
)