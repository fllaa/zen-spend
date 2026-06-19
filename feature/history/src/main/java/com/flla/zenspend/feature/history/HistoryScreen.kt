package com.flla.zenspend.feature.history

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.flla.zenspend.core.designsystem.theme.LocalZenSpendSpacing
import com.flla.zenspend.core.designsystem.theme.NumericDataTextStyle
import com.flla.zenspend.core.designsystem.theme.zenSpendShadowLevel1
import com.flla.zenspend.core.model.Transaction
import com.flla.zenspend.core.model.User
import com.flla.zenspend.core.ui.ScreenScaffold
import com.flla.zenspend.core.ui.collectUiState
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HistoryRoute(viewModel: HistoryViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectUiState()
    HistoryScreen(
        state = state,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onPeriodChange = viewModel::onPeriodChange,
        onAccountChange = viewModel::onAccountChange,
        onCategoryChange = viewModel::onCategoryChange,
    )
}

@Suppress("LongMethod")
@Composable
fun HistoryScreen(
    state: HistoryUiState,
    onSearchQueryChange: (String) -> Unit,
    onPeriodChange: (String) -> Unit,
    onAccountChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
) {
    val spacing = LocalZenSpendSpacing.current
    val darkTheme = isSystemInDarkTheme()

    ScreenScaffold(
        topBar = {
            ZenSpendHeader(user = state.user)
        },
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
            contentPadding =
                PaddingValues(
                    horizontal = spacing.containerPadding,
                    vertical = spacing.md,
                ),
            verticalArrangement = Arrangement.spacedBy(spacing.lg),
        ) {
            // 1. Monthly Summary Card
            item {
                MonthlySummaryCard(
                    totalExpense = state.totalExpense,
                    totalIncome = state.totalIncome,
                    remainingBudget = state.remainingBudget,
                )
            }

            // 2. Search & Filter Section
            item {
                SearchAndFilterSection(
                    query = state.searchQuery,
                    onQueryChange = onSearchQueryChange,
                    selectedPeriod = state.selectedPeriod,
                    onPeriodChange = onPeriodChange,
                    selectedAccount = state.selectedAccount,
                    onAccountChange = onAccountChange,
                    selectedCategory = state.selectedCategory,
                    onCategoryChange = onCategoryChange,
                )
            }

            // 3. Grouped Transactions
            if (state.groupedTransactions.isEmpty()) {
                item {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Tidak ada transaksi ditemukan",
                            style =
                                MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                ),
                        )
                    }
                }
            } else {
                state.groupedTransactions.forEach { (dateHeader, transactions) ->
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(spacing.md),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = dateHeader,
                                    style =
                                        MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                        ),
                                )
                                Text(
                                    text =
                                        if (dateHeader == "Hari Ini") {
                                            "Hari Ini"
                                        } else if (dateHeader == "Kemarin") {
                                            "Kemarin"
                                        } else {
                                            ""
                                        },
                                    style =
                                        MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.outline,
                                        ),
                                )
                            }

                            Column(
                                verticalArrangement = Arrangement.spacedBy(spacing.sm),
                            ) {
                                transactions.forEach { transaction ->
                                    TransactionRow(transaction = transaction)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ZenSpendHeader(
    user: User?,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalZenSpendSpacing.current
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = spacing.containerPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "ZenSpend",
            style =
                MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = (-0.02 * 24).sp,
                ),
        )

        // Profile image placeholder
        Box(
            modifier =
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .border(2.dp, MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = (user?.name?.take(1) ?: "A").uppercase(),
                style =
                    MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
            )
        }
    }
}

