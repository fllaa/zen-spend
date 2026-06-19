package com.flla.zenspend.feature.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.CloudSync
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Policy
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.TrackChanges
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.flla.zenspend.core.designsystem.component.CurrencySelectionBottomSheet
import com.flla.zenspend.core.designsystem.component.ZenSpendTopAppBar
import com.flla.zenspend.core.designsystem.theme.LocalZenSpendSpacing
import com.flla.zenspend.core.designsystem.theme.zenSpendShadowLevel1
import com.flla.zenspend.core.model.ThemeMode
import com.flla.zenspend.core.model.User
import com.flla.zenspend.core.ui.ScreenScaffold
import com.flla.zenspend.core.ui.collectUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileRoute(
    onEditProfileClick: () -> Unit,
    onAccountsClick: () -> Unit,
    onCategoriesClick: () -> Unit,
    onBudgetClick: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectUiState()
    ProfileScreen(
        state = state,
        onThemeModeClick = viewModel::setThemeMode,
        onLogoutClick = viewModel::logout,
        onToggleNotifications = viewModel::toggleNotifications,
        onEditProfileClick = onEditProfileClick,
        onAccountsClick = onAccountsClick,
        onCategoriesClick = onCategoriesClick,
        onBudgetClick = onBudgetClick,
        onCurrencySelected = viewModel::setCurrency,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongMethod", "LongParameterList")
@Composable
fun ProfileScreen(
    state: ProfileUiState,
    onThemeModeClick: (ThemeMode) -> Unit,
    onLogoutClick: () -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onEditProfileClick: () -> Unit,
    onAccountsClick: () -> Unit,
    onCategoriesClick: () -> Unit,
    onBudgetClick: () -> Unit,
    onCurrencySelected: (String) -> Unit,
) {
    val spacing = LocalZenSpendSpacing.current
    val darkTheme = isSystemInDarkTheme()
    val context = LocalContext.current

    var showThemeDialog by remember { mutableStateOf(false) }
    var showCurrencyBottomSheet by remember { mutableStateOf(false) }

    val currencySubtitle =
        when (state.currency) {
            "IDR" -> "IDR (Rp)"
            "USD" -> "USD ($)"
            "EUR" -> "EUR (€)"
            "SGD" -> "SGD (S$)"
            else -> "IDR (Rp)"
        }

    ScreenScaffold(
        topBar = {
            ZenSpendTopAppBar(
                title = "Profile & Settings",
            )
        },
    ) { _ ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = spacing.containerPadding, vertical = spacing.lg),
            verticalArrangement = Arrangement.spacedBy(spacing.lg),
        ) {
            // Profile Card
            ProfileCard(
                user = state.user,
                onEditClick = {
                    Toast.makeText(context, "Edit avatar coming soon!", Toast.LENGTH_SHORT).show()
                },
                onEditProfileClick = onEditProfileClick,
            )

            // Settings Group 1: Account & Finance
            SettingsGroup(title = "Account & Finance") {
                SettingsItem(
                    icon = Icons.Rounded.Payments,
                    title = "Mata Uang",
                    subtitle = currencySubtitle,
                    onClick = {
                        showCurrencyBottomSheet = true
                    },
                )
                SettingsDivider()
                SettingsItem(
                    icon = Icons.Rounded.AccountBalance,
                    title = "Daftar Rekening",
                    onClick = onAccountsClick,
                )
                SettingsDivider()
                SettingsItem(
                    icon = Icons.Rounded.Category,
                    title = "Kategori Transaksi",
                    onClick = onCategoriesClick,
                )
                SettingsDivider()
                SettingsItem(
                    icon = Icons.Rounded.TrackChanges,
                    title = "Atur Anggaran",
                    onClick = onBudgetClick,
                )
            }

            // Settings Group 2: Preferences
            SettingsGroup(title = "Preferences") {
                SettingsItem(
                    icon = Icons.Rounded.Fingerprint,
                    title = "Keamanan",
                    subtitle = "Face ID",
                    onClick = {
                        Toast.makeText(context, "Security configurations coming soon!", Toast.LENGTH_SHORT).show()
                    },
                )
                SettingsDivider()
                SettingsToggleItem(
                    icon = Icons.Rounded.Notifications,
                    title = "Notifikasi",
                    checked = state.notificationsEnabled,
                    onCheckedChange = onToggleNotifications,
                )
                SettingsDivider()
                SettingsItem(
                    icon = Icons.Rounded.LightMode,
                    title = "Tema",
                    subtitle =
                        when (state.themeMode) {
                            ThemeMode.System -> "Sistem Default"
                            ThemeMode.Light -> "Terang"
                            ThemeMode.Dark -> "Gelap"
                        },
                    onClick = { showThemeDialog = true },
                )
            }

            // Settings Group 3: Data & Support
            SettingsGroup(title = "Data & Support") {
                SettingsItem(
                    icon = Icons.Rounded.Share,
                    title = "Ekspor Data",
                    onClick = {
                        Toast.makeText(context, "Export data functionality coming soon!", Toast.LENGTH_SHORT).show()
                    },
                )
                SettingsDivider()
                SettingsItem(
                    icon = Icons.Rounded.CloudSync,
                    title = "Cadangan & Sinkronisasi",
                    onClick = {
                        Toast.makeText(context, "Sync and backup coming soon!", Toast.LENGTH_SHORT).show()
                    },
                )
                SettingsDivider()
                SettingsItem(
                    icon = Icons.AutoMirrored.Rounded.HelpOutline,
                    title = "Pusat Bantuan",
                    onClick = {
                        Toast.makeText(context, "Help center link coming soon!", Toast.LENGTH_SHORT).show()
                    },
                )
                SettingsDivider()
                SettingsItem(
                    icon = Icons.Rounded.Policy,
                    title = "Kebijakan Privasi",
                    onClick = {
                        Toast.makeText(context, "Privacy Policy page coming soon!", Toast.LENGTH_SHORT).show()
                    },
                )
            }

            // Footer Actions (Logout & Version)
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = spacing.md),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                Button(
                    onClick = onLogoutClick,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        ),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(modifier = Modifier.width(spacing.sm))
                    Text(
                        text = "Keluar",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    )
                }

                Text(
                    text = "v1.0.4",
                    style =
                        MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        ),
                )
            }
        }
    }

    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentTheme = state.themeMode,
            onDismiss = { showThemeDialog = false },
            onThemeSelected = { theme ->
                onThemeModeClick(theme)
                showThemeDialog = false
            },
        )
    }

    if (showCurrencyBottomSheet) {
        CurrencySelectionBottomSheet(
            selectedCurrency = state.currency,
            onDismissRequest = { showCurrencyBottomSheet = false },
            onCurrencySelected = { currency ->
                onCurrencySelected(currency)
                showCurrencyBottomSheet = false
            },
        )
    }
}

