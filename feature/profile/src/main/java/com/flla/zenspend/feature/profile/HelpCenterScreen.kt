@file:Suppress("LongMethod", "LongParameterList")

package com.flla.zenspend.feature.profile

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Chat
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Mail
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flla.zenspend.core.designsystem.component.ZenSpendTopAppBar
import com.flla.zenspend.core.designsystem.theme.LocalZenSpendSpacing
import com.flla.zenspend.core.designsystem.theme.zenSpendShadowLevel1
import com.flla.zenspend.core.ui.ScreenScaffold

private data class FaqItem(
    val question: String,
    val answer: String,
)

@Composable
fun HelpCenterScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalZenSpendSpacing.current
    val darkTheme = isSystemInDarkTheme()
    val context = LocalContext.current

    var searchQuery by remember { mutableStateOf("") }
    var expandedItems by remember { mutableStateOf(setOf<Int>()) }

    val faqList =
        remember {
            listOf(
                FaqItem(
                    question = "Bagaimana cara menyinkronkan rekening bank?",
                    answer =
                        "Buka menu 'Data' lalu pilih 'Tambah Sumber'. Ikuti instruksi keamanan untuk " +
                            "menghubungkan akun bank Anda secara real-time melalui enkripsi AES-256.",
                ),
                FaqItem(
                    question = "Apakah data finansial saya aman?",
                    answer =
                        "Keamanan Anda adalah prioritas kami. ZenSpend menggunakan standar perbankan " +
                            "kelas dunia dengan otentikasi dua faktor dan pemantauan ancaman 24/7.",
                ),
                FaqItem(
                    question = "Cara mengekspor laporan bulanan?",
                    answer =
                        "Masuk ke tab 'Analytics', klik ikon export di sudut kanan atas, dan pilih " +
                            "format file (PDF atau CSV) yang Anda butuhkan.",
                ),
            )
        }

    val filteredFaqs =
        remember(searchQuery) {
            if (searchQuery.isBlank()) {
                faqList
            } else {
                faqList.filter {
                    it.question.contains(searchQuery, ignoreCase = true) ||
                        it.answer.contains(searchQuery, ignoreCase = true)
                }
            }
        }

    ScreenScaffold(
        topBar = {
            ZenSpendTopAppBar(
                title = "Pusat Bantuan",
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
            // Search Input Bar
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .zenSpendShadowLevel1(darkTheme),
                placeholder = {
                    Text(
                        text = "Cari topik bantuan...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                trailingIcon =
                    if (searchQuery.isNotEmpty()) {
                        {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "Clear search",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    } else {
                        null
                    },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors =
                    TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    ),
            )

            // Category Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                val categories =
                    listOf(
                        Triple("Akun & Keamanan", Icons.Rounded.Security, "keamanan"),
                        Triple("Transaksi", Icons.AutoMirrored.Rounded.ReceiptLong, "transaksi"),
                        Triple("Anggaran & Laporan", Icons.AutoMirrored.Rounded.TrendingUp, "anggaran"),
                    )
                categories.forEach { (title, icon, filterWord) ->
                    Card(
                        modifier =
                            Modifier
                                .weight(1f)
                                .clickable {
                                    Toast.makeText(context, "Kategori: $title", Toast.LENGTH_SHORT).show()
                                    searchQuery = filterWord
                                }
                                .zenSpendShadowLevel1(darkTheme),
                        shape = RoundedCornerShape(16.dp),
                        colors =
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                            ),
                    ) {
                        Column(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = spacing.md, horizontal = spacing.sm),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Box(
                                modifier =
                                    Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.secondaryContainer),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(24.dp),
                                )
                            }
                            Spacer(modifier = Modifier.height(spacing.sm))
                            Text(
                                text = title,
                                style =
                                    MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center,
                                    ),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 2,
                                modifier = Modifier.heightIn(min = 36.dp),
                            )
                        }
                    }
                }
            }

            // Pertanyaan Populer FAQ Accordion section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Bolt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp),
                    )
                    Text(
                        text = "Pertanyaan Populer",
                        style =
                            MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                            ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(spacing.base),
                ) {
                    filteredFaqs.forEachIndexed { index, faq ->
                        val isExpanded = expandedItems.contains(index)
                        Card(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .animateContentSize()
                                    .zenSpendShadowLevel1(darkTheme),
                            shape = RoundedCornerShape(16.dp),
                            colors =
                                CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                                ),
                        ) {
                            Column(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            expandedItems =
                                                if (isExpanded) {
                                                    expandedItems - index
                                                } else {
                                                    expandedItems + index
                                                }
                                        }
                                        .padding(spacing.md),
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = faq.question,
                                        style =
                                            MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = FontWeight.Medium,
                                            ),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f),
                                    )
                                    val rotationState by animateFloatAsState(
                                        targetValue = if (isExpanded) 180f else 0f,
                                        label = "rotation",
                                    )
                                    Icon(
                                        imageVector = Icons.Rounded.ExpandMore,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.rotate(rotationState),
                                    )
                                }
                                if (isExpanded) {
                                    Spacer(modifier = Modifier.height(spacing.sm))
                                    Text(
                                        text = faq.answer,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                    if (filteredFaqs.isEmpty()) {
                        Column(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = spacing.lg),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(spacing.sm),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                                modifier = Modifier.size(48.dp),
                            )
                            Text(
                                text = "Topik tidak ditemukan",
                                style =
                                    MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Medium,
                                    ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            // Support Actions Card
            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .zenSpendShadowLevel1(darkTheme),
                shape = RoundedCornerShape(24.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(spacing.md),
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(spacing.xs),
                    ) {
                        Text(
                            text = "Masih butuh bantuan?",
                            style =
                                MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = "Tim kami siap membantu Anda menyelesaikan masalah finansial Anda.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center,
                        )
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(spacing.sm),
                    ) {
                        // Chat Button (Primary style)
                        Button(
                            onClick = {
                                Toast.makeText(context, "Membuka Chat...", Toast.LENGTH_SHORT).show()
                            },
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                            shape = RoundedCornerShape(28.dp),
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    contentColor = MaterialTheme.colorScheme.primary,
                                ),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Chat,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(modifier = Modifier.width(spacing.sm))
                            Text(
                                text = "Chat dengan Kami",
                                style =
                                    MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                    ),
                            )
                        }

                        // Email Button (Secondary style)
                        OutlinedButton(
                            onClick = {
                                Toast.makeText(context, "Membuka Email...", Toast.LENGTH_SHORT).show()
                            },
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                            shape = RoundedCornerShape(28.dp),
                            border =
                                BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f),
                                ),
                            colors =
                                ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                ),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Mail,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(modifier = Modifier.width(spacing.sm))
                            Text(
                                text = "Kirim Email",
                                style =
                                    MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                    ),
                            )
                        }
                    }
                }
            }

            // Asymmetric Bento Section
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                // Left Bento Card (Komunitas Pengguna)
                Card(
                    modifier =
                        Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable {
                                Toast.makeText(context, "Membuka Komunitas...", Toast.LENGTH_SHORT).show()
                            },
                    shape = RoundedCornerShape(24.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        ),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(spacing.md),
                        contentAlignment = Alignment.BottomStart,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Groups,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f),
                            modifier =
                                Modifier
                                    .size(112.dp)
                                    .align(Alignment.TopEnd)
                                    .offset(x = 16.dp, y = (-16).dp),
                        )
                        Text(
                            text = "Komunitas Pengguna",
                            style =
                                MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                ),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(bottom = spacing.xs),
                        )
                    }
                }

                // Right Bento Card (Panduan Lengkap)
                Card(
                    modifier =
                        Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable {
                                Toast.makeText(context, "Membuka Panduan...", Toast.LENGTH_SHORT).show()
                            },
                    shape = RoundedCornerShape(24.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        ),
                ) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(spacing.md),
                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.MenuBook,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.size(28.dp),
                        )
                        Text(
                            text = "Panduan Lengkap",
                            style =
                                MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                ),
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                        )
                    }
                }
            }
        }
    }
}
