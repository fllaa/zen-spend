package com.flla.zenspend.core.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.LaptopMac
import androidx.compose.material.icons.rounded.MedicalServices
import androidx.compose.material.icons.rounded.MilitaryTech
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.flla.zenspend.core.designsystem.theme.LocalZenSpendSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongMethod")
@Composable
fun CategorySelectionBottomSheet(
    onDismissRequest: () -> Unit,
    onCategorySelected: (categoryName: String, isIncome: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
) {
    val spacing = LocalZenSpendSpacing.current
    var isIncomeSelected by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val expenseCategories =
        remember {
            listOf(
                CategoryItem("Makan", Icons.Rounded.Restaurant, Color(0xFFE65100), Color(0xFFFFF3E0)),
                CategoryItem("Transport", Icons.Rounded.DirectionsCar, Color(0xFF0D47A1), Color(0xFFE3F2FD)),
                CategoryItem("Belanja", Icons.Rounded.ShoppingBag, Color(0xFF4A148C), Color(0xFFF3E5F5)),
                CategoryItem("Tagihan", Icons.AutoMirrored.Rounded.ReceiptLong, Color(0xFFB71C1C), Color(0xFFFFEBEE)),
                CategoryItem("Kesehatan", Icons.Rounded.MedicalServices, Color(0xFF1B5E20), Color(0xFFE8F5E9)),
                CategoryItem("Hiburan", Icons.Rounded.Movie, Color(0xFF004D40), Color(0xFFE0F2F1)),
                CategoryItem("Edukasi", Icons.Rounded.School, Color(0xFFFF8F00), Color(0xFFFFFDE7)),
                CategoryItem("Lainnya", Icons.Rounded.MoreHoriz, Color(0xFF37474F), Color(0xFFECEFF1)),
            )
        }

    val incomeCategories =
        remember {
            listOf(
                CategoryItem("Gaji", Icons.Rounded.AccountBalanceWallet, Color(0xFF006064), Color(0xFFE0F2F1)),
                CategoryItem("Bonus", Icons.Rounded.MilitaryTech, Color(0xFFF57F17), Color(0xFFFFFDE7)),
                CategoryItem("Freelance", Icons.Rounded.LaptopMac, Color(0xFF006064), Color(0xFFE0F7FA)),
                CategoryItem("Investasi", Icons.Rounded.TrendingUp, Color(0xFF1A237E), Color(0xFFE8EAF6)),
                CategoryItem("Hadiah", Icons.Rounded.CardGiftcard, Color(0xFF880E4F), Color(0xFFFCE4EC)),
                CategoryItem("Lainnya", Icons.Rounded.Add, Color(0xFF37474F), Color(0xFFECEFF1)),
            )
        }

    val currentCategories = if (isIncomeSelected) incomeCategories else expenseCategories
    val filteredCategories =
        remember(currentCategories, searchQuery) {
            if (searchQuery.isBlank()) {
                currentCategories
            } else {
                currentCategories.filter { it.name.contains(searchQuery, ignoreCase = true) }
            }
        }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(
                modifier =
                    Modifier
                        .padding(vertical = spacing.md)
                        .size(width = 48.dp, height = 6.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
            )
        },
        modifier = modifier.testTag("category_selection_bottom_sheet"),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.containerPadding)
                    .padding(bottom = spacing.xl),
        ) {
            Text(
                text = "Pilih Kategori",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = spacing.md),
            )

            // Tab Selector
            CategoryTabRow(
                isIncomeSelected = isIncomeSelected,
                onTabSelected = {
                    isIncomeSelected = it
                    searchQuery = "" // Reset search when switching tabs
                },
                modifier = Modifier.padding(bottom = spacing.md),
            )

            // Search Bar
            CategorySearchTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.padding(bottom = spacing.lg),
            )

            // Categories Grid
            if (filteredCategories.isEmpty()) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Kategori tidak ditemukan",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    verticalArrangement = Arrangement.spacedBy(spacing.lg),
                    horizontalArrangement = Arrangement.spacedBy(spacing.xs),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                ) {
                    items(filteredCategories) { category ->
                        CategoryGridItem(
                            item = category,
                            onClick = {
                                onCategorySelected(category.name, isIncomeSelected)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryTabRow(
    isIncomeSelected: Boolean,
    onTabSelected: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TabItem(
            text = "Pengeluaran",
            selected = !isIncomeSelected,
            onClick = { onTabSelected(false) },
            modifier = Modifier.weight(1f),
        )
        TabItem(
            text = "Pemasukan",
            selected = isIncomeSelected,
            onClick = { onTabSelected(true) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun TabItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        label = "containerColor",
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.outline,
        label = "contentColor",
    )
    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(32.dp))
                .background(containerColor)
                .clickable(onClick = onClick)
                .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = contentColor,
        )
    }
}

@Composable
private fun CategorySearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = "Cari kategori...",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors =
            TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
    )
}

@Composable
private fun CategoryGridItem(
    item: CategoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalZenSpendSpacing.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier =
            modifier
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = onClick)
                .padding(vertical = spacing.xs),
    ) {
        Box(
            modifier =
                Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(item.containerColor),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.name,
                tint = item.color,
                modifier = Modifier.size(28.dp),
            )
        }
        Spacer(modifier = Modifier.height(spacing.xs))
        Text(
            text = item.name,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
