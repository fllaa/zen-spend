package com.flla.zenspend.feature.settings

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.settingsScreen() {
    composable(SettingsRoutes.SETTINGS) {
        SettingsRoute()
    }
}
