package com.flla.example.core.model

data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.System,
    val hasCompletedOnboarding: Boolean = false,
)
