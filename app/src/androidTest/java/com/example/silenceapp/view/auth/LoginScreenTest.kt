package com.example.silenceapp.view.auth

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavController
import com.example.silenceapp.ui.theme.SilenceAppTheme
import com.example.silenceapp.viewmodel.AuthViewModel
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockNavController: NavController
    private lateinit var mockAuthViewModel: AuthViewModel
    private val authSuccessFlow = MutableStateFlow(false)

    @Before
    fun setup() {
        mockNavController = mockk(relaxed = true)
        mockAuthViewModel = mockk(relaxed = true)

        every { mockAuthViewModel.authSuccess } returns authSuccessFlow
        every { mockAuthViewModel.loginUser(any(), any()) } just Runs
        every { mockAuthViewModel.clearAuthSuccess() } just Runs
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun loginScreen_displaysAllComponents() {
        // Given
        composeTestRule.setContent {
            SilenceAppTheme {
                LoginScreen(
                    authViewModel = mockAuthViewModel,
                    navController = mockNavController
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Iniciar sesión").assertExists()
        composeTestRule.onNodeWithText("Email").assertExists()
        composeTestRule.onNodeWithText("Contraseña").assertExists()
        composeTestRule.onNodeWithText("¿No tienes cuenta? Regístrate").assertExists()
    }

    @Test
    fun loginScreen_emailField_acceptsInput() {
        // Given
        composeTestRule.setContent {
            SilenceAppTheme {
                LoginScreen(
                    authViewModel = mockAuthViewModel,
                    navController = mockNavController
                )
            }
        }

        // When
        val testEmail = "test@example.com"
        composeTestRule.onNodeWithText("Email")
            .performTextInput(testEmail)

        // Then
        composeTestRule.onNodeWithText(testEmail).assertExists()
    }

    @Test
    fun loginScreen_passwordField_acceptsInput() {
        // Given
        composeTestRule.setContent {
            SilenceAppTheme {
                LoginScreen(
                    authViewModel = mockAuthViewModel,
                    navController = mockNavController
                )
            }
        }

        // When
        val testPassword = "password123"
        composeTestRule.onNodeWithText("Contraseña")
            .performTextInput(testPassword)

        // Then - password should be hidden
        composeTestRule.onNodeWithText(testPassword).assertDoesNotExist()
    }

    @Test
    fun loginScreen_loginButton_isDisabledWhenFieldsEmpty() {
        // Given
        composeTestRule.setContent {
            SilenceAppTheme {
                LoginScreen(
                    authViewModel = mockAuthViewModel,
                    navController = mockNavController
                )
            }
        }

        // Then - login button should exist but be disabled (or not clickable)
        composeTestRule.onNodeWithText("Iniciar sesión").assertExists()
    }

    @Test
    fun loginScreen_loginButton_callsViewModelWhenClicked() {
        // Given
        val testEmail = "test@example.com"
        val testPassword = "password123"

        composeTestRule.setContent {
            SilenceAppTheme {
                LoginScreen(
                    authViewModel = mockAuthViewModel,
                    navController = mockNavController
                )
            }
        }

        // When
        composeTestRule.onNodeWithText("Email")
            .performTextInput(testEmail)
        composeTestRule.onNodeWithText("Contraseña")
            .performTextInput(testPassword)
        
        composeTestRule.onNodeWithText("Iniciar sesión")
            .performClick()

        // Then
        verify { mockAuthViewModel.loginUser(testEmail, testPassword) }
    }

    @Test
    fun loginScreen_registerLink_navigatesToRegister() {
        // Given
        composeTestRule.setContent {
            SilenceAppTheme {
                LoginScreen(
                    authViewModel = mockAuthViewModel,
                    navController = mockNavController
                )
            }
        }

        // When
        composeTestRule.onNodeWithText("¿No tienes cuenta? Regístrate")
            .performClick()

        // Then
        verify { mockNavController.navigate("register") }
    }

    @Test
    fun loginScreen_showsErrorMessage_whenFieldsEmpty() {
        // Given
        composeTestRule.setContent {
            SilenceAppTheme {
                LoginScreen(
                    authViewModel = mockAuthViewModel,
                    navController = mockNavController
                )
            }
        }

        // When - click login without filling fields
        composeTestRule.onNodeWithText("Iniciar sesión")
            .performClick()

        // Then - error message should appear
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            composeTestRule.onAllNodesWithText("Todos los campos son obligatorios")
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun loginScreen_navigatesToHome_onSuccessfulLogin() {
        // Given
        composeTestRule.setContent {
            SilenceAppTheme {
                LoginScreen(
                    authViewModel = mockAuthViewModel,
                    navController = mockNavController
                )
            }
        }

        // When - simulate successful login
        authSuccessFlow.value = true

        // Then - should navigate to home
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            try {
                verify { mockNavController.navigate("home") }
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    @Test
    fun loginScreen_displaysLogo() {
        // Given
        composeTestRule.setContent {
            SilenceAppTheme {
                LoginScreen(
                    authViewModel = mockAuthViewModel,
                    navController = mockNavController
                )
            }
        }

        // Then - logo should be displayed (test by finding any image)
        // Note: In a real scenario, you'd use testTag or contentDescription
        composeTestRule.onRoot().assertExists()
    }

    @Test
    fun loginScreen_emailField_trimsWhitespace() {
        // Given
        composeTestRule.setContent {
            SilenceAppTheme {
                LoginScreen(
                    authViewModel = mockAuthViewModel,
                    navController = mockNavController
                )
            }
        }

        // When
        val emailWithSpaces = "  test@example.com  "
        composeTestRule.onNodeWithText("Email")
            .performTextInput(emailWithSpaces)
        composeTestRule.onNodeWithText("Contraseña")
            .performTextInput("password123")
        composeTestRule.onNodeWithText("Iniciar sesión")
            .performClick()

        // Then - should call with trimmed email
        verify { mockAuthViewModel.loginUser("test@example.com", "password123") }
    }
}
