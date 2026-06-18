package com.flla.zenspend.feature.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.flla.zenspend.core.designsystem.component.ZenSpendTopAppBar
import com.flla.zenspend.core.designsystem.theme.LocalZenSpendSpacing
import com.flla.zenspend.core.ui.ScreenScaffold
import com.flla.zenspend.core.ui.collectUiState

@Composable
fun ProfileRoute(viewModel: ProfileViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectUiState()
    ProfileScreen(state = state)
}

@Composable
fun ProfileScreen(state: ProfileUiState) {
    val spacing = LocalZenSpendSpacing.current
    ScreenScaffold(
        topBar = { ZenSpendTopAppBar(title = "Profile") },
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(spacing.lg),
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            Text(text = state.user?.name ?: "Unknown user", style = MaterialTheme.typography.headlineSmall)
            Text(text = state.user?.email ?: "No email cached", style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "Profile data is read from Room so the UI remains useful offline.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
