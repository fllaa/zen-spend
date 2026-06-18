package com.flla.example.core.domain.usecase

import com.flla.example.core.common.AppError
import com.flla.example.core.common.AppResult
import com.flla.example.core.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) {
        suspend operator fun invoke(
            name: String,
            email: String,
            password: String,
        ): AppResult<Unit> {
            if (name.isBlank() || email.isBlank() || password.length < 8) {
                val message = "Use a name, email, and password with at least 8 characters."
                return AppResult.Failure(
                    AppError.Validation(message),
                )
            }
            return authRepository.register(name.trim(), email.trim(), password)
        }
    }
