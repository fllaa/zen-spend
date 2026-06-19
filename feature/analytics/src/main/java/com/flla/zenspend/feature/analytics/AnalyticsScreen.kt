@file:Suppress("LongMethod")

package com.flla.zenspend.feature.analytics

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flla.zenspend.core.designsystem.theme.LocalZenSpendSpacing
import com.flla.zenspend.core.designsystem.theme.zenSpendShadowLevel1
import com.flla.zenspend.core.model.User
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AnalyticsRoute(viewModel: AnalyticsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    AnalyticsScreen(
        state = state,
        onPeriodSelected = viewModel::onPeriodSelected,
    )
}

@Composable
fun AnalyticsScreen(
    state: AnalyticsUiState,
    onPeriodSelected: (String) -> Unit,
) {
    val spacing = LocalZenSpendSpacing.current
    val context = LocalContext.current

    Scaffold(
        topBar = {
            AnalyticsHeader(
                user = state.user,
                onProfileClick = {
                    Toast.makeText(context, "Profile clicked", Toast.LENGTH_SHORT).show()
                },
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = spacing.containerPadding, vertical = spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.lg),
        ) {
            // Title & Period Selector
            Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
                Text(
                    text = "Data Statistik",
                    style =
                        MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                )

                PeriodSelector(
                    selectedPeriod = state.selectedPeriod,
                    onPeriodSelected = onPeriodSelected,
                )
            }

            // Bento Summary Grid
            BentoSummaryGrid(
                totalExpense = state.totalExpense,
                dailyAverage = state.dailyAverage,
                largestCategory = state.largestCategory,
            )

            // Spending Trend Bar Chart
            SpendingTrendCard(trends = state.trends)

            // Category Breakdown & Donut Chart
            CategoryBreakdownCard(
                categories = state.categories,
            )

            // Zen Insights
            ZenInsightsSection(insights = state.insights)

            // Extra padding bottom to ensure content scrolls above bottom bar nicely
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AnalyticsHeader(
    user: User?,
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
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
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

        // Avatar
        Box(
            modifier =
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .border(2.dp, MaterialTheme.colorScheme.primaryContainer, CircleShape)
                    .clickable(onClick = onProfileClick),
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

@Composable
private fun PeriodSelector(
    selectedPeriod: String,
    onPeriodSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val periods = listOf("Minggu", "Bulan", "Tahun")
    val spacing = LocalZenSpendSpacing.current

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        periods.forEach { period ->
            val isSelected = period == selectedPeriod
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                Color.Transparent
                            },
                        )
                        .clickable { onPeriodSelected(period) }
                        .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = period,
                    style =
                        MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color =
                                if (isSelected) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                        ),
                )
            }
        }
    }
}

