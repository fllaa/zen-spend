package com.flla.zenspend.feature.home

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DonutLarge
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.flla.zenspend.core.designsystem.component.categoryIcon
import com.flla.zenspend.core.designsystem.component.categoryVisualStyle
import com.flla.zenspend.core.designsystem.theme.LocalZenSpendSpacing
import com.flla.zenspend.core.designsystem.theme.NumericDataTextStyle
import com.flla.zenspend.core.designsystem.theme.zenSpendShadowLevel1
import com.flla.zenspend.core.designsystem.theme.zenSpendShadowLevel2
import com.flla.zenspend.core.model.User
import com.flla.zenspend.core.ui.collectUiState
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeRoute(viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectUiState()
    HomeScreen(state = state)
}

@Suppress("LongMethod")
@Composable
fun HomeScreen(state: HomeUiState) {
    val spacing = LocalZenSpendSpacing.current
    val primaryColor = MaterialTheme.colorScheme.primary
    val scrollState = rememberScrollState()

    var balanceVisible by rememberSaveable { mutableStateOf(true) }

    val categoryItems =
        state.topCategories.map { category ->
            val categoryStyle =
                categoryVisualStyle(
                    iconName = category.iconName,
                    colorHex = category.colorHex,
                )
            TopCategoryItem(
                name = category.name,
                amount = formatCompactCurrency(category.amount),
                icon = categoryStyle.icon,
                iconColor = categoryStyle.tint,
                containerColor = categoryStyle.containerTint,
            )
        }

    val transactionItems =
        state.recentTransactions.map { transaction ->
            TransactionItem(
                title = transaction.title,
                timestamp = formatTransactionTimestamp(transaction.timestamp),
                amount = formatAmountWithoutPrefix(transaction.amount),
                isIncome = transaction.isIncome,
                icon = categoryIcon(transaction.iconName),
            )
        }

    Scaffold(
        topBar = {
            HomeTopBar(
                user = state.user,
                currentMonth = state.currentMonthLabel,
                onMonthClick = {},
                onProfileClick = {},
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .drawBehind {
                        // Soft glowing Japanese minimalism gradient blob in top right
                        drawRect(
                            brush =
                                Brush.radialGradient(
                                    colors = listOf(primaryColor.copy(alpha = 0.05f), Color.Transparent),
                                    center = Offset(size.width * 0.8f, size.height * 0.15f),
                                    radius = size.minDimension * 0.8f,
                                ),
                        )
                    }
                    .verticalScroll(scrollState)
                    .padding(horizontal = spacing.containerPadding, vertical = spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.lg),
        ) {
            // Welcome Greeting
            WelcomeGreeting(userName = state.user?.name.orEmpty())

            // Balance Card
            BalanceCard(
                balance = formatAmountWithoutPrefix(state.totalBalance),
                income = formatCompactCurrency(state.totalIncome),
                expense = formatCompactCurrency(state.totalExpense),
                balanceVisible = balanceVisible,
                onToggleBalanceVisibility = { balanceVisible = !balanceVisible },
            )

            // Budget Bento cards
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                BudgetProgressCard(
                    spentProgress = state.budgetProgress,
                    remainingAmount = state.budgetRemaining,
                    spentAmount = state.budgetSpent,
                    limitAmount = state.budgetLimit,
                )
                StatisticsPreviewCard(bars = state.weeklyExpenseBars)
            }

            // Top Categories section
            TopCategoriesSection(categories = categoryItems)

            // Recent Transactions section
            RecentTransactionsSection(transactions = transactionItems)
        }
    }
}

@Suppress("LongMethod")
@Composable
fun HomeTopBar(
    user: User?,
    currentMonth: String,
    onMonthClick: () -> Unit,
    onProfileClick: () -> Unit,
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

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            // Month selector pill
            Surface(
                modifier = Modifier.clickable { onMonthClick() },
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(32.dp),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = currentMonth.uppercase(),
                        style =
                            MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium,
                            ),
                    )
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // Profile avatar
            Box(
                modifier =
                    Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .border(2.dp, MaterialTheme.colorScheme.primaryContainer, CircleShape)
                        .clickable { onProfileClick() },
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
}

@Composable
fun WelcomeGreeting(
    userName: String,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalZenSpendSpacing.current
    val displayName = userName.ifBlank { "teman" }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.xs),
    ) {
        Text(
            text = "Selamat pagi, $displayName",
            style =
                MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
        )
        Text(
            text = "Kelola keuanganmu hari ini",
            style =
                MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                ),
        )
    }
}

