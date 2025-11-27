package com.example.silenceapp.view.posts

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.silenceapp.data.local.entity.Post
import com.example.silenceapp.ui.components.PostCard
import com.example.silenceapp.viewmodel.PostViewModel

@Composable
fun PostScreen(
    postViewModel: PostViewModel = viewModel(),
    key: String? = null
){
    var postsState by remember { mutableStateOf<List<Post>>(emptyList()) }

    // Recargar posts cada vez que cambia la clave (cada navegación)
    LaunchedEffect(key) {
        postViewModel.getPosts { posts ->
            postsState = posts
        }
    }

    LazyColumn {
        items(postsState){post ->
            PostCard(post = post)
        }
    }
}