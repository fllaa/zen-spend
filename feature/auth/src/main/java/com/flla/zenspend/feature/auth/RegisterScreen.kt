package com.flla.zenspend.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.hilt.navigation.compose.hiltViewModel
import com.flla.zenspend.core.designsystem.component.ZenSpendPrimaryButton
import com.flla.zenspend.core.designsystem.component.ZenSpendTextField
import com.flla.zenspend.core.designsystem.theme.LocalZenSpendSpacing
import com.flla.zenspend.core.ui.collectUiState

@Composable
fun RegisterRoute(
    onRegistered: () -> Unit,
    onLoginClick: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectUiState()
    LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) onRegistered()
    }
    RegisterScreen(
        state = state,
        actions =
            RegisterActions(
                onNameChanged = viewModel::onNameChanged,
                onEmailChanged = viewModel::onEmailChanged,
                onPasswordChanged = viewModel::onPasswordChanged,
                onSubmit = viewModel::submit,
                onLoginClick = onLoginClick,
            ),
    )
}

data class RegisterActions(
    val onNameChanged: (String) -> Unit,
    val onEmailChanged: (String) -> Unit,
    val onPasswordChanged: (String) -> Unit,
    val onSubmit: () -> Unit,
    val onLoginClick: () -> Unit,
)

@Composable
fun RegisterScreen(
    state: RegisterUiState,
    actions: RegisterActions,
) {
    val spacing = LocalZenSpendSpacing.current
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(spacing.lg),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "Create account", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(spacing.lg))
        ZenSpendTextField(
            value = state.name,
            onValueChange = actions.onNameChanged,
            label = "Name",
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(spacing.md))
        ZenSpendTextField(
            value = state.email,
            onValueChange = actions.onEmailChanged,
            label = "Email",
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        )
        Spacer(Modifier.height(spacing.md))
        ZenSpendTextField(
            value = state.password,
            onValueChange = actions.onPasswordChanged,
            label = "Password",
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
        )
        if (state.errorMessage != null) {
            Spacer(Modifier.height(spacing.sm))
            Text(text = state.errorMessage, color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(spacing.lg))
        ZenSpendPrimaryButton(
            text = "Register",
            onClick = actions.onSubmit,
            loading = state.isLoading,
        )
        TextButton(
            onClick = actions.onLoginClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("I already have an account")
        }
    }
}
