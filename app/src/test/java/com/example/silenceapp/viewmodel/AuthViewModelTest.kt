package com.example.silenceapp.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.silenceapp.data.datastore.AuthDataStore
import com.example.silenceapp.data.local.AppDatabase
import com.example.silenceapp.data.local.DatabaseProvider
import com.example.silenceapp.data.remote.client.ApiClient
import com.example.silenceapp.data.remote.response.AuthResponse
import com.example.silenceapp.data.remote.response.ProfileResponse
import com.example.silenceapp.data.remote.response.User
import com.example.silenceapp.data.repository.AuthRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
class AuthViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: AuthViewModel
    private lateinit var mockApplication: Application
    private lateinit var mockRepository: AuthRepository
    private lateinit var mockStore: AuthDataStore
    private lateinit var mockDatabase: AppDatabase

    private val testToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6InVzZXIxMjMiLCJuYW1lIjoiSm9obiBEb2UifQ.test"
    private val testUserId = "user123"

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockApplication = mockk(relaxed = true)
        mockRepository = mockk(relaxed = true)
        mockStore = mockk(relaxed = true)
        mockDatabase = mockk(relaxed = true)

        // Mock static objects
        mockkObject(ApiClient)
        mockkObject(DatabaseProvider)
        
        every { ApiClient.authService } returns mockk(relaxed = true)
        every { DatabaseProvider.getDatabase(any()) } returns mockDatabase

        // Mock constructors
        mockkConstructor(AuthDataStore::class)
        mockkConstructor(AuthRepository::class)
        
        every { anyConstructed<AuthDataStore>().getToken() } returns flowOf(testToken)
        every { anyConstructed<AuthDataStore>().getUserId() } returns flowOf(testUserId)
        coEvery { anyConstructed<AuthDataStore>().saveToken(any()) } just Runs
        coEvery { anyConstructed<AuthDataStore>().saveUserId(any()) } just Runs
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    private fun createViewModel(): AuthViewModel {
        val vm = AuthViewModel(mockApplication)
        
        // Replace repository with mock
        val repositoryField = AuthViewModel::class.java.getDeclaredField("repository")
        repositoryField.isAccessible = true
        repositoryField.set(vm, mockRepository)
        
        return vm
    }

    @Test
    fun `loginUser should set authSuccess to true on successful login`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        coEvery { mockRepository.loginUser(email, password) } returns true

        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.loginUser(email, password)
        advanceUntilIdle()

        // Then
        viewModel.authSuccess.test {
            assertThat(awaitItem()).isTrue()
        }

        coVerify { mockRepository.loginUser(email, password) }
    }

    @Test
    fun `loginUser should set authSuccess to false on failed login`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "wrongpassword"
        coEvery { mockRepository.loginUser(email, password) } throws Exception("Invalid credentials")

        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.loginUser(email, password)
        advanceUntilIdle()

        // Then
        viewModel.authSuccess.test {
            assertThat(awaitItem()).isFalse()
        }
    }

    @Test
    fun `registerUser should set authSuccess to true on successful registration`() = runTest {
        // Given
        val nombre = "John Doe"
        val email = "john@example.com"
        val password = "password123"
        val sexo = "M"
        val fechaNto = "1990-01-01"
        val pais = "USA"

        coEvery { 
            mockRepository.registerUser(nombre, email, password, sexo, fechaNto, pais) 
        } returns true

        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.registerUser(nombre, email, password, sexo, fechaNto, pais)
        advanceUntilIdle()

        // Then
        viewModel.authSuccess.test {
            assertThat(awaitItem()).isTrue()
        }

        coVerify { 
            mockRepository.registerUser(nombre, email, password, sexo, fechaNto, pais) 
        }
    }

    @Test
    fun `registerUser should set authSuccess to false on failed registration`() = runTest {
        // Given
        val nombre = "John Doe"
        val email = "existing@example.com"
        val password = "password123"
        val sexo = "M"
        val fechaNto = "1990-01-01"
        val pais = "USA"

        coEvery { 
            mockRepository.registerUser(nombre, email, password, sexo, fechaNto, pais) 
        } throws Exception("Email already exists")

        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.registerUser(nombre, email, password, sexo, fechaNto, pais)
        advanceUntilIdle()

        // Then
        viewModel.authSuccess.test {
            assertThat(awaitItem()).isFalse()
        }
    }

    @Test
    fun `loadToken should return stored token`() = runTest {
        // Given
        viewModel = createViewModel()
        advanceUntilIdle()

        var capturedToken: String? = null

        // When
        viewModel.loadToken { token ->
            capturedToken = token
        }
        advanceUntilIdle()

        // Then
        assertThat(capturedToken).isEqualTo(testToken)
    }

    @Test
    fun `loadUserId should return stored userId`() = runTest {
        // Given
        viewModel = createViewModel()
        advanceUntilIdle()

        var capturedUserId: String? = null

        // When
        viewModel.loadUserId { userId ->
            capturedUserId = userId
        }
        advanceUntilIdle()

        // Then
        assertThat(capturedUserId).isEqualTo(testUserId)
    }

    @Test
    fun `getProfile should return profile on success`() = runTest {
        // Given
        val expectedProfile = ProfileResponse(
            id = testUserId,
            nombre = "John Doe",
            email = "john@example.com",
            imagen = listOf("https://example.com/profile.jpg"),
            sexo = "M",
            fechaNto = "1990-01-01",
            pais = "USA",
            seguidores = 10,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z",
            v = 0
        )
        coEvery { mockRepository.getProfile() } returns expectedProfile

        viewModel = createViewModel()
        advanceUntilIdle()

        var capturedProfile: ProfileResponse? = null

        // When
        viewModel.getProfile { profile ->
            capturedProfile = profile
        }
        advanceUntilIdle()

        // Then
        assertThat(capturedProfile).isEqualTo(expectedProfile)
        coVerify { mockRepository.getProfile() }
    }

    @Test
    fun `getProfile should return null on error`() = runTest {
        // Given
        coEvery { mockRepository.getProfile() } throws Exception("Network error")

        viewModel = createViewModel()
        advanceUntilIdle()

        var capturedProfile: ProfileResponse? = ProfileResponse(
            id = "temp",
            nombre = "temp",
            email = "temp",
            imagen = emptyList(),
            sexo = "M",
            fechaNto = "",
            pais = "",
            seguidores = 0,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z",
            v = 0
        )

        // When
        viewModel.getProfile { profile ->
            capturedProfile = profile
        }
        advanceUntilIdle()

        // Then
        assertThat(capturedProfile).isNull()
    }

    @Test
    fun `logout should clear all data and set isAuthenticated to false`() = runTest {
        // Given
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.logout()
        advanceUntilIdle()

        // Then
        viewModel.isAuthenticated.test {
            assertThat(awaitItem()).isFalse()
        }
    }

    @Test
    fun `clearAuthSuccess should reset authSuccess to false`() = runTest {
        // Given
        viewModel = createViewModel()
        advanceUntilIdle()

        // When
        viewModel.clearAuthSuccess()

        // Then
        viewModel.authSuccess.test {
            assertThat(awaitItem()).isFalse()
        }
    }

    @Test
    fun `isAuthenticated should be null initially`() = runTest {
        // Given
        viewModel = createViewModel()
        advanceUntilIdle()

        // Then
        viewModel.isAuthenticated.test {
            assertThat(awaitItem()).isNull()
        }
    }
}
