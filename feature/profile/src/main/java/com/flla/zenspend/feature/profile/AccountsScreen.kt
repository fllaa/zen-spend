package com.flla.zenspend.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.flla.zenspend.core.designsystem.component.ZenSpendPrimaryButton
import com.flla.zenspend.core.designsystem.component.ZenSpendTopAppBar
import com.flla.zenspend.core.designsystem.theme.LocalZenSpendSpacing
import com.flla.zenspend.core.designsystem.theme.zenSpendShadowLevel1
import com.flla.zenspend.core.model.Account
import com.flla.zenspend.core.ui.collectUiState
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AccountsRoute(
    onBackClick: () -> Unit,
    viewModel: AccountsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectUiState()
    AccountsScreen(
        state = state,
        onBackClick = onBackClick,
        onToggleVisibility = viewModel::toggleVisibility,
        onSaveAccount = viewModel::saveAccount,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongMethod")
@Composable
fun AccountsScreen(
    state: AccountsUiState,
    onBackClick: () -> Unit,
    onToggleVisibility: (String) -> Unit,
    onSaveAccount: (String, String, String, Long, Boolean) -> Unit,
) {
    val spacing = LocalZenSpendSpacing.current
    var showAddAccountSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ZenSpendTopAppBar(
                title = "Daftar Rekening",
                onBackClick = onBackClick,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddAccountSheet = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Tambah Rekening",
                    )
                    Text(
                        text = "Tambah",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                    )
                }
            }
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = spacing.containerPadding, vertical = spacing.lg),
            verticalArrangement = Arrangement.spacedBy(spacing.lg),
        ) {
            state.accounts.forEach { account ->
                AccountCard(
                    account = account,
                    onToggleVisibility = { onToggleVisibility(account.id) },
                )
            }
            // Spacer to avoid overlay with FAB
            Spacer(modifier = Modifier.height(80.dp))
        }

        if (showAddAccountSheet) {
            AddAccountBottomSheet(
                onDismissRequest = { showAddAccountSheet = false },
                onSave = { name, type, provider, balance, isPrimary ->
                    onSaveAccount(name, type, provider, balance, isPrimary)
                    showAddAccountSheet = false
                },
            )
        }
    }
}

@Suppress("LongMethod")
@Composable
fun AccountCard(
    account: Account,
    onToggleVisibility: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val darkTheme = isSystemInDarkTheme()

    // Determine Gradient Brush based on iconType
    val gradientColors =
        when (account.iconType.uppercase()) {
            "BCA" -> listOf(Color(0xFF004D63), Color(0xFF006480))
            "MANDIRI" -> listOf(Color(0xFF003D5B), Color(0xFF005A82))
            "GOPAY" -> listOf(Color(0xFF006B1B), Color(0xFF1D862D))
            "TUNAI" -> listOf(Color(0xFF516161), Color(0xFF6F787D))
            else -> listOf(Color(0xFF006480), Color(0xFF2D7D9A))
        }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .aspectRatio(1.586f)
                .zenSpendShadowLevel1(darkTheme)
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.linearGradient(gradientColors))
                .padding(24.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // Header Row: Logo & Visibility Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Provider Logo/Icon
                AccountLogo(
                    iconType = account.iconType,
                    accountName = account.name,
                )

                // Visibility Toggle Button
                IconButton(
                    onClick = onToggleVisibility,
                    modifier = Modifier.size(40.dp),
                ) {
                    Icon(
                        imageVector = if (account.isVisible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                        contentDescription = "Toggle Balance Visibility",
                        tint = Color.White.copy(alpha = 0.6f),
                    )
                }
            }

            // Middle: Account Name & Primary Indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = account.name,
                    style =
                        MaterialTheme.typography.titleMedium.copy(
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium,
                        ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (account.isPrimary) {
                    Box(
                        modifier =
                            Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text = "Utama",
                            style =
                                MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                ),
                        )
                    }
                }
            }

            // Bottom: Balance
            Column {
                Text(
                    text = "Saldo Tersedia",
                    style =
                        MaterialTheme.typography.labelSmall.copy(
                            color = Color.White.copy(alpha = 0.6f),
                            letterSpacing = 0.5.sp,
                        ),
                )
                Spacer(modifier = Modifier.height(4.dp))
                val balanceText =
                    if (account.isVisible) {
                        formatRupiah(account.balance)
                    } else {
                        "Rp ••••••"
                    }
                Text(
                    text = balanceText,
                    style =
                        MaterialTheme.typography.headlineMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = (-0.5).sp,
                        ),
                )
            }
        }
    }
}

