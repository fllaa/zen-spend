package com.flla.zenspend.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.flla.zenspend.core.model.AuthTokens
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthTokenDataSource
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
        private val tokenCipher: TokenCipher,
    ) {
        val tokens: Flow<AuthTokens?> =
            dataStore.data.map { preferences ->
                val access = preferences[ACCESS_TOKEN]?.let { runCatching { tokenCipher.decrypt(it) }.getOrNull() }
                val refresh = preferences[REFRESH_TOKEN]?.let { runCatching { tokenCipher.decrypt(it) }.getOrNull() }
                if (access.isNullOrBlank() || refresh.isNullOrBlank()) null else AuthTokens(access, refresh)
            }

        val isSessionExpired: Flow<Boolean> =
            dataStore.data.map { preferences ->
                preferences[SESSION_EXPIRED] ?: false
            }

        suspend fun saveTokens(tokens: AuthTokens) {
            dataStore.edit { preferences ->
                preferences[ACCESS_TOKEN] = tokenCipher.encrypt(tokens.accessToken)
                preferences[REFRESH_TOKEN] = tokenCipher.encrypt(tokens.refreshToken)
                preferences[SESSION_EXPIRED] = false
            }
        }

        suspend fun clearTokens() {
            dataStore.edit { preferences ->
                preferences.remove(ACCESS_TOKEN)
                preferences.remove(REFRESH_TOKEN)
            }
        }

        suspend fun markSessionExpired() {
            dataStore.edit { preferences ->
                preferences.remove(ACCESS_TOKEN)
                preferences.remove(REFRESH_TOKEN)
                preferences[SESSION_EXPIRED] = true
            }
        }

        private companion object {
            val ACCESS_TOKEN = stringPreferencesKey("access_token")
            val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
            val SESSION_EXPIRED = booleanPreferencesKey("session_expired")
        }
    }
