package com.example.silenceapp.data.remote.models

import com.example.silenceapp.data.remote.response.Community

data class CommunityResponse(
    val err: Boolean,
    val msg: String,
    val total: Int,
    val page: Int,
    val limit: Int,
    val pages: Int,
    val results: List<Community>
)
