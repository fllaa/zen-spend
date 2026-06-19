package com.flla.zenspend.feature.profile

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.profileScreen(
    onEditProfileClick: () -> Unit,
    onAccountsClick: () -> Unit,
    onCategoriesClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    composable(ProfileRoutes.PROFILE) {
        ProfileRoute(
            onEditProfileClick = onEditProfileClick,
            onAccountsClick = onAccountsClick,
            onCategoriesClick = onCategoriesClick,
        )
    }
    composable(ProfileRoutes.EDIT_PROFILE) {
        EditProfileRoute(onBackClick = onBackClick)
    }
    composable(ProfileRoutes.ACCOUNTS) {
        AccountsRoute(onBackClick = onBackClick)
    }
    composable(ProfileRoutes.CATEGORIES) {
        CategoriesRoute(onBackClick = onBackClick)
    }
}
