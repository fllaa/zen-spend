package com.flla.example.core.domain.repository

import com.flla.example.core.model.ThemeMode
import com.flla.example.core.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val preferences: Flow<UserPreferences>

    suspend fun setThemeMode(themeMode: ThemeMode)

    suspend fun setOnboardingCompleted(completed: Boolean)
}