@Suppress("LongMethod")
@Composable
fun ProfileCard(
    user: User?,
    onEditClick: () -> Unit,
    onEditProfileClick: () -> Unit,
) {
    val spacing = LocalZenSpendSpacing.current
    val darkTheme = isSystemInDarkTheme()

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .zenSpendShadowLevel1(darkTheme),
        shape = RoundedCornerShape(24.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            Box(
                modifier = Modifier.size(96.dp),
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .border(4.dp, MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = (user?.name?.take(1) ?: "A").uppercase(),
                            style =
                                MaterialTheme.typography.displayMedium.copy(
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontWeight = FontWeight.Bold,
                                ),
                        )
                    }
                }

                IconButton(
                    onClick = onEditClick,
                    modifier =
                        Modifier
                            .align(Alignment.BottomEnd)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = "Edit Avatar",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = user?.name ?: "Avall",
                    style =
                        MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                )
                Text(
                    text = user?.email ?: "avall@email.com",
                    style =
                        MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                )
            }

            Button(
                onClick = onEditProfileClick,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    ),
            ) {
                Text(
                    text = "Edit Profile",
                    style =
                        MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium,
                        ),
                )
            }
        }
    }
}

@Composable
fun SettingsGroup(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    val spacing = LocalZenSpendSpacing.current
    val darkTheme = isSystemInDarkTheme()

    Column(
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        Text(
            text = title.uppercase(),
            style =
                MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp,
                ),
            modifier = Modifier.padding(horizontal = spacing.sm),
        )

        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .zenSpendShadowLevel1(darkTheme),
            shape = RoundedCornerShape(24.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                ),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                content()
            }
        }
    }
}

@Suppress("LongMethod")
@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
) {
    val spacing = LocalZenSpendSpacing.current
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(horizontal = spacing.md, vertical = spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
            }
            Text(
                text = title,
                style =
                    MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.xs),
        ) {
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style =
                        MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium,
                        ),
                )
            }
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val spacing = LocalZenSpendSpacing.current
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.md, vertical = spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp),
                )
            }
            Text(
                text = title,
                style =
                    MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors =
                SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
        )
    }
}

@Composable
fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = LocalZenSpendSpacing.current.md),
        color = MaterialTheme.colorScheme.surfaceVariant,
    )
}

@Composable
fun ThemeSelectionDialog(
    currentTheme: ThemeMode,
    onDismiss: () -> Unit,
    onThemeSelected: (ThemeMode) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Pilih Tema",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ThemeOptionRow(
                    title = "Sistem Default",
                    selected = currentTheme == ThemeMode.System,
                    onClick = { onThemeSelected(ThemeMode.System) },
                )
                ThemeOptionRow(
                    title = "Terang",
                    selected = currentTheme == ThemeMode.Light,
                    onClick = { onThemeSelected(ThemeMode.Light) },
                )
                ThemeOptionRow(
                    title = "Gelap",
                    selected = currentTheme == ThemeMode.Dark,
                    onClick = { onThemeSelected(ThemeMode.Dark) },
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        },
    )
}

@Composable
fun ThemeOptionRow(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            style =
                MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                ),
        )
        if (selected) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}
