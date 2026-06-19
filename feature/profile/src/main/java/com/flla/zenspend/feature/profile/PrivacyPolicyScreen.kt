@file:Suppress("LongMethod", "LongParameterList", "SwallowedException", "MaxLineLength")

package com.flla.zenspend.feature.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ContactSupport
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DataObject
import androidx.compose.material.icons.rounded.EnhancedEncryption
import androidx.compose.material.icons.rounded.Mail
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flla.zenspend.core.designsystem.component.ZenSpendTopAppBar
import com.flla.zenspend.core.designsystem.theme.LocalZenSpendSpacing
import com.flla.zenspend.core.designsystem.theme.NumericDataTextStyle
import com.flla.zenspend.core.designsystem.theme.zenSpendShadowLevel1
import com.flla.zenspend.core.ui.ScreenScaffold

@Composable
fun PrivacyPolicyScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalZenSpendSpacing.current
    val darkTheme = isSystemInDarkTheme()
    val context = LocalContext.current

    ScreenScaffold(
        topBar = {
            ZenSpendTopAppBar(
                title = "Kebijakan Privasi",
                onBackClick = onBackClick,
            )
        },
    ) { _ ->
        Box(
            modifier =
                modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = spacing.containerPadding, vertical = spacing.lg),
                verticalArrangement = Arrangement.spacedBy(spacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Header: Illustration & Last Updated
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.VerifiedUser,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(32.dp),
                        )
                    }
                    Spacer(modifier = Modifier.height(spacing.xs))
                    Text(
                        text = "TERAKHIR DIPERBARUI",
                        style =
                            MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.sp,
                            ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    )
                    Text(
                        text = "24 Mei 2024",
                        style =
                            NumericDataTextStyle.copy(
                                fontWeight = FontWeight.Bold,
                            ),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                // Privacy Policy Content Card
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
                                .padding(spacing.lg),
                        verticalArrangement = Arrangement.spacedBy(spacing.lg),
                    ) {
                        // Section 1: Informasi yang Kami Kumpulkan
                        Column(
                            verticalArrangement = Arrangement.spacedBy(spacing.md),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.DataObject,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp),
                                )
                                Text(
                                    text = "Informasi yang Kami Kumpulkan",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Text(
                                text =
                                    "Kami mengumpulkan informasi yang Anda berikan secara langsung " +
                                        "saat Anda mendaftarkan akun, mencatat transaksi, atau " +
                                        "menghubungi tim dukungan kami.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Column(
                                verticalArrangement = Arrangement.spacedBy(spacing.sm),
                                modifier = Modifier.padding(start = spacing.xs),
                            ) {
                                val listItems =
                                    listOf(
                                        "Informasi Identitas: Nama, alamat email, dan preferensi akun.",
                                        "Data Finansial: Catatan pengeluaran, kategori anggaran, dan riwayat " +
                                            "transaksi yang Anda masukkan secara manual.",
                                        "Data Penggunaan: Informasi tentang bagaimana Anda berinteraksi dengan " +
                                            "aplikasi untuk membantu kami meningkatkan pengalaman pengguna.",
                                    )
                                listItems.forEach { item ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(spacing.md),
                                        verticalAlignment = Alignment.Top,
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.CheckCircle,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier =
                                                Modifier
                                                    .padding(top = 2.dp)
                                                    .size(16.dp),
                                        )
                                        Text(
                                            text = item,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                            }
                        }

                        // Section 2: Bagaimana Kami Menggunakan Data
                        Column(
                            verticalArrangement = Arrangement.spacedBy(spacing.md),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Psychology,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp),
                                )
                                Text(
                                    text = "Bagaimana Kami Menggunakan Data",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Text(
                                text =
                                    "Data Anda digunakan semata-mata untuk memberikan pengalaman " +
                                        "pengelolaan keuangan terbaik. ZenSpend tidak menjual informasi " +
                                        "pribadi Anda kepada pihak ketiga.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(spacing.md),
                            ) {
                                Column(
                                    modifier =
                                        Modifier
                                            .weight(1f)
                                            .background(
                                                color = MaterialTheme.colorScheme.surfaceContainerLow,
                                                shape = RoundedCornerShape(12.dp),
                                            )
                                            .border(
                                                width = 1.dp,
                                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                                shape = RoundedCornerShape(12.dp),
                                            )
                                            .padding(spacing.md),
                                    verticalArrangement = Arrangement.spacedBy(spacing.xs),
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Analytics,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp),
                                    )
                                    Text(
                                        text = "ANALISIS",
                                        style =
                                            MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 1.sp,
                                            ),
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                    Text(
                                        text = "Menyediakan wawasan pengeluaran dan grafik visualisasi data Anda.",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                Column(
                                    modifier =
                                        Modifier
                                            .weight(1f)
                                            .background(
                                                color = MaterialTheme.colorScheme.surfaceContainerLow,
                                                shape = RoundedCornerShape(12.dp),
                                            )
                                            .border(
                                                width = 1.dp,
                                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                                shape = RoundedCornerShape(12.dp),
                                            )
                                            .padding(spacing.md),
                                    verticalArrangement = Arrangement.spacedBy(spacing.xs),
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Notifications,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp),
                                    )
                                    Text(
                                        text = "PENGINGAT",
                                        style =
                                            MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 1.sp,
                                            ),
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                    Text(
                                        text =
                                            "Mengirimkan notifikasi anggaran sesuai dengan " +
                                                "pengaturan yang Anda buat.",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }

                        // Section 3: Keamanan Data
                        Column(
                            verticalArrangement = Arrangement.spacedBy(spacing.md),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Security,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp),
                                )
                                Text(
                                    text = "Keamanan Data",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Text(
                                text =
                                    "Keamanan finansial Anda adalah prioritas utama kami. Kami menerapkan " +
                                        "enkripsi tingkat industri untuk melindungi data sensitif Anda " +
                                        "dari akses yang tidak sah.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(12.dp),
                                        )
                                        .padding(spacing.md),
                                horizontalArrangement = Arrangement.spacedBy(spacing.md),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier =
                                        Modifier
                                            .size(40.dp)
                                            .background(
                                                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                                                shape = RoundedCornerShape(8.dp),
                                            ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.EnhancedEncryption,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.tertiary,
                                        modifier = Modifier.size(24.dp),
                                    )
                                }
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(2.dp),
                                ) {
                                    Text(
                                        text = "Enkripsi End-to-End",
                                        style =
                                            NumericDataTextStyle.copy(
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.SemiBold,
                                            ),
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                    Text(
                                        text = "Data Anda dienkripsi saat transit dan saat disimpan.",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }

                        // Section 4: Hubungi Kami
                        Column(
                            verticalArrangement = Arrangement.spacedBy(spacing.md),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ContactSupport,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp),
                                )
                                Text(
                                    text = "Hubungi Kami",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Text(
                                text =
                                    "Jika Anda memiliki pertanyaan tentang Kebijakan Privasi ini, " +
                                        "silakan hubungi petugas perlindungan data kami melalui:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Button(
                                onClick = {
                                    val intent =
                                        Intent(Intent.ACTION_SENDTO).apply {
                                            data = Uri.parse("mailto:privacy@zenspend.com")
                                        }
                                    try {
                                        context.startActivity(intent)
                                    } catch (e: android.content.ActivityNotFoundException) {
                                        android.widget.Toast.makeText(
                                            context,
                                            "Aplikasi email tidak ditemukan",
                                            android.widget.Toast.LENGTH_SHORT,
                                        ).show()
                                    }
                                },
                                shape = RoundedCornerShape(24.dp),
                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary,
                                    ),
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Mail,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                    )
                                    Text(
                                        text = "privacy@zenspend.com",
                                        style = MaterialTheme.typography.labelSmall,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
