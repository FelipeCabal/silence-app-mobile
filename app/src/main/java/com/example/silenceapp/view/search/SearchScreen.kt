package com.example.silenceapp.view.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.silenceapp.viewmodel.SearchViewModel
import com.example.silenceapp.ui.components.SearchBar
import com.example.silenceapp.ui.components.SearchTabs
import com.example.silenceapp.ui.components.CommunityCard
import com.example.silenceapp.ui.components.PersonCard
import com.example.silenceapp.data.remote.response.Community
import com.example.silenceapp.data.remote.response.User
import androidx.compose.material3.MaterialTheme
import androidx.navigation.NavController

@Composable
fun UserList(
    users: List<User>,
    onFollow: (User) -> Unit,
    navController: NavController
) {
    LazyColumn {
        items(users) { user ->
            PersonCard(
                user = user,
                onFollow = { onFollow(user) },
                onProfileClick = { userId ->
                    println("Navigating to profile from UserList: $userId")
                    try {
                        navController.navigate("profile/$userId") {
                            launchSingleTop = true
                        }
                    } catch (e: Exception) {
                        println("Navigation error: ${e.message}")
                        e.printStackTrace()
                    }
                }
            )
        }
    }
}

@Composable
fun CommunityList(
    communities: List<Community>,
    onFollow: (Community) -> Unit
) {
    LazyColumn {
        items(communities) { community ->
            CommunityCard(
                community = community,
                onFollow = { onFollow(community) }
            )
        }
    }
}


@Composable
fun SearchScreen(viewModel: SearchViewModel, navController: NavController) {

    val users by viewModel.users.collectAsState()
    val communities by viewModel.communities.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val sentRequestsIds by viewModel.sentRequestsIds.collectAsState()
    val sentCommunitiesRequestsIds by viewModel.sentRequestsCommunityIds.collectAsState()
    var query by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(selectedTab) {
        viewModel.loadInitialData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {

        SearchBar(
            query = query,
            onQueryChange = { query = it },
            onSearchClick = {
                viewModel.search(query, selectedTab)
            }
        )

        Spacer(Modifier.height(12.dp))

        SearchTabs(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )

        Spacer(Modifier.height(12.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error: $error", color = Color.Red)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (selectedTab == 0) {
                        items(users) { user ->
                            val alreadySent = sentRequestsIds.contains(user.id)
                            PersonCard(
                                user = user,
                                onFollow = {
                                    if (!alreadySent) {
                                        viewModel.sendFriendRequest(user.id)
                                    }
                                },
                                requestSent = alreadySent,
                                onProfileClick = { userId ->
                                    println("Navigating to profile from SearchScreen: $userId")
                                    try {
                                        navController.navigate("profile/$userId") {
                                            launchSingleTop = true
                                        }
                                    } catch (e: Exception) {
                                        println("Navigation error: ${e.message}")
                                        e.printStackTrace()
                                    }
                                }
                            )
                            Spacer(Modifier.height(12.dp))
                        }
                    } else {
                        items(communities) { community ->
                            val alreadySent = sentCommunitiesRequestsIds.contains(community.id)
                            CommunityCard(
                                community = community,
                                onFollow = {
                                    if (!alreadySent) {
                                        viewModel.sendCommunityRequest(community.id)
                                    }
                                },
                                requestSent = alreadySent
                            )
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}