@Suppress("LongMethod")
@Composable
fun BalanceCard(
    balance: String,
    income: String,
    expense: String,
    balanceVisible: Boolean,
    onToggleBalanceVisibility: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalZenSpendSpacing.current
    val darkTheme = isSystemInDarkTheme()

    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .zenSpendShadowLevel2(),
        shape = MaterialTheme.shapes.extraLarge,
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(spacing.lg),
        ) {
            Box(
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .size(128.dp)
                        .drawBehind {
                            drawCircle(
                                color = Color.White.copy(alpha = 0.1f),
                                radius = size.width / 2f,
                                center = Offset(size.width, -size.height / 3f),
                            )
                        },
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Saldo Total",
                        style =
                            MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f),
                                letterSpacing = (0.15 * 12).sp,
                            ),
                    )
                    IconButton(
                        onClick = onToggleBalanceVisibility,
                        modifier = Modifier.size(24.dp),
                    ) {
                        Icon(
                            imageVector = if (balanceVisible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                            contentDescription = if (balanceVisible) "Sembunyikan Saldo" else "Tampilkan Saldo",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = "Rp",
                        style =
                            MaterialTheme.typography.titleMedium.copy(
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                            ),
                        modifier = Modifier.padding(bottom = 6.dp),
                    )
                    Text(
                        text = if (balanceVisible) balance else "••••••••",
                        style =
                            MaterialTheme.typography.displayLarge.copy(
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            ),
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.md),
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = Color.White.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                text = "Pemasukan",
                                style =
                                    MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                                    ),
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ArrowDownward,
                                    contentDescription = null,
                                    tint = if (darkTheme) Color(0xFF78DC77) else Color(0xFF94F990),
                                    modifier = Modifier.size(16.dp),
                                )
                                Text(
                                    text = income,
                                    style =
                                        NumericDataTextStyle.copy(
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        ),
                                )
                            }
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        color = Color.White.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                text = "Pengeluaran",
                                style =
                                    MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                                    ),
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ArrowUpward,
                                    contentDescription = null,
                                    tint = if (darkTheme) Color(0xFFFFB4AB) else Color(0xFFFFDAD6),
                                    modifier = Modifier.size(16.dp),
                                )
                                Text(
                                    text = expense,
                                    style =
                                        NumericDataTextStyle.copy(
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        ),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Suppress("LongMethod")
@Composable
fun BudgetProgressCard(
    spentProgress: Float,
    remainingAmount: Long,
    spentAmount: Long,
    limitAmount: Long,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalZenSpendSpacing.current
    val darkTheme = isSystemInDarkTheme()

    Card(
        modifier = modifier.zenSpendShadowLevel1(darkTheme),
        shape = MaterialTheme.shapes.large,
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            ),
    ) {
        Column(
            modifier = Modifier.padding(spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Anggaran Bulan Ini",
                    style =
                        MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                )
                Icon(
                    imageVector = Icons.Rounded.DonutLarge,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val remainingLabel =
                        if (limitAmount <= 0L) {
                            "Belum ada budget"
                        } else if (remainingAmount >= 0L) {
                            "Sisa ${formatCompactCurrency(remainingAmount)}"
                        } else {
                            "Lebih ${formatCompactCurrency(-remainingAmount)}"
                        }
                    Text(
                        text =
                            if (limitAmount <= 0L) {
                                "Tambahkan budget pertamamu"
                            } else {
                                "${(spentProgress * 100).toInt()}% Terpakai"
                            },
                        style =
                            MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                    )
                    Text(
                        text = remainingLabel,
                        style =
                            MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                    )
                }

                LinearProgressIndicator(
                    progress = { spentProgress },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(32.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                )

                Text(
                    text =
                        if (limitAmount <= 0L) {
                            "Budget akan muncul setelah kamu menambahkannya dari halaman profile."
                        } else {
                            "${formatCompactCurrency(spentAmount)} dari ${formatCompactCurrency(limitAmount)}"
                        },
                    style =
                        MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}

@Suppress("LongMethod")
@Composable
fun StatisticsPreviewCard(
    bars: List<HomeStatisticBar>,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalZenSpendSpacing.current
    val darkTheme = isSystemInDarkTheme()

    Card(
        modifier = modifier.zenSpendShadowLevel1(darkTheme),
        shape = MaterialTheme.shapes.large,
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            ),
    ) {
        Column(
            modifier = Modifier.padding(spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Statistik",
                    style =
                        MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                )
                Text(
                    text = "7 Hari Terakhir",
                    style =
                        MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                )
            }

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(96.dp)
                        .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                bars.ifEmpty {
                    List(7) { index ->
                        HomeStatisticBar(
                            label = index.toString(),
                            amount = 0L,
                            heightPercentage = 0.12f,
                            isHighlighted = index == 6,
                        )
                    }
                }.forEach { bar ->
                    Box(
                        modifier =
                            Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp)
                                .fillMaxHeight(bar.heightPercentage)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(
                                    if (bar.isHighlighted) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.surfaceContainerHighest
                                    },
                                ),
                    )
                }
            }
        }
    }
}

