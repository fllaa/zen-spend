package com.flla.zenspend.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.flla.zenspend.core.model.ThemeMode
import com.flla.zenspend.core.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesDataSource
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) {
        val preferences: Flow<UserPreferences> =
            dataStore.data.map { preferences ->
                UserPreferences(
                    themeMode = preferences[THEME_MODE]?.let(ThemeMode::valueOf) ?: ThemeMode.System,
                    hasCompletedOnboarding = preferences[ONBOARDING_COMPLETED] ?: false,
                    hasCompletedSetup = preferences[SETUP_COMPLETED] ?: false,
                    currency = preferences[CURRENCY] ?: "IDR",
                )
            }

        suspend fun setThemeMode(themeMode: ThemeMode) {
            dataStore.edit { preferences ->
                preferences[THEME_MODE] = themeMode.name
            }
        }

        suspend fun setOnboardingCompleted(completed: Boolean) {
            dataStore.edit { preferences ->
                preferences[ONBOARDING_COMPLETED] = completed
            }
        }

        suspend fun setSetupCompleted(completed: Boolean) {
            dataStore.edit { preferences ->
                preferences[SETUP_COMPLETED] = completed
            }
        }

        suspend fun setCurrency(currency: String) {
            dataStore.edit { preferences ->
                preferences[CURRENCY] = currency
            }
        }

        private companion object {
            val THEME_MODE = stringPreferencesKey("theme_mode")
            val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
            val SETUP_COMPLETED = booleanPreferencesKey("setup_completed")
            val CURRENCY = stringPreferencesKey("currency")
        }
    }
