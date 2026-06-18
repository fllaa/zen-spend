package com.flla.example.core.domain.usecase

import com.flla.example.core.common.AppError
import com.flla.example.core.common.AppResult
import com.flla.example.core.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String): AppResult<Unit> {
        if (email.isBlank() || password.isBlank()) {
            return AppResult.Failure(AppError.Validation("Email and password are required."))
        }
        return authRepository.login(email.trim(), password)
    }
}
