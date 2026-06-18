package com.flla.zenspend.core.domain.usecase

import com.flla.zenspend.core.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class ObserveUserPreferencesUseCase
    @Inject
    constructor(
        repository: UserPreferencesRepository,
    ) {
        val preferences = repository.preferences
    }
