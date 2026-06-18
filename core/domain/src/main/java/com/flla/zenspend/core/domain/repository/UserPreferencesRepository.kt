package com.flla.zenspend.core.domain.repository

import com.flla.zenspend.core.model.ThemeMode
import com.flla.zenspend.core.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val preferences: Flow<UserPreferences>

    suspend fun setThemeMode(themeMode: ThemeMode)

    suspend fun setOnboardingCompleted(completed: Boolean)
}
