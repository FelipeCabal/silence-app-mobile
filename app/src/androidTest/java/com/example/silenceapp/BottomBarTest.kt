package com.example.silenceapp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test
import com.example.silenceapp.ui.components.BottomNavigationBar

class BottomBarTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun bottomBar_muestraBotones() {
        composeRule.setContent {
            val navController = rememberNavController()
            BottomNavigationBar(navController = navController)
        }

        composeRule.onNodeWithText("home").assertExists()
        composeRule.onNodeWithText("search").assertExists()
        composeRule.onNodeWithText("profile").assertExists()
    }
}
