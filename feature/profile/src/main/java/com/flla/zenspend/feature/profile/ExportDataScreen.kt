@file:Suppress("LongMethod", "LongParameterList")

package com.flla.zenspend.feature.profile

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.EventNote
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.EditCalendar
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.TableChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.unit.dp
import com.flla.zenspend.core.designsystem.component.ZenSpendPrimaryButton
import com.flla.zenspend.core.designsystem.component.ZenSpendTopAppBar
import com.flla.zenspend.core.designsystem.theme.LocalZenSpendSpacing
import com.flla.zenspend.core.designsystem.theme.zenSpendShadowLevel1
import com.flla.zenspend.core.ui.ScreenScaffold

@Composable
fun ExportDataScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalZenSpendSpacing.current
    val darkTheme = isSystemInDarkTheme()
    val context = LocalContext.current

    // Local States
    var selectedTimeRange by remember { mutableStateOf("Bulan Ini") }
    var selectedFormat by remember { mutableStateOf("PDF") }

    val accounts = listOf("Dompet Utama", "Bank BCA", "Kartu Kredit")
    val selectedAccounts = remember { mutableStateListOf("Dompet Utama") }

    ScreenScaffold(
        topBar = {
            ZenSpendTopAppBar(
                title = "Ekspor Data",
                onBackClick = onBackClick,
            )
        },
    ) { _ ->
        Box(modifier = modifier.fillMaxSize()) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = spacing.containerPadding)
                        .padding(top = spacing.lg, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(spacing.xl),
            ) {
                // Hero Section / Contextual Visual
                val gradient =
                    Brush.linearGradient(
                        colors =
                            if (darkTheme) {
                                listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.surfaceContainer,
                                )
                            } else {
                                listOf(
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
                                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                                )
                            },
                    )
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .zenSpendShadowLevel1(darkTheme)
                            .clip(RoundedCornerShape(16.dp))
                            .background(gradient)
                            .padding(spacing.md),
                    contentAlignment = Alignment.BottomStart,
                ) {
                    Text(
                        text = "Amankan catatan finansial Anda dengan mengekspor data transaksi.",
                        style =
                            MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color =
                                    if (darkTheme) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    },
                            ),
                    )
                }

                // Section: Pilih Rentang Waktu
                Column(
                    verticalArrangement = Arrangement.spacedBy(spacing.md),
                ) {
                    Text(
                        text = "Pilih Rentang Waktu",
                        style =
                            MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                            ),
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                            TimeRangeButton(
                                icon = Icons.Rounded.CalendarToday,
                                title = "Bulan Ini",
                                isSelected = selectedTimeRange == "Bulan Ini",
                                onClick = { selectedTimeRange = "Bulan Ini" },
                                modifier = Modifier.weight(1f),
                            )
                            TimeRangeButton(
                                icon = Icons.Rounded.DateRange,
                                title = "3 Bulan Terakhir",
                                isSelected = selectedTimeRange == "3 Bulan Terakhir",
                                onClick = { selectedTimeRange = "3 Bulan Terakhir" },
                                modifier = Modifier.weight(1f),
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                            TimeRangeButton(
                                icon = Icons.AutoMirrored.Rounded.EventNote,
                                title = "Tahun Ini",
                                isSelected = selectedTimeRange == "Tahun Ini",
                                onClick = { selectedTimeRange = "Tahun Ini" },
                                modifier = Modifier.weight(1f),
                            )
                            TimeRangeButton(
                                icon = Icons.Rounded.EditCalendar,
                                title = "Kustom",
                                isSelected = selectedTimeRange == "Kustom",
                                onClick = { selectedTimeRange = "Kustom" },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }

                // Section: Pilih Format
                Column(
                    verticalArrangement = Arrangement.spacedBy(spacing.md),
                ) {
                    Text(
                        text = "Pilih Format",
                        style =
                            MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                            ),
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                        FormatOptionRow(
                            title = "PDF (Laporan Visual)",
                            subtitle = "Grafik dan ringkasan lengkap",
                            icon = Icons.Rounded.PictureAsPdf,
                            iconContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                            iconTint = MaterialTheme.colorScheme.error,
                            isSelected = selectedFormat == "PDF",
                            onClick = { selectedFormat = "PDF" },
                        )
                        FormatOptionRow(
                            title = "CSV (Spreadsheet)",
                            subtitle = "Data mentah untuk pengolahan",
                            icon = Icons.Rounded.TableChart,
                            iconContainerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f),
                            iconTint = MaterialTheme.colorScheme.tertiary,
                            isSelected = selectedFormat == "CSV",
                            onClick = { selectedFormat = "CSV" },
                        )
                    }
                }

                // Section: Pilih Rekening
                Column(
                    verticalArrangement = Arrangement.spacedBy(spacing.md),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Pilih Rekening",
                            style =
                                MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                ),
                        )
                        TextButton(
                            onClick = {
                                if (selectedAccounts.size == accounts.size) {
                                    selectedAccounts.clear()
                                } else {
                                    selectedAccounts.clear()
                                    selectedAccounts.addAll(accounts)
                                }
                            },
                        ) {
                            Text(
                                text = if (selectedAccounts.size == accounts.size) "Batalkan Semua" else "Pilih Semua",
                                style =
                                    MaterialTheme.typography.labelLarge.copy(
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold,
                                    ),
                            )
                        }
                    }

                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    ) {
                        accounts.forEach { account ->
                            val icon =
                                when (account) {
                                    "Dompet Utama" -> Icons.Rounded.AccountBalanceWallet
                                    "Bank BCA" -> Icons.Rounded.AccountBalance
                                    else -> Icons.Rounded.CreditCard
                                }
                            val isSelected = selectedAccounts.contains(account)
                            AccountChip(
                                icon = icon,
                                title = account,
                                isSelected = isSelected,
                                onClick = {
                                    if (isSelected) {
                                        selectedAccounts.remove(account)
                                    } else {
                                        selectedAccounts.add(account)
                                    }
                                },
                            )
                        }
                    }
                }
            }

            // Fixed/Sticky Bottom Action Container
            Box(
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                        .padding(spacing.containerPadding),
            ) {
                ZenSpendPrimaryButton(
                    text = "Ekspor Sekarang",
                    onClick = {
                        Toast.makeText(
                            context,
                            "Mengekspor data ke $selectedFormat...",
                            Toast.LENGTH_SHORT,
                        ).show()
                    },
                )
            }
        }
    }
}

