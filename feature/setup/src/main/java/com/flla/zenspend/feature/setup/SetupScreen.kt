package com.flla.zenspend.feature.setup

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material.icons.rounded.MedicalServices
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.flla.zenspend.core.designsystem.theme.LocalZenSpendSpacing
import com.flla.zenspend.core.ui.collectUiState

@Composable
fun SetupRoute(
    onCompleted: () -> Unit,
    viewModel: SetupViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectUiState()

    LaunchedEffect(state.hasCompleted) {
        if (state.hasCompleted) {
            onCompleted()
        }
    }

    BackHandler(enabled = state.step != SetupStep.CURRENCY) {
        viewModel.previousStep()
    }

    SetupScreen(
        state = state,
        onBackClick = {
            if (!viewModel.previousStep()) {
                // If we're on the first step, back click does nothing or finishes
            }
        },
        onSelectCurrency = viewModel::selectCurrency,
        onSelectAccountType = viewModel::selectAccountType,
        onAccountBalanceChanged = viewModel::updateAccountBalance,
        onAccountNameChanged = viewModel::updateAccountName,
        onToggleCategory = viewModel::toggleCategory,
        onNextClick = viewModel::nextStep,
        onCompleteClick = viewModel::completeSetup,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun SetupScreen(
    state: SetupUiState,
    onBackClick: () -> Unit,
    onSelectCurrency: (String) -> Unit,
    onSelectAccountType: (AccountType) -> Unit,
    onAccountBalanceChanged: (String) -> Unit,
    onAccountNameChanged: (String) -> Unit,
    onToggleCategory: (String) -> Unit,
    onNextClick: () -> Unit,
    onCompleteClick: () -> Unit,
) {
    val spacing = LocalZenSpendSpacing.current
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "ZenSpend",
                            style =
                                MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    letterSpacing = (-0.5).sp,
                                ),
                        )
                    }
                },
                navigationIcon = {
                    if (state.step != SetupStep.CURRENCY) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.testTag("setup_back"),
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Kembali",
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(48.dp))
                    }
                },
                actions = {
                    Spacer(modifier = Modifier.size(48.dp))
                },
                windowInsets = WindowInsets(0.dp),
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                    ),
            )
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .drawBehind {
                        // Soft glowing background blobs
                        drawRect(
                            brush =
                                Brush.radialGradient(
                                    colors = listOf(primaryColor.copy(alpha = 0.05f), Color.Transparent),
                                    center = Offset(size.width * 0.8f, size.height * 0.2f),
                                    radius = size.minDimension * 0.7f,
                                ),
                        )
                        drawRect(
                            brush =
                                Brush.radialGradient(
                                    colors = listOf(secondaryColor.copy(alpha = 0.03f), Color.Transparent),
                                    center = Offset(size.width * 0.2f, size.height * 0.8f),
                                    radius = size.minDimension * 0.7f,
                                ),
                        )
                    }
                    .padding(horizontal = spacing.lg),
        ) {
            // Progress Bar Section
            val progressProgress by animateFloatAsState(
                targetValue =
                    when (state.step) {
                        SetupStep.CURRENCY -> 0.33f
                        SetupStep.ACCOUNT -> 0.66f
                        SetupStep.CATEGORIES -> 1.0f
                    },
                label = "progress",
            )

            val stepLabel =
                when (state.step) {
                    SetupStep.CURRENCY -> "Langkah 1 dari 3"
                    SetupStep.ACCOUNT -> "Langkah 2 dari 3"
                    SetupStep.CATEGORIES -> "Langkah 3 dari 3"
                }

            val stepPercent =
                when (state.step) {
                    SetupStep.CURRENCY -> "33%"
                    SetupStep.ACCOUNT -> "66%"
                    SetupStep.CATEGORIES -> "100%"
                }

            Column(modifier = Modifier.padding(vertical = spacing.md)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stepLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = stepPercent,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Spacer(modifier = Modifier.height(spacing.xs))
                LinearProgressIndicator(
                    progress = { progressProgress },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }

            AnimatedContent(
                targetState = state.step,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                label = "setup_step_content",
            ) { targetStep ->
                when (targetStep) {
                    SetupStep.CURRENCY -> {
                        CurrencyStepContent(
                            selectedCurrency = state.selectedCurrency,
                            onSelectCurrency = onSelectCurrency,
                            onNextClick = onNextClick,
                        )
                    }
                    SetupStep.ACCOUNT -> {
                        AccountStepContent(
                            selectedCurrency = state.selectedCurrency,
                            accountType = state.accountType,
                            accountBalance = state.accountBalance,
                            accountName = state.accountName,
                            onSelectAccountType = onSelectAccountType,
                            onAccountBalanceChanged = onAccountBalanceChanged,
                            onAccountNameChanged = onAccountNameChanged,
                            onNextClick = onNextClick,
                        )
                    }
                    SetupStep.CATEGORIES -> {
                        CategoriesStepContent(
                            selectedCategories = state.selectedCategories,
                            onToggleCategory = onToggleCategory,
                            onCompleteClick = onCompleteClick,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CurrencyStepContent(
    selectedCurrency: String,
    onSelectCurrency: (String) -> Unit,
    onNextClick: () -> Unit,
) {
    val spacing = LocalZenSpendSpacing.current
    val scrollState = rememberScrollState()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = spacing.xl),
    ) {
        Text(
            text = "Pilih Mata Uang",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(spacing.xs))
        Text(
            text = "Pilih mata uang utama yang akan Anda gunakan untuk melacak keuangan.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(spacing.lg))

        val currencies =
            listOf(
                CurrencyOption("IDR", "Rupiah (IDR)", "Indonesia", "Rp"),
                CurrencyOption("USD", "US Dollar (USD)", "Amerika Serikat", "$"),
                CurrencyOption("EUR", "Euro (EUR)", "Uni Eropa", "€"),
                CurrencyOption("SGD", "Singapore Dollar (SGD)", "Singapura", "S$"),
            )

        Column(
            verticalArrangement = Arrangement.spacedBy(spacing.md),
            modifier = Modifier.weight(1f),
        ) {
            currencies.forEach { option ->
                val active = option.code == selectedCurrency
                val strokeColor =
                    if (active) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outlineVariant
                    }
                val containerColor =
                    if (active) {
                        MaterialTheme.colorScheme.primary.copy(
                            alpha = 0.04f,
                        )
                    } else {
                        MaterialTheme.colorScheme.surface
                    }

                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable { onSelectCurrency(option.code) }
                            .testTag("currency_option_${option.code}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = containerColor),
                    border = BorderStroke(1.dp, strokeColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                ) {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(spacing.md),
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
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (active) {
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                            } else {
                                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                            },
                                        ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = option.symbol,
                                    style =
                                        MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color =
                                                if (active) {
                                                    MaterialTheme.colorScheme.primary
                                                } else {
                                                    MaterialTheme.colorScheme.onSurfaceVariant
                                                },
                                        ),
                                )
                            }
                            Column {
                                Text(
                                    text = option.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Text(
                                    text = option.country,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }

                        // Custom radio indicator
                        Box(
                            modifier =
                                Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = 2.dp,
                                        color =
                                            if (active) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.outlineVariant
                                            },
                                        shape = CircleShape,
                                    )
                                    .padding(4.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (active) {
                                Box(
                                    modifier =
                                        Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary),
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(spacing.lg))

        Button(
            onClick = onNextClick,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("currency_next"),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Lanjutkan",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                )
                Spacer(modifier = Modifier.width(spacing.xs))
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = null,
                )
            }
        }
    }
}

private data class CurrencyOption(
    val code: String,
    val name: String,
    val country: String,
    val symbol: String,
)

@Composable
private fun AccountStepContent(
    selectedCurrency: String,
    accountType: AccountType,
    accountBalance: String,
    accountName: String,
    onSelectAccountType: (AccountType) -> Unit,
    onAccountBalanceChanged: (String) -> Unit,
    onAccountNameChanged: (String) -> Unit,
    onNextClick: () -> Unit,
) {
    val spacing = LocalZenSpendSpacing.current
    val scrollState = rememberScrollState()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = spacing.xl),
    ) {
        Text(
            text = "Tambah Akun Pertama",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(spacing.xs))
        Text(
            text = "Masukkan saldo awal untuk akun yang paling sering Anda gunakan.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(spacing.lg))

        // Account Type Label
        Text(
            text = "Tipe Akun",
            style =
                MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(spacing.sm))

        // Type Chips Grid/Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            val types =
                listOf(
                    AccountTypeOption(AccountType.CASH, "Tunai", Icons.Rounded.Payments),
                    AccountTypeOption(AccountType.BANK, "Bank", Icons.Rounded.AccountBalance),
                    AccountTypeOption(AccountType.WALLET, "E-wallet", Icons.Rounded.AccountBalanceWallet),
                )

            types.forEach { option ->
                val active = option.type == accountType
                val containerColor =
                    if (active) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(
                            alpha = 0.4f,
                        )
                    }
                val textColor =
                    if (active) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }

                Column(
                    modifier =
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(containerColor)
                            .clickable { onSelectAccountType(option.type) }
                            .padding(vertical = spacing.md)
                            .testTag("account_type_${option.type.name}"),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    if (active) {
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    },
                                ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = option.icon,
                            contentDescription = option.label,
                            tint =
                                if (active) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    Spacer(modifier = Modifier.height(spacing.xs))
                    Text(
                        text = option.label,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = textColor,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(spacing.lg))

        // Balance Input Card
        Text(
            text = "Saldo Awal",
            style =
                MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(spacing.sm))

        val currencySymbol =
            when (selectedCurrency) {
                "USD" -> "$"
                "EUR" -> "€"
                "SGD" -> "S$"
                else -> "Rp"
            }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.03f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = spacing.lg, horizontal = spacing.md),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = currencySymbol,
                    style =
                        TextStyle(
                            fontFamily = MaterialTheme.typography.headlineLarge.fontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                        ),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                )
                Spacer(modifier = Modifier.width(spacing.xs))

                BasicTextField(
                    value = accountBalance,
                    onValueChange = onAccountBalanceChanged,
                    modifier =
                        Modifier
                            .width(200.dp)
                            .testTag("account_balance_input"),
                    textStyle =
                        TextStyle(
                            fontFamily = MaterialTheme.typography.headlineLarge.fontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Start,
                        ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    decorationBox = { innerTextField ->
                        Box(contentAlignment = Alignment.CenterStart) {
                            if (accountBalance.isEmpty()) {
                                Text(
                                    text = "0",
                                    style =
                                        TextStyle(
                                            fontFamily = MaterialTheme.typography.headlineLarge.fontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 32.sp,
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                        ),
                                )
                            }
                            innerTextField()
                        }
                    },
                )
            }
        }

        Spacer(modifier = Modifier.height(spacing.lg))

        // Account Name Input Card
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .padding(horizontal = spacing.md, vertical = spacing.sm),
        ) {
            Column {
                Text(
                    text = "Nama Akun",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(spacing.xs))
                BasicTextField(
                    value = accountName,
                    onValueChange = onAccountNameChanged,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = spacing.xs)
                            .testTag("account_name_input"),
                    textStyle =
                        MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                    singleLine = true,
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    decorationBox = { innerTextField ->
                        Box {
                            if (accountName.isEmpty()) {
                                Text(
                                    text = "Misal: Dompet Utama, BCA, GoPay",
                                    style =
                                        MaterialTheme.typography.bodyLarge.copy(
                                            color = MaterialTheme.colorScheme.outlineVariant,
                                        ),
                                )
                            }
                            innerTextField()
                        }
                    },
                )
            }
        }

        Spacer(modifier = Modifier.height(spacing.xl))

        // Decorative Zen element
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = Icons.Rounded.Eco,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                modifier = Modifier.size(48.dp),
            )
            Spacer(modifier = Modifier.height(spacing.xs))
            Text(
                text = "Tetap tenang, kelola bijak.",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNextClick,
            enabled = accountName.isNotBlank() && accountBalance.isNotBlank(),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("account_next"),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Lanjutkan",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                )
                Spacer(modifier = Modifier.width(spacing.xs))
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = null,
                )
            }
        }
    }
}

