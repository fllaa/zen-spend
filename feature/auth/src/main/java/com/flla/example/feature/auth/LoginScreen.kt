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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.hilt.navigation.compose.hiltViewModel
import com.flla.example.core.designsystem.component.ExamplePrimaryButton
import com.flla.example.core.designsystem.component.ExampleTextField
import com.flla.example.core.designsystem.theme.LocalExampleSpacing
import com.flla.example.core.ui.collectUiState

@Composable
fun LoginRoute(
    onLoggedIn: () -> Unit,
    onRegisterClick: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectUiState()
    LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) onLoggedIn()
    }
    LoginScreen(
        state = state,
        onEmailChanged = viewModel::onEmailChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        onSubmit = viewModel::submit,
        onRegisterClick = onRegisterClick,
    )
}

@Composable
fun LoginScreen(
    state: LoginUiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onRegisterClick: () -> Unit,
) {
    val spacing = LocalExampleSpacing.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing.lg),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Welcome back",
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = "Sign in to sync your local workspace.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(spacing.lg))
        ExampleTextField(
            value = state.email,
            onValueChange = onEmailChanged,
            label = "Email",
            modifier = Modifier
                .fillMaxWidth()
                .testTag("login_email"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        )
        Spacer(Modifier.height(spacing.md))
        ExampleTextField(
            value = state.password,
            onValueChange = onPasswordChanged,
            label = "Password",
            modifier = Modifier
                .fillMaxWidth()
                .testTag("login_password"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
        )
        if (state.errorMessage != null) {
            Spacer(Modifier.height(spacing.sm))
            Text(text = state.errorMessage, color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(spacing.lg))
        ExamplePrimaryButton(
            text = "Log in",
            onClick = onSubmit,
            loading = state.isLoading,
            modifier = Modifier.testTag("login_submit"),
        )
        TextButton(
            onClick = onRegisterClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Create account")
        }
    }
}
