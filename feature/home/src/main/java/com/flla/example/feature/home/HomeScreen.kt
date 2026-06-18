package com.flla.example.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.flla.example.core.designsystem.component.ExampleTopAppBar
import com.flla.example.core.designsystem.theme.LocalExampleSpacing
import com.flla.example.core.ui.collectUiState

@Composable
fun HomeRoute(
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectUiState()
    HomeScreen(
        state = state,
        onProfileClick = onProfileClick,
        onSettingsClick = onSettingsClick,
    )
}

@Composable
fun HomeScreen(
    state: HomeUiState,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    val spacing = LocalExampleSpacing.current
    Scaffold(
        topBar = { ExampleTopAppBar(title = "Home") },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(selected = true, onClick = {}, label = { Text("Home") }, icon = {})
                NavigationBarItem(selected = false, onClick = onProfileClick, label = { Text("Profile") }, icon = {})
                NavigationBarItem(selected = false, onClick = onSettingsClick, label = { Text("Settings") }, icon = {})
            }
        },
    ) { padding: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(spacing.lg),
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            Text(text = "Local-first dashboard", style = MaterialTheme.typography.headlineSmall)
            Text(
                text = state.user?.let { "Signed in as ${it.name}" } ?: "Using cached session",
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = "This screen observes Room. Network refreshes update the local cache without blocking reads.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
