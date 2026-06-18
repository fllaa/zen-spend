package com.flla.example.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.flla.example.core.designsystem.component.ExamplePrimaryButton
import com.flla.example.core.designsystem.component.ExampleTopAppBar
import com.flla.example.core.designsystem.theme.LocalExampleSpacing
import com.flla.example.core.model.ThemeMode
import com.flla.example.core.ui.ScreenScaffold
import com.flla.example.core.ui.collectUiState

@Composable
fun SettingsRoute(viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectUiState()
    SettingsScreen(
        state = state,
        onThemeModeClick = viewModel::setThemeMode,
        onLogoutClick = viewModel::logout,
    )
}

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onThemeModeClick: (ThemeMode) -> Unit,
    onLogoutClick: () -> Unit,
) {
    val spacing = LocalExampleSpacing.current
    ScreenScaffold(
        topBar = { ExampleTopAppBar(title = "Settings") },
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(spacing.lg),
            verticalArrangement = Arrangement.spacedBy(spacing.lg),
        ) {
            Text(text = "Theme", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                ThemeMode.entries.forEach { mode ->
                    FilterChip(
                        selected = state.preferences.themeMode == mode,
                        onClick = { onThemeModeClick(mode) },
                        label = { Text(mode.name) },
                    )
                }
            }
            ExamplePrimaryButton(
                text = "Log out",
                onClick = onLogoutClick,
            )
        }
    }
}
