@file:Suppress("LongMethod", "CyclomaticComplexMethod")

package com.flla.zenspend.feature.analytics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flla.zenspend.core.designsystem.component.ZenSpendTopAppBar
import com.flla.zenspend.core.designsystem.theme.LocalZenSpendSpacing
import com.flla.zenspend.core.designsystem.theme.zenSpendShadowLevel1
import com.flla.zenspend.core.model.Transaction
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun AnalyticsCategoryDetailRoute(
    onBackClick: () -> Unit,
    viewModel: AnalyticsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    AnalyticsCategoryDetailScreen(
        state = state,
        onPeriodSelected = viewModel::onPeriodSelected,
        onBackClick = onBackClick,
    )
}

@Composable
fun AnalyticsCategoryDetailScreen(
    state: AnalyticsUiState,
    onPeriodSelected: (String) -> Unit,
    onBackClick: () -> Unit,
) {
    val spacing = LocalZenSpendSpacing.current
    val darkTheme = isSystemInDarkTheme()

    // Keep track of expanded categories
    val expandedStates = remember { mutableStateMapOf<String, Boolean>() }

    Scaffold(
        topBar = {
            ZenSpendTopAppBar(
                title = "Rincian Kategori",
                onBackClick = onBackClick,
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = spacing.containerPadding),
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            // Period selector item
            item {
                Spacer(modifier = Modifier.height(spacing.sm))
                PeriodSelector(
                    selectedPeriod = state.selectedPeriod,
                    onPeriodSelected = onPeriodSelected,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(spacing.sm))
            }

            // Total spending card item
            item {
                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .zenSpendShadowLevel1(darkTheme),
                    shape = RoundedCornerShape(16.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                        ),
                ) {
                    Column(
                        modifier = Modifier.padding(spacing.lg),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(spacing.xs),
                    ) {
                        Text(
                            text = "Total Pengeluaran (${state.selectedPeriod})",
                            style =
                                MaterialTheme.typography.labelMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium,
                                ),
                        )
                        Text(
                            text = formatCurrency(state.totalExpense),
                            style =
                                MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontFeatureSettings = "tnum",
                                ),
                        )
                    }
                }
            }

            // Categories detailed list
            if (state.categories.isNotEmpty()) {
                items(state.categories, key = { it.category }) { categoryBreakdown ->
                    val isExpanded = expandedStates[categoryBreakdown.category] ?: false
                    val color = getCategoryColor(categoryBreakdown.category)
                    val icon = getCategoryIcon(categoryBreakdown.category)
                    val displayName = getCategoryDisplayName(categoryBreakdown.category)

                    // Animate rotation of the expand caret icon
                    val rotationAngle by animateFloatAsState(
                        targetValue = if (isExpanded) 180f else 0f,
                        label = "CaretRotation",
                    )

                    // Budget health status color mapping
                    val budgetHealthColor =
                        when {
                            categoryBreakdown.budgetProgress >= 1.0f -> MaterialTheme.colorScheme.error
                            categoryBreakdown.budgetProgress >= 0.8f -> Color(0xFFF57C00) // Near limit orange
                            else -> Color(0xFF2E7D32) // Good green
                        }

                    Card(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .zenSpendShadowLevel1(darkTheme)
                                .animateContentSize(),
                        shape = RoundedCornerShape(16.dp),
                        colors =
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                            ),
                    ) {
                        Column(
                            modifier =
                                Modifier
                                    .clickable {
                                        expandedStates[categoryBreakdown.category] = !isExpanded
                                    }
                                    .padding(spacing.md),
                            verticalArrangement = Arrangement.spacedBy(spacing.md),
                        ) {
                            // Row 1: Header information (Icon, Name, Amount, Caret)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(spacing.md),
                            ) {
                                Box(
                                    modifier =
                                        Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(color.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = color,
                                        modifier = Modifier.size(24.dp),
                                    )
                                }

                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(2.dp),
                                ) {
                                    Text(
                                        text = displayName,
                                        style =
                                            MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                            ),
                                    )
                                    Text(
                                        text = "${categoryBreakdown.transactions.size} Transaksi",
                                        style =
                                            MaterialTheme.typography.labelSmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontWeight = FontWeight.Normal,
                                            ),
                                    )
                                }

                                Column(
                                    horizontalAlignment = Alignment.End,
                                    verticalArrangement = Arrangement.spacedBy(2.dp),
                                ) {
                                    Text(
                                        text = formatCurrency(categoryBreakdown.amount),
                                        style =
                                            MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontFeatureSettings = "tnum",
                                            ),
                                    )
                                    Text(
                                        text = "${(categoryBreakdown.percentage * 100).toInt()}% dari total",
                                        style =
                                            MaterialTheme.typography.labelSmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontWeight = FontWeight.Medium,
                                            ),
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Rounded.KeyboardArrowDown,
                                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier =
                                        Modifier
                                            .size(24.dp)
                                            .rotate(rotationAngle),
                                )
                            }

                            // Row 2: Budget Progress details
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(spacing.xs),
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = "${formatCurrency(
                                            categoryBreakdown.amount,
                                        )} / ${formatCurrency(categoryBreakdown.budgetLimit)} limit",
                                        style =
                                            MaterialTheme.typography.labelSmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontFeatureSettings = "tnum",
                                            ),
                                    )
                                    Text(
                                        text = "${(categoryBreakdown.budgetProgress * 100).toInt()}% Terpakai",
                                        style =
                                            MaterialTheme.typography.labelSmall.copy(
                                                color = budgetHealthColor,
                                                fontWeight = FontWeight.SemiBold,
                                            ),
                                    )
                                }

                                LinearProgressIndicator(
                                    progress = { categoryBreakdown.budgetProgress },
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                    color = budgetHealthColor,
                                    trackColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                )
                            }

                            // Row 3: Expanded transaction history
                            AnimatedVisibility(visible = isExpanded) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(spacing.sm),
                                ) {
                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(vertical = spacing.xs),
                                    )

                                    Text(
                                        text = "Riwayat Transaksi",
                                        style =
                                            MaterialTheme.typography.labelMedium.copy(
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontWeight = FontWeight.SemiBold,
                                            ),
                                    )

                                    if (categoryBreakdown.transactions.isNotEmpty()) {
                                        categoryBreakdown.transactions.forEach { transaction ->
                                            TransactionRowItem(transaction = transaction)
                                        }
                                    } else {
                                        Text(
                                            text = "Tidak ada transaksi",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.Center,
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = spacing.sm),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                item {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Tidak ada transaksi di periode ini.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            // Bottom padding spacer
            item {
                Spacer(modifier = Modifier.height(spacing.xl))
            }
        }
    }
}

