package com.flla.example.feature.profile

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.profileScreen(onBackClick: () -> Unit) {
    composable(ProfileRoutes.PROFILE) {
        ProfileRoute(onBackClick = onBackClick)
    }
}
