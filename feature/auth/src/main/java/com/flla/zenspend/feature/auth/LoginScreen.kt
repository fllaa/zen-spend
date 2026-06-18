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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.hilt.navigation.compose.hiltViewModel
import com.flla.zenspend.core.designsystem.component.ZenSpendPrimaryButton
import com.flla.zenspend.core.designsystem.component.ZenSpendTextField
import com.flla.zenspend.core.designsystem.theme.LocalZenSpendSpacing
import com.flla.zenspend.core.ui.collectUiState

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
        actions =
            LoginActions(
                onEmailChanged = viewModel::onEmailChanged,
                onPasswordChanged = viewModel::onPasswordChanged,
                onSubmit = viewModel::submit,
                onRegisterClick = onRegisterClick,
            ),
    )
}

data class LoginActions(
    val onEmailChanged: (String) -> Unit,
    val onPasswordChanged: (String) -> Unit,
    val onSubmit: () -> Unit,
    val onRegisterClick: () -> Unit,
)

@Composable
fun LoginScreen(
    state: LoginUiState,
    actions: LoginActions,
) {
    val spacing = LocalZenSpendSpacing.current
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(spacing.lg),
        verticalArrangement = Arrangement.Center,
    ) {
        LoginHeader()
        Spacer(Modifier.height(spacing.lg))
        LoginFields(
            state = state,
            actions = actions,
        )
        LoginErrorMessage(errorMessage = state.errorMessage)
        Spacer(Modifier.height(spacing.lg))
        LoginButtons(
            isLoading = state.isLoading,
            actions = actions,
        )
    }
}

@Composable
private fun LoginHeader() {
    Text(
        text = "Welcome back",
        style = MaterialTheme.typography.headlineMedium,
    )
    Text(
        text = "Sign in to sync your local workspace.",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun LoginFields(
    state: LoginUiState,
    actions: LoginActions,
) {
    val spacing = LocalZenSpendSpacing.current
    ZenSpendTextField(
        value = state.email,
        onValueChange = actions.onEmailChanged,
        label = "Email",
        modifier =
            Modifier
                .fillMaxWidth()
                .testTag("login_email"),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
    )
    Spacer(Modifier.height(spacing.md))
    ZenSpendTextField(
        value = state.password,
        onValueChange = actions.onPasswordChanged,
        label = "Password",
        modifier =
            Modifier
                .fillMaxWidth()
                .testTag("login_password"),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = PasswordVisualTransformation(),
    )
}

@Composable
private fun LoginErrorMessage(errorMessage: String?) {
    if (errorMessage != null) {
        val spacing = LocalZenSpendSpacing.current
        Spacer(Modifier.height(spacing.sm))
        Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
    }
}

@Composable
private fun LoginButtons(
    isLoading: Boolean,
    actions: LoginActions,
) {
    ZenSpendPrimaryButton(
        text = "Log in",
        onClick = actions.onSubmit,
        loading = isLoading,
        modifier = Modifier.testTag("login_submit"),
    )
    TextButton(
        onClick = actions.onRegisterClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("Create account")
    }
}
