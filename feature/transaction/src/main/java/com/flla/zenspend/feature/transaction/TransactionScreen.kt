package com.flla.zenspend.feature.transaction

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Backspace
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.LaptopMac
import androidx.compose.material.icons.rounded.MedicalServices
import androidx.compose.material.icons.rounded.MilitaryTech
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material.icons.rounded.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flla.zenspend.core.designsystem.theme.LocalZenSpendSpacing
import com.flla.zenspend.core.designsystem.theme.zenSpendShadowLevel1
import com.flla.zenspend.core.designsystem.theme.zenSpendShadowLevel2
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@Composable
fun TransactionRoute(
    category: String,
    isIncome: Boolean,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TransactionViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(category, isIncome) {
        viewModel.initialize(category, isIncome)
    }

    LaunchedEffect(uiState.isSaveSuccess) {
        if (uiState.isSaveSuccess) {
            onSaveSuccess()
        }
    }

    TransactionScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onDigitClick = viewModel::appendDigit,
        onDeleteClick = viewModel::deleteDigit,
        onAccountSelect = viewModel::selectAccount,
        onDateChange = viewModel::updateDate,
        onNoteChange = viewModel::updateNote,
        onSaveClick = viewModel::saveTransaction,
        modifier = modifier,
    )
}

@Composable
fun TransactionScreen(
    uiState: TransactionUiState,
    onBackClick: () -> Unit,
    onDigitClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onAccountSelect: (String) -> Unit,
    onDateChange: (Long) -> Unit,
    onNoteChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalZenSpendSpacing.current
    val context = LocalContext.current
    val categoryStyle = getCategoryStyle(uiState.category, uiState.isIncome)

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = spacing.containerPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }

                Text(
                    text = "Tambah Transaksi",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(32.dp))
                        .background(categoryStyle.containerColor)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Icon(
                        imageVector = categoryStyle.icon,
                        contentDescription = uiState.category,
                        tint = categoryStyle.color,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(spacing.xs))
                    Text(
                        text = uiState.category,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = categoryStyle.color,
                    )
                }
            }
        },
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 320.dp), // Height allowance for sticky numpad area
        ) {
            // Amount Input Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.containerPadding)
                    .padding(top = spacing.md, bottom = spacing.lg)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f))
                    .padding(vertical = spacing.lg),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (uiState.isIncome) "Jumlah Pemasukan" else "Jumlah Pengeluaran",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    )
                    Spacer(modifier = Modifier.height(spacing.xs))
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Rp ",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                            modifier = Modifier.padding(bottom = 6.dp),
                        )
                        Text(
                            text = formatRupiah(uiState.amount),
                            style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    if (uiState.amountErrorMessage != null) {
                        Spacer(modifier = Modifier.height(spacing.xs))
                        Text(
                            text = uiState.amountErrorMessage,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }

            // Form Fields
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.containerPadding),
                verticalArrangement = Arrangement.spacedBy(spacing.lg),
            ) {
                // Account Selector
                Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                    Text(
                        text = "Pilih Akun",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = spacing.xs),
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    ) {
                        AccountOptionItem(
                            name = "Tunai",
                            icon = Icons.Rounded.Payments,
                            isSelected = uiState.selectedAccount == "Tunai",
                            onClick = { onAccountSelect("Tunai") }
                        )
                        AccountOptionItem(
                            name = "BCA",
                            icon = Icons.Rounded.AccountBalance,
                            isSelected = uiState.selectedAccount == "BCA",
                            onClick = { onAccountSelect("BCA") }
                        )
                        AccountOptionItem(
                            name = "GoPay",
                            icon = Icons.Rounded.Wallet,
                            isSelected = uiState.selectedAccount == "GoPay",
                            onClick = { onAccountSelect("GoPay") }
                        )
                    }
                }

                // Date Selector
                Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                    Text(
                        text = "Tanggal",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = spacing.xs),
                    )
                    val date = Instant.ofEpochMilli(uiState.dateEpochMillis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("id", "ID"))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .zenSpendShadowLevel1()
                            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                            .clickable {
                                showDatePicker(context, date) { year, month, day ->
                                    val calendar = Calendar.getInstance()
                                    calendar.set(year, month, day)
                                    onDateChange(calendar.timeInMillis)
                                }
                            }
                            .padding(horizontal = spacing.md),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = date.format(formatter),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Icon(
                                imageVector = Icons.Rounded.EditNote,
                                contentDescription = "Pilih Tanggal",
                                tint = MaterialTheme.colorScheme.outline,
                            )
                        }
                    }
                }

                // Note
                Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                    Text(
                        text = "Catatan",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = spacing.xs),
                    )
                    OutlinedTextField(
                        value = uiState.note,
                        onValueChange = onNoteChange,
                        placeholder = {
                            Text(
                                text = "Makan siang di kantor",
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .zenSpendShadowLevel1(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                            focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            unfocusedBorderColor = Color.Transparent,
                        ),
                    )
                }

                // Visual decoration: Zen State indicator
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Zen State Enabled",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 2.sp
                        ),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        // Sticky keypad & save button
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .zenSpendShadowLevel2()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                    .padding(top = spacing.md, bottom = spacing.containerPadding)
                    .padding(horizontal = spacing.containerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Keypad grid
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        KeypadButton(text = "1", onClick = { onDigitClick("1") }, modifier = Modifier.weight(1f))
                        KeypadButton(text = "2", onClick = { onDigitClick("2") }, modifier = Modifier.weight(1f))
                        KeypadButton(text = "3", onClick = { onDigitClick("3") }, modifier = Modifier.weight(1f))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        KeypadButton(text = "4", onClick = { onDigitClick("4") }, modifier = Modifier.weight(1f))
                        KeypadButton(text = "5", onClick = { onDigitClick("5") }, modifier = Modifier.weight(1f))
                        KeypadButton(text = "6", onClick = { onDigitClick("6") }, modifier = Modifier.weight(1f))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        KeypadButton(text = "7", onClick = { onDigitClick("7") }, modifier = Modifier.weight(1f))
                        KeypadButton(text = "8", onClick = { onDigitClick("8") }, modifier = Modifier.weight(1f))
                        KeypadButton(text = "9", onClick = { onDigitClick("9") }, modifier = Modifier.weight(1f))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        KeypadButton(text = "000", onClick = { onDigitClick("000") }, modifier = Modifier.weight(1f))
                        KeypadButton(text = "0", onClick = { onDigitClick("0") }, modifier = Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .clickable(onClick = onDeleteClick),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Backspace,
                                contentDescription = "Hapus",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(spacing.md))

                // Save button
                Button(
                    onClick = onSaveClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .zenSpendShadowLevel1(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    shape = RoundedCornerShape(28.dp),
                    enabled = !uiState.isSaving,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (uiState.isSaving) "Menyimpan..." else "Simpan",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Spacer(modifier = Modifier.width(spacing.sm))
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = "Simpan",
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AccountOptionItem(
    name: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalZenSpendSpacing.current
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
    val textColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    val baseModifier = modifier
        .clip(RoundedCornerShape(16.dp))
        .border(
            width = 1.dp,
            color = borderColor,
            shape = RoundedCornerShape(16.dp),
        )
        .background(MaterialTheme.colorScheme.surfaceContainerLowest)
        .clickable(onClick = onClick)
        .padding(horizontal = spacing.md, vertical = 12.dp)

    Row(
        modifier = if (isSelected) baseModifier.zenSpendShadowLevel1() else baseModifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = name,
            tint = textColor,
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(spacing.sm))
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = textColor,
        )
    }
}

@Composable
private fun KeypadButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

private fun formatRupiah(amountStr: String): String {
    val parsed = amountStr.toLongOrNull() ?: 0L
    val formatter = NumberFormat.getInstance(Locale("id", "ID"))
    return formatter.format(parsed)
}

private fun showDatePicker(
    context: android.content.Context,
    currentDate: LocalDate,
    onDateSet: (year: Int, month: Int, day: Int) -> Unit,
) {
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            onDateSet(year, month, dayOfMonth)
        },
        currentDate.year,
        currentDate.monthValue - 1,
        currentDate.dayOfMonth
    ).show()
}

