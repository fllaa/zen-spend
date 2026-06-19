package com.flla.zenspend.feature.profile

import com.flla.zenspend.core.domain.usecase.ObserveCurrentUserUseCase
import com.flla.zenspend.core.domain.usecase.UpdateProfileUseCase
import com.flla.zenspend.core.model.User
import com.flla.zenspend.core.testing.MainDispatcherRule
import com.flla.zenspend.core.testing.repository.FakeUserRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EditProfileViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userRepository = FakeUserRepository()
    private val observeCurrentUserUseCase = ObserveCurrentUserUseCase(userRepository)
    private val updateProfileUseCase = UpdateProfileUseCase(userRepository)

    private lateinit var viewModel: EditProfileViewModel

    @Before
    fun setUp() {
        val initialUser = User(
            id = "demo-user",
            name = "Budi Santoso",
            email = "budi.santoso@email.com",
            phone = "+62 812 3456 7890",
            avatarUrl = null
        )
        userRepository.setUser(initialUser)
        viewModel = EditProfileViewModel(observeCurrentUserUseCase, updateProfileUseCase)
    }

    @Test
    fun loadUserProfile_loadsInitialData() {
        val state = viewModel.uiState.value
        assertEquals("Budi Santoso", state.name)
        assertEquals("budi.santoso@email.com", state.email)
        assertEquals("+62 812 3456 7890", state.phone)
        assertFalse(state.isLoading)
    }

    @Test
    fun onNameChange_updatesState() {
        viewModel.onNameChange("Avall")
        assertEquals("Avall", viewModel.uiState.value.name)
    }

    @Test
    fun saveProfile_whenSucceeds_updatesCachedUserAndSuccessState() {
        viewModel.onNameChange("Avall")
        viewModel.onPhoneChange("+62899")
        viewModel.saveProfile()

        val state = viewModel.uiState.value
        assertTrue(state.isSaveSuccess)
        assertFalse(state.isLoading)

        // Check local cached value in userRepository
        assertEquals("Avall", userRepository.currentUser?.name)
        assertEquals("+62899", userRepository.currentUser?.phone)
    }
}