@Composable
private fun BentoSummaryGrid(
    totalExpense: Long,
    dailyAverage: Long,
    largestCategory: String,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalZenSpendSpacing.current
    val darkTheme = isSystemInDarkTheme()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        // Total Expense Card (Main Bento Card)
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
                verticalArrangement = Arrangement.spacedBy(spacing.xs),
            ) {
                Text(
                    text = "TOTAL PENGELUARAN",
                    style =
                        MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp,
                        ),
                )

                Text(
                    text = formatCurrency(totalExpense),
                    style =
                        MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = FontFamily.SansSerif,
                            fontFeatureSettings = "tnum",
                        ),
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 4.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.TrendingUp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = "12% dari bulan lalu",
                        style =
                            MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Medium,
                            ),
                    )
                }
            }
        }

        // Secondary Bento Cards Side-by-Side
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            Card(
                modifier =
                    Modifier
                        .weight(1f)
                        .zenSpendShadowLevel1(darkTheme),
                shape = RoundedCornerShape(16.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    ),
            ) {
                Column(
                    modifier = Modifier.padding(spacing.md),
                    verticalArrangement = Arrangement.spacedBy(spacing.xs),
                ) {
                    Text(
                        text = "Rata-rata Harian",
                        style =
                            MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                    )
                    Text(
                        text = formatCurrency(dailyAverage),
                        style =
                            MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontFeatureSettings = "tnum",
                            ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Card(
                modifier =
                    Modifier
                        .weight(1f)
                        .zenSpendShadowLevel1(darkTheme),
                shape = RoundedCornerShape(16.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    ),
            ) {
                Column(
                    modifier = Modifier.padding(spacing.md),
                    verticalArrangement = Arrangement.spacedBy(spacing.xs),
                ) {
                    Text(
                        text = "Kategori Terbesar",
                        style =
                            MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                    )
                    Text(
                        text = largestCategory,
                        style =
                            MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                            ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun SpendingTrendCard(
    trends: List<MonthlyTrend>,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalZenSpendSpacing.current
    val darkTheme = isSystemInDarkTheme()

    Card(
        modifier =
            modifier
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
            verticalArrangement = Arrangement.spacedBy(spacing.lg),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column {
                    Text(
                        text = "Tren Pengeluaran",
                        style =
                            MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                            ),
                    )
                    Text(
                        text = "Perbandingan 3 Bulan Terakhir",
                        style =
                            MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                    )
                }
                Text(
                    text = "Mei 2024",
                    style =
                        MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                        ),
                )
            }

            // Custom Bar Chart Row
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(horizontal = spacing.md),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom,
            ) {
                trends.forEach { trend ->
                    BarChartColumn(trend = trend)
                }
            }
        }
    }
}

@Composable
private fun BarChartColumn(
    trend: MonthlyTrend,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalZenSpendSpacing.current
    val animatedHeight by animateFloatAsState(
        targetValue = trend.heightPercentage,
        label = "BarHeightAnimation",
    )

    Column(
        modifier = modifier.width(64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.sm, Alignment.Bottom),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (trend.isCurrent) {
                    Box(
                        modifier =
                            Modifier
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                                    RoundedCornerShape(12.dp),
                                )
                                .border(1.dp, MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text = "Sekarang",
                            style =
                                MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                ),
                            textAlign = TextAlign.Center,
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth(0.6f)
                            .height((120 * animatedHeight).dp)
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                            .background(
                                if (trend.isCurrent) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surfaceContainerHighest
                                },
                            ),
                )
            }
        }

        Text(
            text = trend.monthName,
            style =
                MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (trend.isCurrent) FontWeight.Bold else FontWeight.Medium,
                    color =
                        if (trend.isCurrent) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outline
                        },
                ),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun CategoryBreakdownCard(
    categories: List<CategoryBreakdown>,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalZenSpendSpacing.current
    val darkTheme = isSystemInDarkTheme()
    val context = LocalContext.current

    Card(
        modifier =
            modifier
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
            verticalArrangement = Arrangement.spacedBy(spacing.lg),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Rincian Kategori",
                    style =
                        MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                )

                Text(
                    text = "Lihat Semua",
                    style =
                        MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                        ),
                    modifier =
                        Modifier.clickable {
                            Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show()
                        },
                )
            }

            // Donut Chart + Legend Row
            if (categories.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.lg),
                ) {
                    DonutChart(
                        categories = categories,
                        modifier = Modifier.size(96.dp),
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(spacing.xs),
                    ) {
                        categories.take(4).forEach { cat ->
                            val color = getCategoryColor(cat.category)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                            ) {
                                Box(
                                    modifier =
                                        Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(color),
                                )
                                Text(
                                    text = cat.category,
                                    style =
                                        MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        ),
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = "${(cat.percentage * 100).toInt()}%",
                                    style =
                                        MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                        ),
                                )
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "Tidak ada transaksi pengeluaran di periode ini.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }

            // Category progress list
            Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
                categories.forEach { cat ->
                    CategoryItem(categoryBreakdown = cat)
                }
            }
        }
    }
}

@Composable
private fun DonutChart(
    categories: List<CategoryBreakdown>,
    modifier: Modifier = Modifier,
) {
    val colors = categories.map { getCategoryColor(it.category) }

    Canvas(modifier = modifier) {
        var startAngle = -90f
        val strokeWidth = 12.dp.toPx()

        categories.forEachIndexed { index, cat ->
            val sweepAngle = cat.percentage * 360f
            if (sweepAngle > 0) {
                drawArc(
                    color = colors[index],
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                )
                startAngle += sweepAngle
            }
        }
    }
}

