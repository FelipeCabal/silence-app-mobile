package com.example.silenceapp.view.posts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.silenceapp.ui.components.CommentInput
import com.example.silenceapp.ui.components.PostDetailContent
import com.example.silenceapp.viewmodel.CommentViewModel
import com.example.silenceapp.viewmodel.PostViewModel

@Composable
fun PostDetailScreen(
    postId: String,
    postViewModel: PostViewModel = viewModel(),
) {
    val state by postViewModel.postDetailState.collectAsState()

    var commentText by rememberSaveable { mutableStateOf("") }

    // Cargar detalle al entrar
    LaunchedEffect(postId) {
        postViewModel.loadPostDetail(postId)
    }

    when {
        state.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        state.error != null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Error: ${state.error}")
            }
        }

        state.post != null -> {
            Scaffold(
                bottomBar = {
                    CommentInput(
                        comment = commentText,
                        onCommentChange = {newText -> commentText = newText},
                        onSendClick = {
                            postViewModel.sendComment(
                                postRemoteId = postId,
                                text = commentText
                            )
                            commentText = ""
                        },
                        isSending = state.isSendingComment,
                        error = state.commentError
                    )
                }
            ) { inner ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(inner)
                        .verticalScroll(rememberScrollState())
                ) {
                    PostDetailContent(
                        post = state.post!!,
                        comments = state.comments,
                        onLikeClick = { remoteId ->
                            android.util.Log.d("PostDetailScreen", "🎯 onLikeClick triggered with remoteId: $remoteId")
                            postViewModel.toggleLike(remoteId)
                        }
                    )
                }
            }
        }

        else -> {
            // Estado inicial sin nada
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "No hay información del post")
            }
        }
    }
}

