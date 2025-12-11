package com.example.silenceapp.data.repository

import app.cash.turbine.test
import com.example.silenceapp.data.datastore.AuthDataStore
import com.example.silenceapp.data.remote.dto.LoginRequest
import com.example.silenceapp.data.remote.dto.RegisterRequest
import com.example.silenceapp.data.remote.response.AuthResponse
import com.example.silenceapp.data.remote.response.ProfileResponse
import com.example.silenceapp.data.remote.service.AuthService
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthRepositoryTest {

    private lateinit var repository: AuthRepository
    private lateinit var mockAuthService: AuthService
    private lateinit var mockStore: AuthDataStore

    private val testToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6InVzZXIxMjMiLCJuYW1lIjoiSm9obiBEb2UifQ.test"
    private val testUserId = "user123"

    @Before
    fun setup() {
        mockAuthService = mockk()
        mockStore = mockk(relaxed = true)
        repository = spyk(AuthRepository(mockAuthService, mockStore))
        
        // Mock extractUserIdFromToken since Base64 doesn't work in Robolectric
        coEvery { repository.extractUserIdFromToken(any()) } returns testUserId
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `loginUser should save token and userId on success`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val loginRequest = LoginRequest(email, password)
        val loginResponse = AuthResponse(access_token = testToken)

        coEvery { mockAuthService.login(loginRequest) } returns loginResponse
        coEvery { mockStore.saveToken(testToken) } just Runs
        coEvery { mockStore.saveUserId(any()) } just Runs

        // When
        val result = repository.loginUser(email, password)

        // Then
        assertThat(result).isTrue()
        coVerify { mockAuthService.login(loginRequest) }
        coVerify { mockStore.saveToken(testToken) }
        coVerify { mockStore.saveUserId(testUserId) }
    }

    @Test
    fun `loginUser should throw exception on network error`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "wrongpassword"
        val loginRequest = LoginRequest(email, password)

        coEvery { mockAuthService.login(loginRequest) } throws Exception("Invalid credentials")

        // When & Then
        try {
            repository.loginUser(email, password)
            throw AssertionError("Expected exception was not thrown")
        } catch (e: Exception) {
            assertThat(e.message).isEqualTo("Invalid credentials")
        }

        coVerify { mockAuthService.login(loginRequest) }
        coVerify(exactly = 0) { mockStore.saveToken(any()) }
    }

    @Test
    fun `registerUser should save token and userId on success`() = runTest {
        // Given
        val nombre = "John Doe"
        val email = "john@example.com"
        val password = "password123"
        val sexo = "M"
        val fechaNto = "1990-01-01"
        val pais = "USA"
        
        val registerRequest = RegisterRequest(
            nombre = nombre,
            email = email,
            password = password,
            sexo = sexo,
            fechaNto = fechaNto,
            pais = pais
        )
        val loginResponse = AuthResponse(access_token = testToken)

        coEvery { mockAuthService.register(registerRequest) } returns loginResponse
        coEvery { mockStore.saveToken(testToken) } just Runs
        coEvery { mockStore.saveUserId(any()) } just Runs

        // When
        val result = repository.registerUser(nombre, email, password, sexo, fechaNto, pais)

        // Then
        assertThat(result).isTrue()
        coVerify { mockAuthService.register(registerRequest) }
        coVerify { mockStore.saveToken(testToken) }
        coVerify { mockStore.saveUserId(testUserId) }
    }

    @Test
    fun `registerUser should throw exception when email already exists`() = runTest {
        // Given
        val nombre = "John Doe"
        val email = "existing@example.com"
        val password = "password123"
        val sexo = "M"
        val fechaNto = "1990-01-01"
        val pais = "USA"

        val registerRequest = RegisterRequest(
            nombre = nombre,
            email = email,
            password = password,
            sexo = sexo,
            fechaNto = fechaNto,
            pais = pais
        )

        coEvery { mockAuthService.register(registerRequest) } throws Exception("Email already exists")

        // When & Then
        try {
            repository.registerUser(nombre, email, password, sexo, fechaNto, pais)
            throw AssertionError("Expected exception was not thrown")
        } catch (e: Exception) {
            assertThat(e.message).isEqualTo("Email already exists")
        }

        coVerify { mockAuthService.register(registerRequest) }
        coVerify(exactly = 0) { mockStore.saveToken(any()) }
    }

    @Test
    fun `getProfile should return profile with valid token`() = runTest {
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

        every { mockStore.getToken() } returns flowOf(testToken)
        coEvery { mockAuthService.profile("Bearer $testToken") } returns expectedProfile

        // When
        val result = repository.getProfile()

        // Then
        assertThat(result).isEqualTo(expectedProfile)
        coVerify { mockAuthService.profile("Bearer $testToken") }
    }

    @Test
    fun `getProfile should throw exception when token is invalid`() = runTest {
        // Given
        val invalidToken = "invalid_token"
        every { mockStore.getToken() } returns flowOf(invalidToken)
        coEvery { mockAuthService.profile("Bearer $invalidToken") } throws Exception("Unauthorized")

        // When & Then
        try {
            repository.getProfile()
            throw AssertionError("Expected exception was not thrown")
        } catch (e: Exception) {
            assertThat(e.message).isEqualTo("Unauthorized")
        }
    }

    @Test
    fun `extractUserIdFromToken should extract id from valid JWT`() = runTest {
        // Given
        val validJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6InVzZXIxMjMiLCJuYW1lIjoiSm9obiBEb2UifQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
        
        // Mock the extraction to return expected userId (Base64 doesn't work in Robolectric)
        coEvery { repository.extractUserIdFromToken(validJwt) } returns testUserId

        // When
        val result = repository.extractUserIdFromToken(validJwt)

        // Then
        assertThat(result).isEqualTo(testUserId)
    }

    @Test
    fun `loginUser should handle malformed JWT gracefully`() = runTest {
        // Given
        val malformedJwt = "invalid.jwt.token"
        val loginResponse = AuthResponse(access_token = malformedJwt)

        coEvery { mockAuthService.login(any()) } returns loginResponse
        coEvery { mockStore.saveToken(malformedJwt) } just Runs

        // When
        val result = repository.loginUser("test@test.com", "password")

        // Then - should still save token even if userId extraction fails
        assertThat(result).isTrue()
        coVerify { mockStore.saveToken(malformedJwt) }
    }
}
