package com.flla.zenspend.feature.onboarding

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.TrendingDown
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.flla.zenspend.core.designsystem.theme.LocalZenSpendSpacing
import com.flla.zenspend.core.ui.collectUiState
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun OnboardingRoute(
    onCompleted: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectUiState()
    LaunchedEffect(state.hasCompleted) {
        if (state.hasCompleted) {
            onCompleted()
        }
    }

    OnboardingScreen(
        onCompleted = viewModel::completeOnboarding,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Suppress("LongMethod")
@Composable
fun OnboardingScreen(onCompleted: () -> Unit) {
    val spacing = LocalZenSpendSpacing.current
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = spacing.md),
                contentAlignment = Alignment.CenterEnd,
            ) {
                TextButton(
                    onClick = onCompleted,
                    modifier = Modifier.testTag("onboarding_skip"),
                ) {
                    Text(
                        text = "Lewati",
                        style =
                            MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                    )
                }
            }
        },
        bottomBar = {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.lg)
                        .padding(bottom = spacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.lg),
            ) {
                // Page Indicator
                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    repeat(3) { index ->
                        val active = pagerState.currentPage == index
                        val width by animateDpAsState(
                            targetValue = if (active) 24.dp else 8.dp,
                            label = "indicator_width",
                        )
                        Box(
                            modifier =
                                Modifier
                                    .size(width = width, height = 8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        color =
                                            if (active) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.outlineVariant
                                            },
                                    ),
                        )
                    }
                }

                // Next Button
                Button(
                    onClick = {
                        if (pagerState.currentPage < 2) {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            onCompleted()
                        }
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("onboarding_next"),
                    shape = RoundedCornerShape(28.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                        ),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.animateContentSize(),
                    ) {
                        Text(
                            text = if (pagerState.currentPage == 2) "Mulai Sekarang" else "Lanjut",
                            style =
                                MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                ),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                            contentDescription = null,
                        )
                    }
                }
            }
        },
    ) { padding ->
        val primaryColor = MaterialTheme.colorScheme.primary
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .drawBehind {
                        // Soft Japanese minimalist background glow
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
                                    colors = listOf(Color(0xFF1B7F5C).copy(alpha = 0.03f), Color.Transparent),
                                    center = Offset(size.width * 0.2f, size.height * 0.8f),
                                    radius = size.minDimension * 0.7f,
                                ),
                        )
                    },
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Spacer(modifier = Modifier.height(spacing.md))

                    // Illustration Container
                    Box(
                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        when (page) {
                            0 -> SlideOneIllustration()
                            1 -> SlideTwoIllustration()
                            2 -> SlideThreeIllustration()
                        }
                    }

                    // Text Content Container
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(spacing.md),
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(bottom = spacing.xl),
                    ) {
                        val title =
                            when (page) {
                                0 -> "Catat Segalanya"
                                1 -> "Kendalikan Anggaran Anda"
                                else -> "Pahami Keuangan Anda"
                            }
                        val subtitle =
                            when (page) {
                                0 ->
                                    "Pantau pemasukan dan pengeluaran Anda dari tunai, " +
                                        "bank, hingga dompet digital dalam satu tempat yang tenang."
                                1 ->
                                    "Pantau pengeluaran Anda berdasarkan kategori untuk " +
                                        "melihat ke mana uang Anda mengalir setiap bulan."
                                else ->
                                    "Dapatkan visualisasi cerdas dan laporan mendalam " +
                                        "tentang ke mana uang Anda mengalir setiap bulannya secara otomatis."
                            }

                        Text(
                            text = title,
                            style =
                                MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    letterSpacing = (-0.5).sp,
                                ),
                            textAlign = TextAlign.Center,
                        )

                        Text(
                            text = subtitle,
                            style =
                                MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 24.sp,
                                ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = spacing.md),
                        )
                    }
                }
            }
        }
    }
}