@Composable
private fun TimeRangeButton(
    icon: ImageVector,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalZenSpendSpacing.current
    val darkTheme = isSystemInDarkTheme()

    val containerColor =
        if (isSelected) {
            MaterialTheme.colorScheme.secondaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainerLowest
        }

    val contentColor =
        if (isSelected) {
            MaterialTheme.colorScheme.onSecondaryContainer
        } else {
            MaterialTheme.colorScheme.onSurface
        }

    val iconColor =
        if (isSelected) {
            MaterialTheme.colorScheme.onSecondaryContainer
        } else {
            MaterialTheme.colorScheme.primary
        }

    val borderStroke =
        if (isSelected) {
            BorderStroke(1.5.dp, MaterialTheme.colorScheme.primaryContainer)
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        }

    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .zenSpendShadowLevel1(darkTheme)
                .clip(RoundedCornerShape(16.dp))
                .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        border = borderStroke,
        colors = CardDefaults.cardColors(containerColor = containerColor),
    ) {
        Column(
            modifier =
                Modifier
                    .padding(spacing.md)
                    .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(spacing.xs),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp),
            )
            Text(
                text = title,
                style =
                    MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = contentColor,
                    ),
            )
        }
    }
}

@Composable
private fun FormatOptionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconContainerColor: Color,
    iconTint: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val spacing = LocalZenSpendSpacing.current
    val darkTheme = isSystemInDarkTheme()

    val containerColor =
        if (isSelected) {
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
        } else {
            MaterialTheme.colorScheme.surfaceContainerLowest
        }

    val borderStroke =
        if (isSelected) {
            BorderStroke(1.5.dp, MaterialTheme.colorScheme.primaryContainer)
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        }

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .zenSpendShadowLevel1(darkTheme)
                .clip(RoundedCornerShape(16.dp))
                .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        border = borderStroke,
        colors = CardDefaults.cardColors(containerColor = containerColor),
    ) {
        Row(
            modifier =
                Modifier
                    .padding(spacing.md)
                    .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
                modifier = Modifier.weight(1f),
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(iconContainerColor),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp),
                    )
                }
                Column {
                    Text(
                        text = title,
                        style =
                            MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                            ),
                    )
                    Text(
                        text = subtitle,
                        style =
                            MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                    )
                }
            }
            Icon(
                imageVector = if (isSelected) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Composable
private fun AccountChip(
    icon: ImageVector,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val spacing = LocalZenSpendSpacing.current
    val darkTheme = isSystemInDarkTheme()

    val containerColor =
        if (isSelected) {
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
        } else {
            MaterialTheme.colorScheme.surfaceContainerLowest
        }

    val contentColor =
        if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }

    val borderStroke =
        if (isSelected) {
            BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        }

    Card(
        modifier =
            Modifier
                .zenSpendShadowLevel1(darkTheme)
                .clip(RoundedCornerShape(24.dp))
                .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        border = borderStroke,
        colors = CardDefaults.cardColors(containerColor = containerColor),
    ) {
        Row(
            modifier =
                Modifier
                    .padding(horizontal = spacing.md, vertical = spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.xs),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = title,
                style =
                    MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = contentColor,
                    ),
            )
        }
    }
}
