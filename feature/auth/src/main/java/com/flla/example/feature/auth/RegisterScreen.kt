package com.flla.example.feature.auth

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
import com.flla.example.core.designsystem.component.ExamplePrimaryButton
import com.flla.example.core.designsystem.component.ExampleTextField
import com.flla.example.core.designsystem.theme.LocalExampleSpacing
import com.flla.example.core.ui.collectUiState

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
        onNameChanged = viewModel::onNameChanged,
        onEmailChanged = viewModel::onEmailChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        onSubmit = viewModel::submit,
        onLoginClick = onLoginClick,
    )
}

@Composable
fun RegisterScreen(
    state: RegisterUiState,
    onNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onLoginClick: () -> Unit,
) {
    val spacing = LocalExampleSpacing.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing.lg),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "Create account", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(spacing.lg))
        ExampleTextField(
            value = state.name,
            onValueChange = onNameChanged,
            label = "Name",
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(spacing.md))
        ExampleTextField(
            value = state.email,
            onValueChange = onEmailChanged,
            label = "Email",
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        )
        Spacer(Modifier.height(spacing.md))
        ExampleTextField(
            value = state.password,
            onValueChange = onPasswordChanged,
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
        ExamplePrimaryButton(
            text = "Register",
            onClick = onSubmit,
            loading = state.isLoading,
        )
        TextButton(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("I already have an account")
        }
    }
}