@Suppress("LongMethod")
@Composable
fun AccountLogo(
    iconType: String,
    accountName: String,
    modifier: Modifier = Modifier,
) {
    when (iconType.uppercase()) {
        "BCA" -> {
            Box(
                modifier =
                    modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .padding(4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "BCA",
                    color = Color(0xFF004D63),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }
        "MANDIRI" -> {
            Box(
                modifier =
                    modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .padding(4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "MNDR",
                    color = Color(0xFF003D5B),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }
        "GOPAY" -> {
            Box(
                modifier =
                    modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.AccountBalanceWallet,
                    contentDescription = "GoPay",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
        "TUNAI" -> {
            Box(
                modifier =
                    modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Payments,
                    contentDescription = "Tunai",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
        else -> {
            Box(
                modifier =
                    modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = accountName.take(2).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongMethod")
@Composable
fun AddAccountBottomSheet(
    onDismissRequest: () -> Unit,
    onSave: (name: String, type: String, provider: String, balance: Long, isPrimary: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalZenSpendSpacing.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedProvider by remember { mutableStateOf("BCA") }
    var accountName by remember { mutableStateOf("") }
    var initialBalanceStr by remember { mutableStateOf("") }
    var isPrimary by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        dragHandle = {
            Box(
                modifier =
                    Modifier
                        .padding(vertical = spacing.md)
                        .size(width = 48.dp, height = 6.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
            )
        },
        modifier = modifier,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.gutter)
                    .padding(bottom = spacing.xl)
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(spacing.lg),
        ) {
            // Header
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.align(Alignment.CenterStart),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
                Text(
                    text = "Tambah Akun Baru",
                    style =
                        MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            // Provider Selector
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "PILIH PENYEDIA",
                    style =
                        MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                        ),
                )

                // Banks
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "BANK",
                        style =
                            MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.outline,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                            ),
                    )
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        ProviderPill(
                            name = "BCA",
                            selected = selectedProvider == "BCA",
                            onClick = { selectedProvider = "BCA" },
                        )
                        ProviderPill(
                            name = "Mandiri",
                            selected = selectedProvider == "Mandiri",
                            onClick = { selectedProvider = "Mandiri" },
                        )
                        ProviderPill(
                            name = "SeaBank",
                            selected = selectedProvider == "SeaBank",
                            onClick = { selectedProvider = "SeaBank" },
                        )
                    }
                }

                // E-Wallets
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "E-WALLET",
                        style =
                            MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.outline,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                            ),
                    )
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        ProviderPill(
                            name = "DANA",
                            selected = selectedProvider == "DANA",
                            onClick = { selectedProvider = "DANA" },
                        )
                        ProviderPill(
                            name = "OVO",
                            selected = selectedProvider == "OVO",
                            onClick = { selectedProvider = "OVO" },
                        )
                        ProviderPill(
                            name = "GoPay",
                            selected = selectedProvider == "GoPay",
                            onClick = { selectedProvider = "GoPay" },
                        )
                    }
                }

                // Lainnya
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "LAINNYA",
                        style =
                            MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.outline,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                            ),
                    )
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        ProviderPill(
                            name = "Tunai",
                            selected = selectedProvider == "Tunai",
                            onClick = { selectedProvider = "Tunai" },
                        )
                    }
                }
            }

            // Input: Nama Rekening
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "NAMA REKENING",
                    style =
                        MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                        ),
                )
                TextField(
                    value = accountName,
                    onValueChange = { accountName = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Contoh: Tabungan Utama",
                            color = MaterialTheme.colorScheme.outline,
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors =
                        TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                        ),
                )
            }

            // Input: Saldo Awal
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "SALDO AWAL",
                    style =
                        MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                        ),
                )
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceContainerLow,
                                RoundedCornerShape(16.dp),
                            )
                            .padding(spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "JUMLAH SALDO",
                        style =
                            MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                            ),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = "Rp ",
                            style =
                                MaterialTheme.typography.headlineLarge.copy(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                    fontWeight = FontWeight.SemiBold,
                                ),
                        )
                        // A clean basic input field
                        BasicTextField(
                            value = initialBalanceStr,
                            onValueChange = { newVal ->
                                if (newVal.all { it.isDigit() }) {
                                    initialBalanceStr = newVal
                                }
                            },
                            textStyle =
                                MaterialTheme.typography.headlineLarge.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Start,
                                ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.width(IntrinsicSize.Min),
                        )
                        if (initialBalanceStr.isEmpty()) {
                            Text(
                                text = "0",
                                style =
                                    MaterialTheme.typography.headlineLarge.copy(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                        fontWeight = FontWeight.SemiBold,
                                    ),
                            )
                        }
                    }
                }
            }

            // Toggle: Akun Utama
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceContainerLowest,
                            RoundedCornerShape(16.dp),
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { isPrimary = !isPrimary }
                        .padding(spacing.md),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    Column {
                        Text(
                            text = "Jadikan Rekening Utama",
                            style =
                                MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                ),
                        )
                        Text(
                            text = "Default untuk transaksi baru",
                            style =
                                MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 0.5.sp,
                                ),
                        )
                    }
                }

                Switch(
                    checked = isPrimary,
                    onCheckedChange = { isPrimary = it },
                    colors =
                        SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                        ),
                )
            }

            // Footer Action Button
            ZenSpendPrimaryButton(
                text = "Simpan Akun",
                onClick = {
                    if (accountName.isNotBlank()) {
                        val balance = initialBalanceStr.toLongOrNull() ?: 0L
                        // For the repository 'type', we combine provider & type (e.g. BCA Personal / GoPay Wallet)
                        val accountType =
                            when (selectedProvider.uppercase()) {
                                "BCA", "MANDIRI", "SEABANK" -> "$selectedProvider Tabungan"
                                "GOPAY", "DANA", "OVO" -> selectedProvider
                                else -> "Uang Tunai"
                            }
                        onSave(accountName, accountType, selectedProvider, balance, isPrimary)
                    }
                },
                enabled = accountName.isNotBlank(),
            )
        }
    }
}

@Composable
fun ProviderPill(
    name: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor =
        if (selected) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
        } else {
            Color.Transparent
        }
    val contentColor =
        if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
    val borderColor =
        if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.outlineVariant
        }

    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(32.dp))
                .background(containerColor)
                .clickable(onClick = onClick)
                .background(Color.Transparent)
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(32.dp),
                )
                .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = contentColor,
        )
    }
}

fun formatRupiah(amount: Long): String {
    val formatter = NumberFormat.getIntegerInstance(Locale("id", "ID"))
    return "Rp ${formatter.format(amount)}"
}