@Suppress("LongMethod")
@Composable
fun MonthlySummaryCard(
    totalExpense: Long,
    totalIncome: Long,
    remainingBudget: Long,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalZenSpendSpacing.current
    val darkTheme = isSystemInDarkTheme()

    // Curved gradient background matching Zen theme
    val gradient =
        Brush.linearGradient(
            colors =
                listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.primaryContainer,
                ),
        )

    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .zenSpendShadowLevel1(darkTheme),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(gradient)
                    .padding(spacing.lg),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Ringkasan Oktober",
                        style =
                            MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                                fontWeight = FontWeight.SemiBold,
                            ),
                    )
                    Icon(
                        imageVector = Icons.Rounded.CalendarMonth,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp),
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Total Pengeluaran",
                        style =
                            MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                            ),
                    )
                    Text(
                        text = formatCurrency(totalExpense),
                        style =
                            MaterialTheme.typography.displayLarge.copy(
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.SemiBold,
                            ),
                    )
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f),
                    thickness = 1.dp,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = "Pemasukan",
                            style =
                                MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                                ),
                        )
                        Text(
                            text = formatCurrency(totalIncome),
                            style =
                                NumericDataTextStyle.copy(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                ),
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = "Sisa Anggaran",
                            style =
                                MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                                ),
                        )
                        Text(
                            text = formatCurrency(remainingBudget),
                            style =
                                NumericDataTextStyle.copy(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                ),
                        )
                    }
                }
            }
        }
    }
}

@Suppress("LongMethod", "LongParameterList")
@Composable
fun SearchAndFilterSection(
    query: String,
    onQueryChange: (String) -> Unit,
    selectedPeriod: String,
    onPeriodChange: (String) -> Unit,
    selectedAccount: String,
    onAccountChange: (String) -> Unit,
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalZenSpendSpacing.current
    var periodMenuExpanded by remember { mutableStateOf(false) }
    var accountMenuExpanded by remember { mutableStateOf(false) }
    var categoryMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        // Search Input
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "Cari transaksi...",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                )
            },
            shape = RoundedCornerShape(16.dp),
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    unfocusedBorderColor = Color.Transparent,
                ),
            singleLine = true,
        )

        // Filter chips row
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            contentPadding = PaddingValues(vertical = 4.dp),
        ) {
            // Period chip
            item {
                Box {
                    FilterChipItem(
                        label = selectedPeriod,
                        icon = Icons.Rounded.CalendarToday,
                        active = selectedPeriod != "Semua",
                        onClick = { periodMenuExpanded = true },
                    )
                    DropdownMenu(
                        expanded = periodMenuExpanded,
                        onDismissRequest = { periodMenuExpanded = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text("Bulan Ini") },
                            onClick = {
                                onPeriodChange("Bulan Ini")
                                periodMenuExpanded = false
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("Semua") },
                            onClick = {
                                onPeriodChange("Semua")
                                periodMenuExpanded = false
                            },
                        )
                    }
                }
            }

            // Account chip
            item {
                Box {
                    FilterChipItem(
                        label = selectedAccount,
                        icon = Icons.AutoMirrored.Rounded.ReceiptLong,
                        active = selectedAccount != "Semua Rekening",
                        onClick = { accountMenuExpanded = true },
                    )
                    DropdownMenu(
                        expanded = accountMenuExpanded,
                        onDismissRequest = { accountMenuExpanded = false },
                    ) {
                        listOf("Semua Rekening", "BCA", "Mandiri", "Tunai").forEach { acc ->
                            DropdownMenuItem(
                                text = { Text(acc) },
                                onClick = {
                                    onAccountChange(acc)
                                    accountMenuExpanded = false
                                },
                            )
                        }
                    }
                }
            }

            // Category chip
            item {
                Box {
                    FilterChipItem(
                        label = selectedCategory,
                        icon = Icons.Rounded.Category,
                        active = selectedCategory != "Kategori",
                        onClick = { categoryMenuExpanded = true },
                    )
                    DropdownMenu(
                        expanded = categoryMenuExpanded,
                        onDismissRequest = { categoryMenuExpanded = false },
                    ) {
                        listOf("Kategori", "Makanan", "Pendapatan", "Transportasi", "Kebutuhan", "Utilitas").forEach {
                                cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    onCategoryChange(cat)
                                    categoryMenuExpanded = false
                                },
                            )
                        }
                    }
                }
            }

            // Custom Advanced Filter toggle chip
            item {
                FilterChipItem(
                    label = "Filter",
                    icon = Icons.Rounded.Tune,
                    active = false,
                    onClick = {},
                )
            }
        }
    }
}

