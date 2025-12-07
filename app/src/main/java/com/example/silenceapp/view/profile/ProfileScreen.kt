package com.example.silenceapp.view.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.silenceapp.R
import com.example.silenceapp.data.local.entity.Post
import com.example.silenceapp.ui.components.PostCard
import com.example.silenceapp.ui.components.ProfileActionsBar
import com.example.silenceapp.ui.components.ProfileHeader
import com.example.silenceapp.ui.theme.onBackgroundColor
import com.example.silenceapp.viewmodel.ProfileViewModel
import com.example.silenceapp.viewmodel.RelationshipStatus
import com.google.gson.Gson

private enum class PostFilter {
    POSTS,
    LIKES
}

@Composable
fun ProfileScreen(
    navController: NavController,
    userId: String,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val state = profileViewModel.uiState
    var selectedFilter by remember { mutableStateOf(PostFilter.POSTS) }

    LaunchedEffect(userId) {
        profileViewModel.loadProfile(userId)
    }

    // Cargar los liked posts cuando se cambia al filtro de likes
    LaunchedEffect(selectedFilter) {
        if (selectedFilter == PostFilter.LIKES && state.likedPosts.isEmpty()) {
            profileViewModel.loadLikedPosts(userId)
        }
    }

    when {
        state.isLoadingProfile && state.profile == null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        state.errorMessage != null && state.profile == null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = state.errorMessage
                        ?: stringResource(id = R.string.generic_error_message),
                    color = onBackgroundColor
                )
            }
        }

        else -> {
            val profile = state.profile
            if (profile == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(id = R.string.profile_load_failed),
                        color = onBackgroundColor
                    )
                }
                return
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                item {
                    ProfileHeader(profile = profile)
                }

                item {
                    Spacer(modifier = Modifier.height(28.dp))
                    ProfileActionsBar(
                        isOwnProfile = state.isOwnProfile,
                        relationshipStatus = state.relationshipStatus,
                        onPrimaryAction = {
                            when (state.relationshipStatus) {
                                RelationshipStatus.NONE -> profileViewModel.sendFriendRequest()
                                RelationshipStatus.PENDING -> profileViewModel.cancelFriendRequest()
                                RelationshipStatus.ACCEPTED -> profileViewModel.removeFriend()
                            }
                        },
                        onSecondaryAction = { navController.navigate("create-chat") },
                        onReport = { profileViewModel.reportUser() },
                        onEdit = { navController.navigate("edit-profile") },
                        onShare = { profileViewModel.shareProfile() }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        IconToggle(
                            icon = Icons.Filled.GridOn,
                            contentDescription = stringResource(id = R.string.user_posts_content_description),
                            isSelected = selectedFilter == PostFilter.POSTS,
                            onClick = { selectedFilter = PostFilter.POSTS }
                        )
                        Spacer(modifier = Modifier.size(24.dp))
                        IconToggle(
                            icon = Icons.Filled.FavoriteBorder,
                            contentDescription = stringResource(id = R.string.liked_posts_content_description),
                            isSelected = selectedFilter == PostFilter.LIKES,
                            onClick = { selectedFilter = PostFilter.LIKES }
                        )
                    }
                }

                val displayedPosts = when (selectedFilter) {
                    PostFilter.POSTS -> state.posts
                    PostFilter.LIKES -> state.likedPosts
                }

                val isLoading = when (selectedFilter) {
                    PostFilter.POSTS -> state.isLoadingPosts
                    PostFilter.LIKES -> state.isLoadingLikedPosts
                }
                
                // Convertir PostResponse a Post entity para usar PostCard
                val displayedPostsAsEntity = displayedPosts.map { postResponse ->
                    Post(
                        id = 0, // No importa para visualización
                        remoteId = postResponse.id,
                        userId = postResponse.owner?.id ?: "",
                        userName = if (postResponse.esAnonimo) "Anónimo" else (postResponse.owner?.nombre ?: "Usuario desconocido"),
                        userImageProfile = postResponse.owner?.imagen?.firstOrNull(),
                        description = postResponse.description,
                        images = postResponse.imagen?.filterNotNull()?.filter { it.isNotBlank() } ?: emptyList(),
                        cantLikes = postResponse.cantLikes,
                        cantComentarios = postResponse.cantComentarios,
                        esAnonimo = postResponse.esAnonimo,
                        hasLiked = false, // TODO: verificar si el usuario le dio like
                        createdAt = postResponse.createdAt.toLongOrNull() ?: System.currentTimeMillis()
                    )
                }

                when {
                    isLoading -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }

                    displayedPosts.isEmpty() -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (selectedFilter == PostFilter.POSTS) {
                                        stringResource(id = R.string.empty_posts_message)
                                    } else {
                                        stringResource(id = R.string.empty_liked_posts_message)
                                    },
                                    color = onBackgroundColor
                                )
                            }
                        }
                    }

                    else -> {
                        items(displayedPostsAsEntity) { post ->
                            PostCard(
                                post = post,
                                onClick = { postId ->
                                    // Navegar al detalle del post
                                    navController.navigate("post/$postId")
                                },
                                onLikeClick = { postId ->
                                    // TODO: Implementar toggle like en ProfileViewModel
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }

                if (state.errorMessage != null && (state.posts.isNotEmpty() || state.likedPosts.isNotEmpty())) {
                    item {
                        Text(
                            text = state.errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IconToggle(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
        )
    }
}