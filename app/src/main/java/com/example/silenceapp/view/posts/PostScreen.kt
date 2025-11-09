package com.example.silenceapp.view.posts

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.silenceapp.ui.components.PostCard
import com.example.silenceapp.viewmodel.PostViewModel

@Composable
fun PostScreen(viewModel: PostViewModel = viewModel()){
    val postsState = viewModel.posts.observeAsState(emptyList())

    LazyColumn {
        items(postsState.value) { post ->
                PostCard(post = post)
            }
    }
}