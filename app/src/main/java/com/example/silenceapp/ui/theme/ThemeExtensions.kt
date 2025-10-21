package com.example.silenceapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val primaryColor: Color
    @Composable get() = MaterialTheme.colorScheme.primary

val secondaryColor: Color
    @Composable get() = MaterialTheme.colorScheme.secondary

val backgroundColor: Color
    @Composable get() = MaterialTheme.colorScheme.background

val onBackgroundColor: Color
    @Composable get() = MaterialTheme.colorScheme.onBackground

val surfaceColor: Color
    @Composable get() = MaterialTheme.colorScheme.surface

val errorColor: Color
    @Composable get() = MaterialTheme.colorScheme.error