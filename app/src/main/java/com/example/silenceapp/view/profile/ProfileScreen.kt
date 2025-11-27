package com.example.silenceapp.view.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.silenceapp.ui.components.ProfileActionsBar
import com.example.silenceapp.ui.components.ProfileHeader
import com.example.silenceapp.ui.components.ProfilePostCard
import com.example.silenceapp.ui.theme.onBackgroundColor
import com.example.silenceapp.viewmodel.ProfileViewModel
import com.example.silenceapp.viewmodel.RelationshipStatus

@Composable
fun ProfileScreen(
    navController: NavController,
    userId: String,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val state = profileViewModel.uiState

    LaunchedEffect(userId) {
        profileViewModel.loadProfile(userId)
        profileViewModel.loadUserPosts(userId)
    }

    when {
        state.isLoadingProfile && state.profile == null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        state.errorMessage != null && state.profile == null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.errorMessage ?: "Error", color = onBackgroundColor)
            }
        }
        else -> {
            val profile = state.profile
            if (profile == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No se pudo cargar el perfil", color = onBackgroundColor)
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
                        onSecondaryAction = { /* TODO: Navegar a chat */ },
                        onReport = { profileViewModel.reportUser() },
                        onEdit = { navController.navigate("edit-profile") },
                        onShare = { profileViewModel.shareProfile() }
                    )
                }

                item {
                    Text(
                        text = "Publicaciones",
                        style = MaterialTheme.typography.titleMedium,
                        color = onBackgroundColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    )
                }

                when {
                    state.isLoadingPosts -> {
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

                    state.posts.isEmpty() -> {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "Aún no hay publicaciones", color = onBackgroundColor)
                            }
                        }
                    }

                    else -> {
                        items(state.posts) { post ->
                            ProfilePostCard(post = post)
                        }
                    }
                }

                if (state.errorMessage != null && state.posts.isNotEmpty()) {
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