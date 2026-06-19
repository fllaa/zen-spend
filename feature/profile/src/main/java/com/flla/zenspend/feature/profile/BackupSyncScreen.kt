@file:Suppress("LongMethod", "LongParameterList")

package com.flla.zenspend.feature.profile

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Autorenew
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.CloudSync
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flla.zenspend.core.designsystem.component.ZenSpendPrimaryButton
import com.flla.zenspend.core.designsystem.component.ZenSpendSecondaryButton
import com.flla.zenspend.core.designsystem.component.ZenSpendTopAppBar
import com.flla.zenspend.core.designsystem.theme.LocalZenSpendSpacing
import com.flla.zenspend.core.designsystem.theme.zenSpendShadowLevel1
import com.flla.zenspend.core.ui.ScreenScaffold

@Composable
fun BackupSyncScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalZenSpendSpacing.current
    val darkTheme = isSystemInDarkTheme()
    val context = LocalContext.current

    // Local state for account connection and switches
    var isConnected by remember { mutableStateOf(true) }
    var autoSyncEnabled by remember { mutableStateOf(true) }
    var wifiOnlyEnabled by remember { mutableStateOf(true) }

    ScreenScaffold(
        topBar = {
            ZenSpendTopAppBar(
                title = "Cadangan & Sinkronisasi",
                onBackClick = onBackClick,
            )
        },
    ) { _ ->
        Column(
            modifier =
                modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = spacing.containerPadding, vertical = spacing.lg),
            verticalArrangement = Arrangement.spacedBy(spacing.lg),
        ) {
            // 1. Status Terkini Card
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
                            .padding(spacing.md),
                    verticalArrangement = Arrangement.spacedBy(spacing.md),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "STATUS TERKINI",
                            style =
                                MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 1.sp,
                                ),
                        )

                        // Badge representation of Connection Status
                        val badgeBgColor =
                            if (isConnected) {
                                MaterialTheme.colorScheme.tertiaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        val badgeTextColor =
                            if (isConnected) {
                                MaterialTheme.colorScheme.onTertiaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        val dotColor =
                            if (isConnected) {
                                MaterialTheme.colorScheme.tertiary
                            } else {
                                MaterialTheme.colorScheme.outline
                            }

                        Box(
                            modifier =
                                Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(badgeBgColor)
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Box(
                                    modifier =
                                        Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(dotColor),
                                )
                                Text(
                                    text = if (isConnected) "Terhubung" else "Terputus",
                                    style =
                                        MaterialTheme.typography.labelSmall.copy(
                                            color = badgeTextColor,
                                            fontWeight = FontWeight.Bold,
                                        ),
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.md),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CloudSync,
                            contentDescription = null,
                            tint =
                                if (isConnected) {
                                    MaterialTheme.colorScheme.tertiary
                                } else {
                                    MaterialTheme.colorScheme.outline
                                },
                            modifier = Modifier.size(24.dp),
                        )
                        Text(
                            text =
                                buildAnnotatedString {
                                    append("Terakhir sinkronisasi: ")
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                                        append(if (isConnected) "Hari ini, 10:45" else "Tidak ada sinkronisasi aktif")
                                    }
                                },
                            style =
                                MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                ),
                        )
                    }
                }
            }

            // 2. Akun Sinkronisasi Section
            Column(
                verticalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                Text(
                    text = "Akun Sinkronisasi",
                    style =
                        MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
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
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(spacing.md),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
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
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Cloud,
                                    contentDescription = "Google Drive Logo",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp),
                                )
                            }

                            Column {
                                Text(
                                    text = "Google Drive",
                                    style =
                                        MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                        ),
                                )
                                Text(
                                    text = if (isConnected) "budi.santoso@gmail.com" else "Belum terhubung",
                                    style =
                                        MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        ),
                                )
                            }
                        }

                        if (isConnected) {
                            OutlinedButton(
                                onClick = {
                                    isConnected = false
                                    Toast.makeText(
                                        context,
                                        "Koneksi Google Drive diputuskan",
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                },
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                colors =
                                    ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error,
                                    ),
                            ) {
                                Text(
                                    text = "Putuskan",
                                    style =
                                        MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.error,
                                        ),
                                )
                            }
                        } else {
                            OutlinedButton(
                                onClick = {
                                    isConnected = true
                                    Toast.makeText(
                                        context,
                                        "Menghubungkan ke Google Drive...",
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                },
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                                colors =
                                    ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.primary,
                                    ),
                            ) {
                                Text(
                                    text = "Hubungkan",
                                    style =
                                        MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                        ),
                                )
                            }
                        }
                    }
                }
            }

            // 3. Pengaturan Cadangan Section
            Column(
                verticalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                Text(
                    text = "Pengaturan Cadangan",
                    style =
                        MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
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
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Toggle Item 1: Sinkronisasi Otomatis
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
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Autorenew,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp),
                                    )
                                }
                                Text(
                                    text = "Sinkronisasi Otomatis",
                                    style =
                                        MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                        ),
                                )
                            }

                            Switch(
                                checked = autoSyncEnabled,
                                onCheckedChange = { autoSyncEnabled = it },
                                colors =
                                    SwitchDefaults.colors(
                                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                                        uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                                    ),
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = spacing.md),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                        )

                        // Toggle Item 2: Hanya via Wi-Fi
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
                                        imageVector = Icons.Rounded.Wifi,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(20.dp),
                                    )
                                }
                                Text(
                                    text = "Hanya via Wi-Fi",
                                    style =
                                        MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                        ),
                                )
                            }

                            Switch(
                                checked = wifiOnlyEnabled,
                                onCheckedChange = { wifiOnlyEnabled = it },
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
                }
            }

            // 4. Cadangkan Manual Section
            Column(
                verticalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                Text(
                    text = "Cadangkan Manual",
                    style =
                        MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                    modifier = Modifier.padding(horizontal = spacing.sm),
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    ZenSpendPrimaryButton(
                        text = "Cadangkan Sekarang",
                        onClick = {
                            if (isConnected) {
                                Toast.makeText(
                                    context,
                                    "Mencadangkan data ke Google Drive...",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Silakan hubungkan akun Google Drive terlebih dahulu",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        },
                    )

                    ZenSpendSecondaryButton(
                        text = "Pulihkan Data",
                        onClick = {
                            if (isConnected) {
                                Toast.makeText(
                                    context,
                                    "Memulai pemulihan data dari Google Drive...",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Silakan hubungkan akun Google Drive terlebih dahulu",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        },
                    )
                }
            }

            // 5. Info Footer
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.sm, vertical = spacing.xs),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = "Data Anda dienkripsi dan disimpan dengan aman di Google Drive pribadi Anda.",
                    style =
                        MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            fontStyle = FontStyle.Italic,
                        ),
                )
            }
        }
    }
}
