package com.example.silenceapp.view.search

import com.example.silenceapp.data.remote.response.User
import com.example.silenceapp.data.remote.response.Community

data class SearchUiState(
    val query: String = "",
    val currentTabIndex: Int = 0,

    val people: List<User> = emptyList(),
    val communities: List<Community> = emptyList(),

    val user: User? = null,
    val community: Community? = null,

    val isLoading: Boolean = false,
    val error: String? = null
)