@Suppress("LongMethod")
@Composable
private fun SlideOneIllustration() {
    val infiniteTransition = rememberInfiniteTransition(label = "slide_1_float")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(3000, easing = LinearEasing),
                repeatMode = androidx.compose.animation.core.RepeatMode.Reverse,
            ),
        label = "main_card_float",
    )
    val floatY1 by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(2500, easing = LinearEasing),
                repeatMode = androidx.compose.animation.core.RepeatMode.Reverse,
            ),
        label = "float_1",
    )
    val floatY2 by infiniteTransition.animateFloat(
        initialValue = 5f,
        targetValue = -5f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(2800, easing = LinearEasing),
                repeatMode = androidx.compose.animation.core.RepeatMode.Reverse,
            ),
        label = "float_2",
    )

    Box(
        modifier =
            Modifier
                .size(280.dp)
                .aspectRatio(1f),
    ) {
        // Blur background blob
        Box(
            modifier =
                Modifier
                    .size(200.dp)
                    .align(Alignment.Center)
                    .blur(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        shape = CircleShape,
                    ),
        )

        // Main Device Mock/Card
        Card(
            modifier =
                Modifier
                    .width(160.dp)
                    .height(240.dp)
                    .align(Alignment.Center)
                    .offset { IntOffset(0, offsetY.roundToInt()) }
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(24.dp),
                    ),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Device Header bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .width(40.dp)
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    )
                    Box(
                        modifier =
                            Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    )
                }

                // Balance display area
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = "Total Saldo",
                        style =
                            MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                    )
                    Text(
                        text = "Rp 12.450.000",
                        style =
                            MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                            ),
                    )
                }

                // Mini Transaction items
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxHeight(),
                ) {
                    repeat(2) { index ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier =
                                    Modifier
                                        .size(24.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            if (index == 0) {
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                            } else {
                                                Color(0xFF1B7F5C).copy(alpha = 0.1f)
                                            },
                                        ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector =
                                        if (index == 0) {
                                            Icons.Rounded.AccountBalanceWallet
                                        } else {
                                            Icons.Rounded.Payments
                                        },
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint =
                                        if (index == 0) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            Color(0xFF1B7F5C)
                                        },
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                            ) {
                                Box(
                                    modifier =
                                        Modifier
                                            .width(48.dp)
                                            .height(6.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)),
                                )
                                Box(
                                    modifier =
                                        Modifier
                                            .width(28.dp)
                                            .height(4.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)),
                                )
                            }
                        }
                    }
                }
            }
        }

        // Floating Badges
        // E-Wallet (Top Right)
        Card(
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .offset { IntOffset((-8).dp.roundToPx(), (32 + floatY1).dp.roundToPx()) }
                    .border(
                        width = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp),
                    ),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        ) {
            Row(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.AccountBalanceWallet,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = "E-Wallet",
                    style =
                        MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        ),
                )
            }
        }

        // Tunai (Bottom Left)
        Card(
            modifier =
                Modifier
                    .align(Alignment.BottomStart)
                    .offset { IntOffset((8).dp.roundToPx(), (-32 + floatY2).dp.roundToPx()) }
                    .border(
                        width = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp),
                    ),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        ) {
            Row(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Payments,
                    contentDescription = null,
                    tint = Color(0xFF1B7F5C),
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = "Tunai",
                    style =
                        MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B7F5C),
                        ),
                )
            }
        }
    }
}

