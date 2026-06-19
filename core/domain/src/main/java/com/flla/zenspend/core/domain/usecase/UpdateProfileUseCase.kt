package com.flla.zenspend.core.domain.usecase

import com.flla.zenspend.core.common.AppResult
import com.flla.zenspend.core.domain.repository.UserRepository
import com.flla.zenspend.core.model.User
import javax.inject.Inject

class UpdateProfileUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) {
        suspend operator fun invoke(
            name: String,
            email: String,
            phone: String?,
        ): AppResult<User> {
            return userRepository.updateProfile(name, email, phone)
        }
    }
