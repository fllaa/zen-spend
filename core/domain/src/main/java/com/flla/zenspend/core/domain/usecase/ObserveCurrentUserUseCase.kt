package com.flla.zenspend.core.domain.usecase

import com.flla.zenspend.core.domain.repository.UserRepository
import javax.inject.Inject

class ObserveCurrentUserUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) {
        operator fun invoke() = userRepository.observeCurrentUser()
    }
