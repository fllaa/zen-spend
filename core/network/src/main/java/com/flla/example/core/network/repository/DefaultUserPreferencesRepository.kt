package com.flla.example.core.network.repository

import com.flla.example.core.datastore.UserPreferencesDataSource
import com.flla.example.core.domain.repository.UserPreferencesRepository
import com.flla.example.core.model.ThemeMode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultUserPreferencesRepository @Inject constructor(
    private val dataSource: UserPreferencesDataSource,
) : UserPreferencesRepository {
    override val preferences = dataSource.preferences

    override suspend fun setThemeMode(themeMode: ThemeMode) {
        dataSource.setThemeMode(themeMode)
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        dataSource.setOnboardingCompleted(completed)
    }
}
