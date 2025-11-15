package com.example.silenceapp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test
import androidx.compose.material3.Scaffold
import com.example.silenceapp.ui.components.BottomNavigationBar
import com.example.silenceapp.ui.components.NavigationGraph


class NavigationGraphTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun navigationGraph_cambiaPantallasCorrectamente() {
        composeRule.setContent {
            val navController = rememberNavController()

            Scaffold(
                bottomBar = { BottomNavigationBar(navController) }
            ) { padding ->
                NavigationGraph(
                    navController = navController,
                    innerPadding = padding
                )
            }
        }

        // HOME
        composeRule.onNodeWithTag("btn-home", useUnmergedTree = true).performClick()
        composeRule.onNodeWithTag("screen-home").assertExists()

        // SEARCH
        composeRule.onNodeWithTag("btn-search", useUnmergedTree = true).performClick()
        composeRule.onNodeWithTag("screen-search").assertExists()

        // PROFILE
        composeRule.onNodeWithTag("btn-profile", useUnmergedTree = true).performClick()
        composeRule.onNodeWithTag("screen-profile").assertExists()
    }

}
