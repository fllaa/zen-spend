package com.flla.zenspend.core.designsystem.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ZenSpendSpacing(
    val base: Dp = 8.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 24.dp,
    val xl: Dp = 32.dp,
    val containerPadding: Dp = 20.dp,
    val gutter: Dp = 16.dp,
)

val LocalZenSpendSpacing = staticCompositionLocalOf { ZenSpendSpacing() }
