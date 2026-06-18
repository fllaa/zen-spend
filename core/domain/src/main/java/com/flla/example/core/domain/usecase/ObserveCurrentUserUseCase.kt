package com.flla.example.core.domain.usecase

import com.flla.example.core.domain.repository.UserRepository
import javax.inject.Inject

class ObserveCurrentUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    operator fun invoke() = userRepository.observeCurrentUser()
}
