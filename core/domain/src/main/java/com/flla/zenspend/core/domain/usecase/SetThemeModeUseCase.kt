package com.flla.zenspend.core.domain.usecase

import com.flla.zenspend.core.domain.repository.UserPreferencesRepository
import com.flla.zenspend.core.model.ThemeMode
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
