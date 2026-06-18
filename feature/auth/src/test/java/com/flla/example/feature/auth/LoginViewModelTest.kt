package com.flla.example.feature.auth

import com.flla.example.core.common.AppError
import com.flla.example.core.common.AppResult
import com.flla.example.core.domain.usecase.LoginUseCase
import com.flla.example.core.testing.MainDispatcherRule
import com.flla.example.core.testing.repository.FakeAuthRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val authRepository = FakeAuthRepository()
    private val viewModel = LoginViewModel(LoginUseCase(authRepository))

    @Test
    fun submit_whenLoginSucceeds_marksAuthenticated() {
        viewModel.submit()

        val state = viewModel.uiState.value
        assertTrue(state.isAuthenticated)
        assertFalse(state.isLoading)
    }

    @Test
    fun submit_whenValidationFails_showsMessage() {
        authRepository.loginResult = AppResult.Failure(AppError.Validation("Nope"))
        viewModel.submit()

        assertEquals("Nope", viewModel.uiState.value.errorMessage)
    }
}
