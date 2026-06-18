package com.flla.example.core.domain.usecase

import com.flla.example.core.domain.repository.UserRepository
import javax.inject.Inject

class RefreshCurrentUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke() = userRepository.refreshCurrentUser()
}
