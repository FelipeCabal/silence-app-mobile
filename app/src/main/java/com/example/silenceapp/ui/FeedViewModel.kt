package com.example.silenceapp.ui

import PostsRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import com.example.silenceapp.data.Post

class FeedViewModel(
    private val repo: PostsRepository = PostsRepository()
) : ViewModel() {
    val postsFlow: Flow<PagingData<Post>> = repo.postsStream().cachedIn(viewModelScope)
}
