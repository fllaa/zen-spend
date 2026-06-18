package com.flla.zenspend.core.domain.usecase

import com.flla.zenspend.core.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class SetSetupCompletedUseCase
    @Inject
    constructor(
        private val repository: UserPreferencesRepository,
    ) {
        suspend operator fun invoke(completed: Boolean) {
            repository.setSetupCompleted(completed)
        }
    }
