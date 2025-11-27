package com.example.silenceapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
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


val postBackgroundColor: Color
    @Composable get() = if (isSystemInDarkTheme()) {
        DimGray
    } else {
        lightGray
    }
val backgroundInteraction: Color
    @Composable get() = if (isSystemInDarkTheme()){
        PaleMint.copy(0.6f)
    } else {
        secondaryColor
    }