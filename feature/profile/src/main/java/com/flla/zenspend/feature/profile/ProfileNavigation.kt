package com.flla.zenspend.feature.profile

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.profileScreen() {
    composable(ProfileRoutes.PROFILE) {
        ProfileRoute()
    }
}
