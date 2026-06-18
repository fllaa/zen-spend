package com.flla.example.core.domain.usecase

import com.flla.example.core.domain.repository.SessionRepository
import javax.inject.Inject

class ObserveSessionUseCase @Inject constructor(
    sessionRepository: SessionRepository,
) {
    val sessionState = sessionRepository.sessionState
}
