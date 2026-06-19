package com.flla.zenspend.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Flight
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LaptopMac
import androidx.compose.material.icons.rounded.MedicalServices
import androidx.compose.material.icons.rounded.MilitaryTech
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.SportsSoccer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.flla.zenspend.core.designsystem.component.ZenSpendPrimaryButton
import com.flla.zenspend.core.designsystem.component.ZenSpendTextField
import com.flla.zenspend.core.designsystem.component.ZenSpendTopAppBar
import com.flla.zenspend.core.designsystem.theme.LocalZenSpendSpacing
import com.flla.zenspend.core.designsystem.theme.zenSpendShadowLevel1
import com.flla.zenspend.core.model.Category
import com.flla.zenspend.core.ui.collectUiState
import kotlinx.coroutines.launch

@Composable
fun CategoriesRoute(
    onBackClick: () -> Unit,
    viewModel: CategoriesViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectUiState()
    CategoriesScreen(
        state = state,
        onBackClick = onBackClick,
        onTabSelected = viewModel::selectTab,
        onSaveCategory = viewModel::saveCategory,
    )
}

@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    state: CategoriesUiState,
    onBackClick: () -> Unit,
    onTabSelected: (Boolean) -> Unit,
    onSaveCategory: (name: String, iconName: String, colorHex: String, isIncome: Boolean, isPrimary: Boolean) -> Unit,
) {
    val spacing = LocalZenSpendSpacing.current
    var showAddCategorySheet by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        onTabSelected(pagerState.currentPage == 1)
    }

    Scaffold(
        topBar = {
            ZenSpendTopAppBar(
                title = "Kategori Transaksi",
                onBackClick = onBackClick,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddCategorySheet = true },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(16.dp),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Tambah Kategori",
                    )
                    Text(
                        text = "Tambah",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                    )
                }
            }
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding),
        ) {
            // Tab Header (Expense vs Income)
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
            ) {
                Tab(
                    selected = pagerState.currentPage == 0,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    },
                    text = { Text("Pengeluaran") },
                )
                Tab(
                    selected = pagerState.currentPage == 1,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
                    text = { Text("Pemasukan") },
                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                val isIncomePage = page == 1
                val filteredCategories = state.categories.filter { it.isIncome == isIncomePage }

                if (filteredCategories.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Belum ada kategori.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding =
                            PaddingValues(
                                horizontal = spacing.containerPadding,
                                vertical = spacing.lg,
                            ),
                        horizontalArrangement = Arrangement.spacedBy(spacing.md),
                        verticalArrangement = Arrangement.spacedBy(spacing.md),
                    ) {
                        items(filteredCategories) { category ->
                            CategoryCard(category = category)
                        }
                    }
                }
            }
        }

        if (showAddCategorySheet) {
            AddCategoryBottomSheet(
                onDismissRequest = { showAddCategorySheet = false },
                onSave = { name, iconName, colorHex, isPrimary ->
                    onSaveCategory(name, iconName, colorHex, pagerState.currentPage == 1, isPrimary)
                    showAddCategorySheet = false
                },
            )
        }
    }
}

@Composable
fun CategoryCard(
    category: Category,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalZenSpendSpacing.current
    val darkTheme = isSystemInDarkTheme()
    val parsedColor = parseHexColor(category.colorHex)

    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .zenSpendShadowLevel1(darkTheme)
                .clip(RoundedCornerShape(16.dp)),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(parsedColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = getIconByName(category.iconName),
                    contentDescription = category.name,
                    tint = parsedColor,
                    modifier = Modifier.size(32.dp),
                )
            }
            Spacer(modifier = Modifier.height(spacing.sm))
            Text(
                text = category.name,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryBottomSheet(
    onDismissRequest: () -> Unit,
    onSave: (name: String, iconName: String, colorHex: String, isPrimary: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalZenSpendSpacing.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var categoryName by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("restaurant") }
    var selectedColorHex by remember { mutableStateOf("#E65100") }
    var isPrimary by remember { mutableStateOf(false) }
    var showColorPickerDialog by remember { mutableStateOf(false) }

    val icons =
        remember {
            listOf(
                "restaurant",
                "directions_car",
                "shopping_bag",
                "receipt_long",
                "medical_services",
                "movie",
                "school",
                "favorite",
                "sports_soccer",
                "flight",
                "home",
                "savings",
                "account_balance_wallet",
                "card_giftcard",
            )
        }

    val presetColors =
        remember {
            listOf(
                "#E65100",
                "#0D47A1",
                "#4A148C",
                "#B71C1C",
                "#1B5E20",
                "#004D40",
            )
        }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        dragHandle = {
            Box(
                modifier =
                    Modifier
                        .padding(vertical = spacing.md)
                        .size(width = 48.dp, height = 6.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
            )
        },
        modifier = modifier,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.gutter)
                    .padding(bottom = spacing.xl)
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(spacing.lg),
        ) {
            // Header
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.align(Alignment.CenterStart),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
                Text(
                    text = "Tambah Kategori",
                    style =
                        MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            // Category Name Input
            Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                Text(
                    text = "Nama Kategori",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                ZenSpendTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = "e.g., Hobi, Keluarga",
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // Icon Picker
            Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Pilih Ikon",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(spacing.md),
                ) {
                    icons.forEach { iconName ->
                        val isSelected = selectedIcon == iconName
                        Box(
                            modifier =
                                Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) {
                                            MaterialTheme.colorScheme.primaryContainer
                                        } else {
                                            MaterialTheme.colorScheme.surfaceContainerHighest
                                        },
                                    )
                                    .clickable { selectedIcon = iconName },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = getIconByName(iconName),
                                contentDescription = iconName,
                                tint =
                                    if (isSelected) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                            )
                        }
                    }
                }
            }

            // Color Picker
            Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Warna Kategori",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "Lihat Semua",
                        style =
                            MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                            ),
                        modifier = Modifier.clickable { showColorPickerDialog = true },
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    presetColors.forEach { colorHex ->
                        val color = parseHexColor(colorHex)
                        val isSelected = selectedColorHex.equals(colorHex, ignoreCase = true)
                        Box(
                            modifier =
                                Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (isSelected) 3.dp else 0.dp,
                                        color =
                                            if (isSelected) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                Color.Transparent
                                            },
                                        shape = CircleShape,
                                    )
                                    .clickable { selectedColorHex = colorHex },
                        )
                    }
                }
            }

            // Toggle Primary
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .padding(spacing.md)
                        .clickable { isPrimary = !isPrimary },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Kategori Utama",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "Tampilkan di halaman ringkasan",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(
                    checked = isPrimary,
                    onCheckedChange = { isPrimary = it },
                )
            }

            // Save Button
            ZenSpendPrimaryButton(
                text = "Simpan",
                onClick = {
                    if (categoryName.isNotBlank()) {
                        onSave(categoryName, selectedIcon, selectedColorHex, isPrimary)
                    }
                },
                enabled = categoryName.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    if (showColorPickerDialog) {
        CustomColorPickerDialog(
            initialColorHex = selectedColorHex,
            onColorSelected = {
                selectedColorHex = it
                showColorPickerDialog = false
            },
            onDismissRequest = { showColorPickerDialog = false },
        )
    }
}

