package com.flla.zenspend.feature.profile

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.profileScreen(
    onEditProfileClick: () -> Unit,
    onAccountsClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    composable(ProfileRoutes.PROFILE) {
        ProfileRoute(
            onEditProfileClick = onEditProfileClick,
            onAccountsClick = onAccountsClick,
        )
    }
    composable(ProfileRoutes.EDIT_PROFILE) {
        EditProfileRoute(onBackClick = onBackClick)
    }
    composable(ProfileRoutes.ACCOUNTS) {
        AccountsRoute(onBackClick = onBackClick)
    }
}
