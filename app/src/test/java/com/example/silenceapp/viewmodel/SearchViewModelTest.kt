package com.example.silenceapp.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.silenceapp.data.datastore.AuthDataStore
import com.example.silenceapp.data.remote.client.ApiClient
import com.example.silenceapp.data.remote.response.Community
import com.example.silenceapp.data.remote.response.User
import com.example.silenceapp.data.remote.response.FriendRequest
import com.example.silenceapp.data.remote.response.RequestUser
import com.example.silenceapp.data.remote.response.CommunityRequest
import com.example.silenceapp.data.remote.response.FriendRequestResponse
import com.example.silenceapp.data.remote.response.Community_Member_RequestResponse
import com.example.silenceapp.data.repository.SearchRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class SearchViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: SearchViewModel
    private lateinit var mockApplication: Application
    private lateinit var mockStore: AuthDataStore
    private lateinit var mockRepository: SearchRepository

    private val testUsers = listOf(
        User(
            id = "user1",
            nombre = "John Doe",
            email = "john@example.com",
            imagen = "https://example.com/john.jpg",
            pais = "USA",
            sexo = "M",
            fechaNto = "1990-01-01"
        ),
        User(
            id = "user2",
            nombre = "Jane Smith",
            email = "jane@example.com",
            imagen = "https://example.com/jane.jpg",
            pais = "Canada",
            sexo = "F",
            fechaNto = "1992-05-15"
        )
    )

    private val testCommunities = listOf(
        Community(
            id = "comm1",
            nombre = "Tech Community",
            imagen = "https://example.com/tech.jpg",
            lastMessage = "Welcome to tech!",
            lastMessageDate = "2024-01-01"
        ),
        Community(
            id = "comm2",
            nombre = "Music Lovers",
            imagen = "https://example.com/music.jpg",
            lastMessage = "Share your music",
            lastMessageDate = "2024-01-02"
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        mockApplication = mockk(relaxed = true)
        mockStore = mockk(relaxed = true)
        mockRepository = mockk(relaxed = true)

        // Mock ApiClient
        mockkObject(ApiClient)
        every { ApiClient.searchService } returns mockk(relaxed = true)

        // Mock AuthDataStore constructor
        mockkConstructor(AuthDataStore::class)
        every { anyConstructed<AuthDataStore>().getToken() } returns mockk(relaxed = true)

        coEvery { mockRepository.getUsers() } returns testUsers
        coEvery { mockRepository.getCommunities() } returns testCommunities
        coEvery { mockRepository.getSentFriendsRequests() } returns emptyList()
        coEvery { mockRepository.getSentCommunitiesRequests() } returns mockk {
            every { data } returns emptyList()
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    private fun createViewModel(): SearchViewModel {
        // Create ViewModel and replace repository with mock
        val vm = SearchViewModel(mockApplication)
        
        // Use reflection to replace repository
        val repositoryField = SearchViewModel::class.java.getDeclaredField("repository")
        repositoryField.isAccessible = true
        repositoryField.set(vm, mockRepository)
        
        return vm
    }

    @Test
    fun `loadInitialData should load users and communities successfully`() = runTest {
        // Given
        viewModel = createViewModel()

        // When
        advanceUntilIdle()

        // Then
        viewModel.users.test {
            assertThat(awaitItem()).isEqualTo(testUsers)
        }

        viewModel.communities.test {
            assertThat(awaitItem()).isEqualTo(testCommunities)
        }

        viewModel.isLoading.test {
            assertThat(awaitItem()).isFalse()
        }

        coVerify { mockRepository.getUsers() }
        coVerify { mockRepository.getCommunities() }
    }

    @Test
    fun `loadInitialData should set error when repository fails`() = runTest {
        // Given
        val errorMessage = "Network error"
        coEvery { mockRepository.getUsers() } throws Exception(errorMessage)
        viewModel = createViewModel()

        // When
        advanceUntilIdle()

        // Then
        viewModel.error.test {
            assertThat(awaitItem()).isEqualTo(errorMessage)
        }

        viewModel.isLoading.test {
            assertThat(awaitItem()).isFalse()
        }
    }

    @Test
    fun `search should filter users when tab is 0`() = runTest {
        // Given
        val searchQuery = "John"
        val filteredUsers = listOf(testUsers[0])
        coEvery { mockRepository.searchUsers(searchQuery) } returns filteredUsers
        
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.search(searchQuery, tab = 0)
        advanceUntilIdle()

        // Then
        viewModel.users.test {
            assertThat(awaitItem()).isEqualTo(filteredUsers)
        }

        coVerify { mockRepository.searchUsers(searchQuery) }
    }

    @Test
    fun `search should filter communities when tab is 1`() = runTest {
        // Given
        val searchQuery = "Tech"
        val filteredCommunities = listOf(testCommunities[0])
        coEvery { mockRepository.searchCommunities(searchQuery) } returns filteredCommunities
        
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.search(searchQuery, tab = 1)
        advanceUntilIdle()

        // Then
        viewModel.communities.test {
            assertThat(awaitItem()).isEqualTo(filteredCommunities)
        }

        coVerify { mockRepository.searchCommunities(searchQuery) }
    }

    @Test
    fun `sendFriendRequest should update sentRequestsIds on success`() = runTest {
        // Given
        val userId = "user123"
        coEvery { mockRepository.sendFriendRequest(userId) } returns mockk(relaxed = true)
        
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.sendFriendRequest(userId)
        advanceUntilIdle()

        // Then
        viewModel.sentRequestsIds.test {
            assertThat(awaitItem()).contains(userId)
        }

        coVerify { mockRepository.sendFriendRequest(userId) }
    }

    @Test
    fun `sendFriendRequest should set error on failure`() = runTest {
        // Given
        val userId = "user123"
        val errorMessage = "Request failed"
        coEvery { mockRepository.sendFriendRequest(userId) } throws Exception(errorMessage)
        
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.sendFriendRequest(userId)
        advanceUntilIdle()

        // Then
        viewModel.error.test {
            val error = awaitItem()
            assertThat(error).contains("Error sending request")
            assertThat(error).contains(errorMessage)
        }
    }

    @Test
    fun `sendCommunityRequest should update sentRequestsCommunityIds on success`() = runTest {
        // Given
        val communityId = "comm123"
        coEvery { mockRepository.sendCommunityRequest(communityId) } returns mockk(relaxed = true)
        
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.sendCommunityRequest(communityId)
        advanceUntilIdle()

        // Then
        viewModel.sentRequestsCommunityIds.test {
            assertThat(awaitItem()).contains(communityId)
        }

        coVerify { mockRepository.sendCommunityRequest(communityId) }
    }

    @Test
    fun `clearData should reset all states`() = runTest {
        // Given
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.clearData()

        // Then
        viewModel.users.test {
            assertThat(awaitItem()).isEmpty()
        }

        viewModel.communities.test {
            assertThat(awaitItem()).isEmpty()
        }

        viewModel.sentRequestsIds.test {
            assertThat(awaitItem()).isEmpty()
        }

        viewModel.sentRequestsCommunityIds.test {
            assertThat(awaitItem()).isEmpty()
        }

        viewModel.error.test {
            assertThat(awaitItem()).isNull()
        }
    }

    @Test
    fun `loadSentRequests should load friend and community requests`() = runTest {
        // Given
        val friendRequest = FriendRequest(
            _id = "req1",
            userEnvia = RequestUser(_id = "currentUser", nombre = "Current User", imagen = null),
            userRecibe = RequestUser(_id = "user1", nombre = "User 1", imagen = null),
            status = "pending",
            createdAt = "2024-01-01",
            updatedAt = "2024-01-01"
        )
        val community = Community(
            id = "comm1",
            nombre = "Community 1",
            imagen = "image.jpg",
            lastMessage = null,
            lastMessageDate = null
        )
        val communityRequest = CommunityRequest(
            err = false,
            msg = "Success",
            data = listOf(community)
        )
        
        coEvery { mockRepository.getSentFriendsRequests() } returns listOf(friendRequest)
        coEvery { mockRepository.getSentCommunitiesRequests() } returns communityRequest

        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.loadSentRequests()
        advanceUntilIdle()

        // Then
        viewModel.sentRequestsIds.test {
            assertThat(awaitItem()).contains("user1")
        }

        viewModel.sentRequestsCommunityIds.test {
            assertThat(awaitItem()).contains("comm1")
        }
    }
}
