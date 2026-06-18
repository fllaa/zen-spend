package com.flla.example.feature.profile

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.profileScreen() {
    composable(ProfileRoutes.PROFILE) {
        ProfileRoute()
    }
}
