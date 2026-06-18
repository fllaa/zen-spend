package com.flla.example.core.domain.usecase

import com.flla.example.core.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class ObserveUserPreferencesUseCase
    @Inject
    constructor(
        repository: UserPreferencesRepository,
    ) {
        val preferences = repository.preferences
    }