private data class AccountTypeOption(
    val type: AccountType,
    val label: String,
    val icon: ImageVector,
)

@Composable
private fun CategoriesStepContent(
    selectedCategories: Set<String>,
    onToggleCategory: (String) -> Unit,
    onCompleteClick: () -> Unit,
) {
    val spacing = LocalZenSpendSpacing.current

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(bottom = spacing.xl),
    ) {
        Text(
            text = "Pilih Kategori",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(spacing.xs))
        Text(
            text = "Pilih kategori pengeluaran yang ingin Anda pantau. Anda bisa menambahkannya nanti.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(spacing.lg))

        val categories =
            listOf(
                CategoryOptionInfo("Makanan", Icons.Rounded.Restaurant),
                CategoryOptionInfo("Transportasi", Icons.Rounded.DirectionsCar),
                CategoryOptionInfo("Belanja", Icons.Rounded.ShoppingBag),
                CategoryOptionInfo("Hiburan", Icons.Rounded.Movie),
                CategoryOptionInfo("Kesehatan", Icons.Rounded.MedicalServices),
                CategoryOptionInfo("Tagihan", Icons.AutoMirrored.Rounded.ReceiptLong),
                CategoryOptionInfo("Pendidikan", Icons.Rounded.School),
                CategoryOptionInfo("Lainnya", Icons.Rounded.MoreHoriz),
            )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.md),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = spacing.md),
        ) {
            items(categories) { category ->
                val active = selectedCategories.contains(category.name)
                val borderStrokeColor =
                    if (active) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        Color.Transparent
                    }
                val containerColor =
                    if (active) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surface
                    }

                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable { onToggleCategory(category.name) }
                            .testTag("category_option_${category.name}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = containerColor),
                    border = BorderStroke(2.dp, borderStrokeColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                ) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(spacing.md),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (active) {
                                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                                        } else {
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                                        },
                                    ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = category.icon,
                                contentDescription = category.name,
                                tint =
                                    if (active) {
                                        MaterialTheme.colorScheme.onPrimary
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    },
                                modifier = Modifier.size(20.dp),
                            )
                        }
                        Spacer(modifier = Modifier.height(spacing.md))
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color =
                                if (active) {
                                    MaterialTheme.colorScheme.onPrimary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                },
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(spacing.lg))

        Button(
            onClick = onCompleteClick,
            enabled = selectedCategories.isNotEmpty(),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("setup_finish"),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Selesai",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                )
                Spacer(modifier = Modifier.width(spacing.xs))
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                )
            }
        }
    }
}

private data class CategoryOptionInfo(
    val name: String,
    val icon: ImageVector,
)
