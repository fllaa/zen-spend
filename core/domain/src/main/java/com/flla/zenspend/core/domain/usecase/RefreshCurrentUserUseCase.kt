package com.flla.zenspend.core.domain.usecase

import com.flla.zenspend.core.domain.repository.UserRepository
import javax.inject.Inject

class RefreshCurrentUserUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) {
        suspend operator fun invoke() = userRepository.refreshCurrentUser()
    }
