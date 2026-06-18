package com.flla.example.core.domain.usecase

import com.flla.example.core.domain.repository.UserPreferencesRepository
import com.flla.example.core.model.ThemeMode
import javax.inject.Inject

class SetThemeModeUseCase
    @Inject
    constructor(
        private val repository: UserPreferencesRepository,
    ) {
        suspend operator fun invoke(themeMode: ThemeMode) {
            repository.setThemeMode(themeMode)
        }
    }