@Composable
private fun CategoryItem(
    categoryBreakdown: CategoryBreakdown,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalZenSpendSpacing.current
    val color = getCategoryColor(categoryBreakdown.category)
    val icon = getCategoryIcon(categoryBreakdown.category)

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        // Rounded Icon
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

        // Mid section (Title, Progress bar, and percentage label)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(spacing.xs),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = getCategoryDisplayName(categoryBreakdown.category),
                    style =
                        MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                )
                Text(
                    text = formatCurrency(categoryBreakdown.amount),
                    style =
                        MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFeatureSettings = "tnum",
                        ),
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                LinearProgressIndicator(
                    progress = { categoryBreakdown.budgetProgress },
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                    color = color,
                    trackColor = MaterialTheme.colorScheme.surfaceContainerLow,
                )
                Text(
                    text = "${(categoryBreakdown.budgetProgress * 100).toInt()}% Budget",
                    style =
                        MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium,
                        ),
                )
            }
        }
    }
}

@Composable
private fun ZenInsightsSection(
    insights: List<ZenInsight>,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalZenSpendSpacing.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            modifier = Modifier.padding(bottom = 2.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = "Insight Zen",
                style =
                    MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
            )
        }

        insights.forEach { insight ->
            ZenInsightCard(insight = insight)
        }
    }
}

@Composable
private fun ZenInsightCard(
    insight: ZenInsight,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalZenSpendSpacing.current
    val darkTheme = isSystemInDarkTheme()

    val (accentColor, icon, containerBgColor) =
        when (insight.type) {
            InsightType.WARNING ->
                Triple(
                    MaterialTheme.colorScheme.error,
                    Icons.Rounded.TrendingUp,
                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f),
                )
            InsightType.SUCCESS ->
                Triple(
                    MaterialTheme.colorScheme.tertiary,
                    Icons.Rounded.Lightbulb,
                    MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.15f),
                )
            InsightType.INFO ->
                Triple(
                    MaterialTheme.colorScheme.primary,
                    Icons.Rounded.Savings,
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                )
        }

    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .zenSpendShadowLevel1(darkTheme),
        shape = RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp, topEnd = 16.dp, bottomEnd = 16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            // Colored left border accent
            Box(
                modifier =
                    Modifier
                        .width(4.dp)
                        .height(84.dp)
                        .background(accentColor),
            )

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(spacing.md),
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
                verticalAlignment = Alignment.Top,
            ) {
                // Circular icon
                Box(
                    modifier =
                        Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(containerBgColor),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp),
                    )
                }

                // Info text
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = insight.title,
                        style =
                            MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                            ),
                    )
                    Text(
                        text = insight.description,
                        style =
                            MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                    )
                }
            }
        }
    }
}

// Helpers
private fun formatCurrency(amount: Long): String {
    val numberFormat = NumberFormat.getNumberInstance(Locale("in", "ID"))
    return "Rp ${numberFormat.format(amount)}"
}

@Composable
private fun getCategoryColor(category: String): Color {
    return when (category) {
        "Makanan" -> MaterialTheme.colorScheme.error
        "Pendapatan" -> MaterialTheme.colorScheme.tertiary
        "Transportasi" -> MaterialTheme.colorScheme.primary
        "Kebutuhan", "Belanja" -> MaterialTheme.colorScheme.secondary
        "Utilitas" -> Color(0xFFE65100)
        else -> MaterialTheme.colorScheme.outline
    }
}

private fun getCategoryIcon(category: String): ImageVector {
    return when (category) {
        "Makanan" -> Icons.Rounded.Restaurant
        "Transportasi" -> Icons.Rounded.DirectionsCar
        "Kebutuhan", "Belanja" -> Icons.Rounded.ShoppingBag
        "Utilitas" -> Icons.AutoMirrored.Rounded.ReceiptLong
        else -> Icons.Rounded.Coffee
    }
}

private fun getCategoryDisplayName(category: String): String {
    return when (category) {
        "Makanan" -> "Makanan & Minuman"
        "Kebutuhan" -> "Belanja"
        else -> category
    }
}
