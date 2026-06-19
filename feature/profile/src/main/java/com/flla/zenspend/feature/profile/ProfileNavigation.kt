package com.flla.zenspend.feature.profile

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

@Suppress("LongParameterList")
fun NavGraphBuilder.profileScreen(
    onEditProfileClick: () -> Unit,
    onAccountsClick: () -> Unit,
    onCategoriesClick: () -> Unit,
    onBudgetClick: () -> Unit,
    onExportDataClick: () -> Unit,
    onBackupSyncClick: () -> Unit,
    onHelpCenterClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    composable(ProfileRoutes.PROFILE) {
        ProfileRoute(
            onEditProfileClick = onEditProfileClick,
            onAccountsClick = onAccountsClick,
            onCategoriesClick = onCategoriesClick,
            onBudgetClick = onBudgetClick,
            onExportDataClick = onExportDataClick,
            onBackupSyncClick = onBackupSyncClick,
            onHelpCenterClick = onHelpCenterClick,
            onPrivacyPolicyClick = onPrivacyPolicyClick,
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
    composable(ProfileRoutes.BUDGET) {
        BudgetRoute(onBackClick = onBackClick)
    }
    composable(ProfileRoutes.EXPORT_DATA) {
        ExportDataScreen(onBackClick = onBackClick)
    }
    composable(ProfileRoutes.BACKUP_SYNC) {
        BackupSyncScreen(onBackClick = onBackClick)
    }
    composable(ProfileRoutes.HELP_CENTER) {
        HelpCenterScreen(onBackClick = onBackClick)
    }
    composable(ProfileRoutes.PRIVACY_POLICY) {
        PrivacyPolicyScreen(onBackClick = onBackClick)
    }
}
