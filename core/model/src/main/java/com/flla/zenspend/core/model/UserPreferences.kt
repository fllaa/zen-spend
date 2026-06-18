package com.flla.zenspend.core.model

data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.System,
    val hasCompletedOnboarding: Boolean = false,
    val hasCompletedSetup: Boolean = false,
)
