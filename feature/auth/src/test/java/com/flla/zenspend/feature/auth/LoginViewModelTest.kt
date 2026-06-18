package com.flla.zenspend.feature.auth

import com.flla.zenspend.core.common.AppError
import com.flla.zenspend.core.common.AppResult
import com.flla.zenspend.core.domain.usecase.LoginUseCase
import com.flla.zenspend.core.testing.MainDispatcherRule
import com.flla.zenspend.core.testing.repository.FakeAuthRepository
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
