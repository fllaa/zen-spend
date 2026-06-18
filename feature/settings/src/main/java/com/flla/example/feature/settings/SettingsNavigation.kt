package com.flla.example.feature.settings

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.settingsScreen(onBackClick: () -> Unit) {
    composable(SettingsRoutes.SETTINGS) {
        SettingsRoute(onBackClick = onBackClick)
    }
}