@Suppress("LongMethod")
@Composable
fun CustomColorPickerDialog(
    initialColorHex: String,
    onColorSelected: (String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val spacing = LocalZenSpendSpacing.current
    var hexInput by remember { mutableStateOf(initialColorHex) }

    val fullColorGrid =
        remember {
            listOf(
                "#FF8A80",
                "#FF5252",
                "#FF1744",
                "#D50000",
                "#FF4081",
                "#F50057",
                "#C51162",
                "#E040FB",
                "#D500F9",
                "#AA00FF",
                "#7C4DFF",
                "#651FFF",
                "#6200EA",
                "#536DFE",
                "#3D5AFE",
                "#304FFE",
                "#448AFF",
                "#2979FF",
                "#2962FF",
                "#40C4FF",
            )
        }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                ),
            modifier = Modifier.padding(spacing.md),
        ) {
            Column(
                modifier = Modifier.padding(spacing.lg),
                verticalArrangement = Arrangement.spacedBy(spacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Pilih Warna",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                )

                // 4x5 Color Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    modifier = Modifier.height(180.dp),
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    verticalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    items(fullColorGrid) { colorHex ->
                        val color = parseHexColor(colorHex)
                        val isSelected = hexInput.equals(colorHex, ignoreCase = true)
                        Box(
                            modifier =
                                Modifier
                                    .aspectRatio(1f)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (isSelected) 3.dp else 0.dp,
                                        color =
                                            if (isSelected) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                Color.Transparent
                                            },
                                        shape = CircleShape,
                                    )
                                    .clickable { hexInput = colorHex },
                        )
                    }
                }

                // Custom Hex Input
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(spacing.xs),
                ) {
                    Text(
                        text = "Custom Hex",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    ZenSpendTextField(
                        value = hexInput,
                        onValueChange = { hexInput = it },
                        label = "e.g., #E0F2F1",
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onDismissRequest) {
                        Text(
                            text = "Batal",
                            style =
                                MaterialTheme.typography.labelLarge.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold,
                                ),
                        )
                    }
                    Spacer(modifier = Modifier.width(spacing.md))
                    IconButton(
                        onClick = {
                            if (hexInput.isNotBlank()) {
                                var formattedHex = hexInput.trim()
                                if (!formattedHex.startsWith("#")) {
                                    formattedHex = "#$formattedHex"
                                }
                                onColorSelected(formattedHex)
                            }
                        },
                    ) {
                        Text(
                            text = "Pilih",
                            style =
                                MaterialTheme.typography.labelLarge.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold,
                                ),
                        )
                    }
                }
            }
        }
    }
}

@Suppress("TooGenericExceptionCaught", "SwallowedException")
fun parseHexColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        Color(0xFF37474F) // Default fallback color
    }
}

@Suppress("CyclomaticComplexMethod")
fun getIconByName(name: String): ImageVector {
    return when (name.lowercase()) {
        "restaurant", "fastfood" -> Icons.Rounded.Restaurant
        "directions_car" -> Icons.Rounded.DirectionsCar
        "shopping_bag" -> Icons.Rounded.ShoppingBag
        "receipt_long", "receipt" -> Icons.AutoMirrored.Rounded.ReceiptLong
        "medical_services" -> Icons.Rounded.MedicalServices
        "movie" -> Icons.Rounded.Movie
        "school" -> Icons.Rounded.School
        "account_balance_wallet" -> Icons.Rounded.AccountBalanceWallet
        "military_tech" -> Icons.Rounded.MilitaryTech
        "laptop_mac" -> Icons.Rounded.LaptopMac
        "trending_up" -> Icons.AutoMirrored.Rounded.TrendingUp
        "card_giftcard" -> Icons.Rounded.CardGiftcard
        "favorite" -> Icons.Rounded.Favorite
        "sports_soccer" -> Icons.Rounded.SportsSoccer
        "flight" -> Icons.Rounded.Flight
        "home" -> Icons.Rounded.Home
        "savings" -> Icons.Rounded.Savings
        "add" -> Icons.Rounded.Add
        else -> Icons.Rounded.MoreHoriz
    }
}