@Composable
fun FilterChipItem(
    label: String,
    icon: ImageVector,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor =
        if (active) {
            MaterialTheme.colorScheme.secondaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainerHigh
        }
    val contentColor =
        if (active) {
            MaterialTheme.colorScheme.onSecondaryContainer
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = CircleShape,
        color = containerColor,
        contentColor = contentColor,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Suppress("LongMethod")
@Composable
fun TransactionRow(
    transaction: Transaction,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalZenSpendSpacing.current
    val darkTheme = isSystemInDarkTheme()
    val context = LocalContext.current
    var menuExpanded by remember { mutableStateOf(false) }

    // Determine category specific icon and background tints
    val (categoryIcon, categoryTint, categoryBg) =
        when (transaction.category) {
            "Makanan" ->
                Triple(
                    Icons.Rounded.Restaurant,
                    MaterialTheme.colorScheme.error,
                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
                )
            "Pendapatan" ->
                Triple(
                    Icons.Rounded.Payments,
                    MaterialTheme.colorScheme.tertiary,
                    MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f),
                )
            "Transportasi" ->
                Triple(
                    Icons.Rounded.DirectionsCar,
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                )
            "Kebutuhan" ->
                Triple(
                    Icons.Rounded.ShoppingBag,
                    MaterialTheme.colorScheme.secondary,
                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f),
                )
            "Utilitas" ->
                Triple(
                    Icons.AutoMirrored.Rounded.ReceiptLong,
                    Color(0xFFE65100),
                    Color(0xFFFFE0B2).copy(alpha = 0.4f),
                )
            else ->
                Triple(
                    Icons.Rounded.Coffee,
                    MaterialTheme.colorScheme.outline,
                    MaterialTheme.colorScheme.surfaceContainerHigh,
                )
        }

    Box(modifier = modifier) {
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = {},
                        onLongClick = { menuExpanded = true },
                    )
                    .zenSpendShadowLevel1(darkTheme),
            shape = RoundedCornerShape(16.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                ),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                // Category Icon with soft background
                Box(
                    modifier =
                        Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(categoryBg),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = categoryIcon,
                        contentDescription = null,
                        tint = categoryTint,
                        modifier = Modifier.size(24.dp),
                    )
                }

                // Title and Details (Category + Account)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = transaction.title,
                        style =
                            MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                            ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = transaction.category,
                            style =
                                MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.outline,
                                ),
                        )
                        Box(
                            modifier =
                                Modifier
                                    .size(3.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                        )
                        Text(
                            text = transaction.account,
                            style =
                                MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.outline,
                                ),
                        )
                    }
                }

                // Transaction amount and time
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    val amountText = (if (transaction.isIncome) "+" else "-") + " " + formatCurrency(transaction.amount)
                    val amountColor =
                        if (transaction.isIncome) {
                            MaterialTheme.colorScheme.tertiary
                        } else {
                            MaterialTheme.colorScheme.error
                        }

                    Text(
                        text = amountText,
                        style =
                            NumericDataTextStyle.copy(
                                color = amountColor,
                                fontWeight = FontWeight.SemiBold,
                            ),
                    )

                    // Format time from timestamp
                    val timeString = formatTime(transaction.timestamp)
                    Text(
                        text = timeString,
                        style =
                            MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.outline,
                            ),
                    )
                }
            }
        }

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
        ) {
            DropdownMenuItem(
                text = { Text("Ubah") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = null,
                    )
                },
                onClick = {
                    menuExpanded = false
                    Toast.makeText(context, "Ubah: ${transaction.title}", Toast.LENGTH_SHORT).show()
                },
            )
            DropdownMenuItem(
                text = { Text("Hapus") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                    )
                },
                onClick = {
                    menuExpanded = false
                    Toast.makeText(context, "Hapus: ${transaction.title}", Toast.LENGTH_SHORT).show()
                },
            )
        }
    }
}

// Format numbers to Indonesian Currency style
private fun formatCurrency(amount: Long): String {
    val numberFormat = NumberFormat.getNumberInstance(Locale("in", "ID"))
    return "Rp ${numberFormat.format(amount)}"
}

// Format timestamp to hh:mm
private fun formatTime(timestamp: Long): String {
    val instant = java.time.Instant.ofEpochMilli(timestamp)
    val localDateTime = instant.atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()
    return String.format(Locale.US, "%02d:%02d", localDateTime.hour, localDateTime.minute)
}