@Composable
private fun TransactionRowItem(
    transaction: Transaction,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalZenSpendSpacing.current

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = transaction.title,
                style =
                    MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                Text(
                    text = formatDate(transaction.timestamp),
                    style =
                        MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                )
                Box(
                    modifier =
                        Modifier
                            .size(3.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.outlineVariant),
                )
                Text(
                    text = transaction.account,
                    style =
                        MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                )
            }
        }

        Text(
            text = "- ${formatCurrency(transaction.amount)}",
            style =
                MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFeatureSettings = "tnum",
                ),
        )
    }
}

private fun formatDate(timestamp: Long): String {
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)
    val zoneId = ZoneId.systemDefault()
    val txLocalDate = Instant.ofEpochMilli(timestamp).atZone(zoneId).toLocalDate()

    return when (txLocalDate) {
        today -> "Hari Ini"
        yesterday -> "Kemarin"
        else -> {
            val monthName =
                when (txLocalDate.monthValue) {
                    1 -> "Jan"
                    2 -> "Feb"
                    3 -> "Mar"
                    4 -> "Apr"
                    5 -> "Mei"
                    6 -> "Jun"
                    7 -> "Jul"
                    8 -> "Agt"
                    9 -> "Sep"
                    10 -> "Okt"
                    11 -> "Nov"
                    12 -> "Des"
                    else -> ""
                }
            "${txLocalDate.dayOfMonth} $monthName"
        }
    }
}