private data class CategoryStyle(
    val icon: ImageVector,
    val color: Color,
    val containerColor: Color
)

private fun getCategoryStyle(category: String, isIncome: Boolean): CategoryStyle {
    return if (isIncome) {
        when (category) {
            "Gaji" -> CategoryStyle(Icons.Rounded.AccountBalanceWallet, Color(0xFF006064), Color(0xFFE0F2F1))
            "Bonus" -> CategoryStyle(Icons.Rounded.MilitaryTech, Color(0xFFF57F17), Color(0xFFFFFDE7))
            "Freelance" -> CategoryStyle(Icons.Rounded.LaptopMac, Color(0xFF006064), Color(0xFFE0F7FA))
            "Investasi" -> CategoryStyle(Icons.Rounded.TrendingUp, Color(0xFF1A237E), Color(0xFFE8EAF6))
            "Hadiah" -> CategoryStyle(Icons.Rounded.CardGiftcard, Color(0xFF880E4F), Color(0xFFFCE4EC))
            else -> CategoryStyle(Icons.Rounded.Add, Color(0xFF37474F), Color(0xFFECEFF1))
        }
    } else {
        when (category) {
            "Makan" -> CategoryStyle(Icons.Rounded.Restaurant, Color(0xFFE65100), Color(0xFFFFF3E0))
            "Transport" -> CategoryStyle(Icons.Rounded.DirectionsCar, Color(0xFF0D47A1), Color(0xFFE3F2FD))
            "Belanja" -> CategoryStyle(Icons.Rounded.ShoppingBag, Color(0xFF4A148C), Color(0xFFF3E5F5))
            "Tagihan" -> CategoryStyle(Icons.AutoMirrored.Rounded.ReceiptLong, Color(0xFFB71C1C), Color(0xFFFFEBEE))
            "Kesehatan" -> CategoryStyle(Icons.Rounded.MedicalServices, Color(0xFF1B5E20), Color(0xFFE8F5E9))
            "Hiburan" -> CategoryStyle(Icons.Rounded.Movie, Color(0xFF004D40), Color(0xFFE0F2F1))
            "Edukasi" -> CategoryStyle(Icons.Rounded.School, Color(0xFFFF8F00), Color(0xFFFFFDE7))
            else -> CategoryStyle(Icons.Rounded.MoreHoriz, Color(0xFF37474F), Color(0xFFECEFF1))
        }
    }
}