@Suppress("LongMethod")
@Composable
private fun SlideTwoIllustration() {
    val infiniteTransition = rememberInfiniteTransition(label = "slide_2_float")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(2800, easing = LinearEasing),
                repeatMode = androidx.compose.animation.core.RepeatMode.Reverse,
            ),
        label = "categories_card_float",
    )

    Box(
        modifier =
            Modifier
                .size(280.dp)
                .aspectRatio(1f),
    ) {
        // Blur background blob
        Box(
            modifier =
                Modifier
                    .size(200.dp)
                    .align(Alignment.Center)
                    .blur(48.dp)
                    .background(
                        color = Color(0xFF1B7F5C).copy(alpha = 0.06f),
                        shape = CircleShape,
                    ),
        )

        // Conceptual Categories List Card
        Card(
            modifier =
                Modifier
                    .width(200.dp)
                    .height(180.dp)
                    .align(Alignment.Center)
                    .offset { IntOffset(0, offsetY.roundToInt()) }
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(24.dp),
                    ),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                verticalArrangement = Arrangement.SpaceAround,
            ) {
                // Category Progress List
                CategoryProgressRow(
                    title = "Makanan",
                    percentage = 0.75f,
                    icon = Icons.Rounded.Restaurant,
                    iconColor = MaterialTheme.colorScheme.primary,
                )
                CategoryProgressRow(
                    title = "Transportasi",
                    percentage = 0.40f,
                    icon = Icons.Rounded.DirectionsCar,
                    iconColor = Color(0xFF1B7F5C),
                )
                CategoryProgressRow(
                    title = "Belanja",
                    percentage = 0.90f,
                    icon = Icons.Rounded.ShoppingBag,
                    iconColor = MaterialTheme.colorScheme.error,
                )
            }
        }

        // Floating Trend Indicator Badge
        Box(
            modifier =
                Modifier
                    .size(40.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = (-24).dp, y = 28.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .border(0.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.TrendingDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun CategoryProgressRow(
    title: String,
    percentage: Float,
    icon: ImageVector,
    iconColor: Color,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(iconColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = iconColor,
                    )
                }
                Text(
                    text = title,
                    style =
                        MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                )
            }
            Text(
                text = "${(percentage * 100).roundToInt()}%",
                style =
                    MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
            )
        }
        LinearProgressIndicator(
            progress = { percentage },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
            color = iconColor,
            trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
        )
    }
}

@Suppress("LongMethod")
@Composable
private fun SlideThreeIllustration() {
    val infiniteTransition = rememberInfiniteTransition(label = "slide_3_float")
    val barHeightFactor1 by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.05f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = androidx.compose.animation.core.RepeatMode.Reverse,
            ),
        label = "bar_1",
    )
    val barHeightFactor2 by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(2200, easing = LinearEasing),
                repeatMode = androidx.compose.animation.core.RepeatMode.Reverse,
            ),
        label = "bar_2",
    )
    val barHeightFactor3 by infiniteTransition.animateFloat(
        initialValue = 0.90f,
        targetValue = 1.10f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(2400, easing = LinearEasing),
                repeatMode = androidx.compose.animation.core.RepeatMode.Reverse,
            ),
        label = "bar_3",
    )

    Box(
        modifier =
            Modifier
                .size(280.dp)
                .aspectRatio(1f),
    ) {
        // Blur background blob
        Box(
            modifier =
                Modifier
                    .size(200.dp)
                    .align(Alignment.Center)
                    .blur(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                        shape = CircleShape,
                    ),
        )

        // Conceptual Minimal Graph Bars
        Row(
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            // Bar 1
            Box(
                modifier =
                    Modifier
                        .width(44.dp)
                        .height((50 * barHeightFactor1).dp)
                        .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
            )

            // Bar 2 (Active/Focused)
            Box(
                modifier =
                    Modifier
                        .width(44.dp)
                        .height((110 * barHeightFactor2).dp)
                        .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .drawBehind {
                            // Ambient shadow / glow
                        },
                contentAlignment = Alignment.TopCenter,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Insights,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier =
                        Modifier
                            .padding(top = 12.dp)
                            .size(18.dp),
                )
            }

            // Bar 3
            Box(
                modifier =
                    Modifier
                        .width(44.dp)
                        .height((75 * barHeightFactor3).dp)
                        .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
            )
        }

        // Floating Insight Tag Card
        Card(
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-8).dp, y = 24.dp)
                    .border(
                        width = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp),
                    ),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        ) {
            Row(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1B7F5C).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.TrendingUp,
                        contentDescription = null,
                        tint = Color(0xFF1B7F5C),
                        modifier = Modifier.size(14.dp),
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = "INSIGHT",
                        style =
                            MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 1.sp,
                            ),
                    )
                    Text(
                        text = "Tabungan Naik",
                        style =
                            MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                            ),
                    )
                }
            }
        }
    }
}