data class TopCategoryItem(
    val name: String,
    val amount: String,
    val icon: ImageVector,
    val iconColor: Color,
    val containerColor: Color,
)

@Suppress("LongMethod")
@Composable
fun TopCategoriesSection(
    categories: List<TopCategoryItem>,
    onSeeAllClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val spacing = LocalZenSpendSpacing.current
    val darkTheme = isSystemInDarkTheme()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Kategori Teratas",
                style =
                    MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
            )
            TextButton(
                onClick = onSeeAllClick,
                contentPadding = PaddingValues(0.dp),
            ) {
                Text(
                    text = "Lihat Semua",
                    style =
                        MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium,
                        ),
                )
            }
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.md),
            contentPadding = PaddingValues(bottom = 8.dp),
        ) {
            items(categories) { item ->
                Card(
                    modifier =
                        Modifier
                            .width(128.dp)
                            .zenSpendShadowLevel1(darkTheme),
                    shape = MaterialTheme.shapes.large,
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                        ),
                ) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(item.containerColor),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = item.iconColor,
                                modifier = Modifier.size(24.dp),
                            )
                        }
                        Text(
                            text = item.name,
                            style =
                                MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                ),
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = item.amount,
                            style =
                                NumericDataTextStyle.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 14.sp,
                                ),
                        )
                    }
                }
            }
        }
    }
}

data class TransactionItem(
    val title: String,
    val timestamp: String,
    val amount: String,
    val isIncome: Boolean,
    val icon: ImageVector,
)

@OptIn(ExperimentalFoundationApi::class)
@Suppress("LongMethod")
@Composable
fun RecentTransactionsSection(
    transactions: List<TransactionItem>,
    onHistoryClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val spacing = LocalZenSpendSpacing.current
    val darkTheme = isSystemInDarkTheme()
    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Transaksi Terakhir",
                style =
                    MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
            )
            TextButton(
                onClick = onHistoryClick,
                contentPadding = PaddingValues(0.dp),
            ) {
                Text(
                    text = "Riwayat",
                    style =
                        MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium,
                        ),
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            transactions.forEach { transaction ->
                var menuExpanded by remember { mutableStateOf(false) }
                Box(modifier = Modifier.fillMaxWidth()) {
                    Card(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .combinedClickable(
                                    onClick = {},
                                    onLongClick = { menuExpanded = true },
                                )
                                .zenSpendShadowLevel1(darkTheme),
                        shape = MaterialTheme.shapes.large,
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
                            Box(
                                modifier =
                                    Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = transaction.icon,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(24.dp),
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                            ) {
                                Text(
                                    text = transaction.title,
                                    style =
                                        MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                        ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = transaction.timestamp,
                                    style =
                                        MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        ),
                                )
                            }

                            Text(
                                text = (if (transaction.isIncome) "+" else "-") + "Rp " + transaction.amount,
                                style =
                                    NumericDataTextStyle.copy(
                                        color =
                                            if (transaction.isIncome) {
                                                MaterialTheme.colorScheme.tertiary
                                            } else {
                                                MaterialTheme.colorScheme.error
                                            },
                                        fontWeight = FontWeight.SemiBold,
                                    ),
                            )
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
        }
    }
}

private fun formatAmountWithoutPrefix(amount: Long): String =
    NumberFormat.getNumberInstance(Locale("in", "ID")).format(amount)

private fun formatCompactCurrency(amount: Long): String {
    val absAmount = kotlin.math.abs(amount)
    val formatted =
        when {
            absAmount >= 1_000_000_000L -> String.format(Locale.US, "%.1fB", amount / 1_000_000_000f)
            absAmount >= 1_000_000L -> String.format(Locale.US, "%.1fM", amount / 1_000_000f)
            absAmount >= 1_000L -> String.format(Locale.US, "%.0frb", amount / 1_000f)
            else -> formatAmountWithoutPrefix(amount)
        }
    return "Rp $formatted"
}

private fun formatTransactionTimestamp(timestamp: Long): String {
    val zoneId = ZoneId.systemDefault()
    val today = LocalDate.now()
    val dateTime = Instant.ofEpochMilli(timestamp).atZone(zoneId)
    val date = dateTime.toLocalDate()
    val time = dateTime.toLocalTime()
    val timeText = time.format(DateTimeFormatter.ofPattern("HH:mm", Locale("id", "ID")))
    return when (date) {
        today -> "Hari ini, $timeText"
        today.minusDays(1) -> "Kemarin, $timeText"
        else -> date.format(DateTimeFormatter.ofPattern("dd MMM, HH:mm", Locale("id", "ID")))
    }
}
