package com.flla.zenspend.core.domain.usecase

import com.flla.zenspend.core.domain.repository.SessionRepository
import javax.inject.Inject

class ObserveSessionUseCase
    @Inject
    constructor(
        sessionRepository: SessionRepository,
    ) {
        val sessionState = sessionRepository.sessionState
    }
