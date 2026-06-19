package com.flla.zenspend.feature.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.Waves
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.flla.zenspend.core.designsystem.component.ZenSpendPrimaryButton
import com.flla.zenspend.core.designsystem.component.ZenSpendSecondaryButton
import com.flla.zenspend.core.designsystem.component.ZenSpendTopAppBar
import com.flla.zenspend.core.designsystem.theme.LocalZenSpendSpacing
import com.flla.zenspend.core.designsystem.theme.zenSpendShadowLevel1
import com.flla.zenspend.core.model.BudgetPeriod
import com.flla.zenspend.core.model.Category
import com.flla.zenspend.core.ui.collectUiState

@Composable
fun BudgetRoute(
    onBackClick: () -> Unit,
    viewModel: BudgetViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectUiState()
    BudgetScreen(
        state = state,
        onBackClick = onBackClick,
        onSaveBudget = viewModel::saveBudget,
        onDeleteBudget = viewModel::deleteBudget,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongMethod", "ComplexMethod")
@Composable
fun BudgetScreen(
    state: BudgetUiState,
    onBackClick: () -> Unit,
    onSaveBudget: (String, Long, BudgetPeriod) -> Unit,
    onDeleteBudget: (String) -> Unit,
) {
    val spacing = LocalZenSpendSpacing.current
    val darkTheme = isSystemInDarkTheme()
    val context = LocalContext.current

    var showAddBudgetSheet by remember { mutableStateOf(false) }
    var selectedBudgetForEdit by remember { mutableStateOf<CategoryBudgetInfo?>(null) }

    Scaffold(
        topBar = {
            ZenSpendTopAppBar(
                title = "Atur Anggaran",
                onBackClick = onBackClick,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (state.availableCategories.isEmpty()) {
                        Toast.makeText(
                            context,
                            "Semua kategori pengeluaran sudah memiliki anggaran!",
                            Toast.LENGTH_SHORT,
                        ).show()
                    } else {
                        selectedBudgetForEdit = null
                        showAddBudgetSheet = true
                    }
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = if (darkTheme) MaterialTheme.colorScheme.onPrimaryContainer else Color.White,
                shape = RoundedCornerShape(16.dp),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Tambah Anggaran",
                    )
                    Text(
                        text = "Tambah",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                    )
                }
            }
        },
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = spacing.containerPadding, vertical = spacing.lg),
                verticalArrangement = Arrangement.spacedBy(spacing.lg),
            ) {
                // Monthly Summary Overview Card
                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .zenSpendShadowLevel1(darkTheme)
                            .clip(RoundedCornerShape(16.dp)),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                ) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(spacing.lg),
                        verticalArrangement = Arrangement.spacedBy(spacing.md),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top,
                        ) {
                            Column {
                                Text(
                                    text = "Sisa Anggaran Total",
                                    style =
                                        MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                                            fontWeight = FontWeight.Medium,
                                        ),
                                )
                                Text(
                                    text = formatRupiah(state.totalRemainingAmount),
                                    style =
                                        MaterialTheme.typography.headlineLarge.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 32.sp,
                                        ),
                                )
                            }
                            Box(
                                modifier =
                                    Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White.copy(alpha = 0.2f))
                                        .padding(8.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Savings,
                                    contentDescription = "Wallet",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = "Terpakai (${(state.totalSpentPercentage * 100).toInt()}%)",
                                    style =
                                        MaterialTheme.typography.labelMedium.copy(
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f),
                                        ),
                                )
                                Text(
                                    text = "Total: ${formatRupiah(state.totalBudgetLimit)}",
                                    style =
                                        MaterialTheme.typography.labelMedium.copy(
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f),
                                        ),
                                )
                            }
                            LinearProgressIndicator(
                                progress = { state.totalSpentPercentage },
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(CircleShape),
                                color = Color.White,
                                trackColor = Color.White.copy(alpha = 0.2f),
                                strokeCap = StrokeCap.Round,
                            )
                        }
                    }
                }

                // Grid of category budgets
                if (state.categoryBudgets.isEmpty()) {
                    Card(
                        modifier =
                            Modifier
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
                                    .padding(spacing.lg),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(spacing.sm),
                        ) {
                            Text(
                                text = "Belum Ada Anggaran",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = "Atur anggaran belanja untuk melacak pengeluaran Anda dengan lebih bijak.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                            )
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
                        state.categoryBudgets.forEach { budgetInfo ->
                            BudgetCard(
                                budgetInfo = budgetInfo,
                                darkTheme = darkTheme,
                                spacing = spacing,
                                onClick = {
                                    selectedBudgetForEdit = budgetInfo
                                    showAddBudgetSheet = true
                                },
                            )
                        }
                    }
                }

                // Tip Card
                val warningBudget =
                    state.categoryBudgets
                        .firstOrNull { it.spentPercentage > 0.85f }
                val tipText =
                    if (warningBudget != null) {
                        "Pengeluaran ${warningBudget.category.name} hampir mencapai batas " +
                            "(${(warningBudget.spentPercentage * 100).toInt()}%). Coba kurangi " +
                            "frekuensi pengeluaran untuk menjaga keseimbangan keuanganmu bulan ini."
                    } else {
                        "Semua anggaran Anda dalam keadaan stabil dan terkendali. " +
                            "Pertahankan kebiasaan hemat ini untuk ketenangan pikiran finansial Anda!"
                    }

                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp)),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        ),
                    border =
                        androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                        ),
                ) {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(spacing.md),
                        horizontalArrangement = Arrangement.spacedBy(spacing.md),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Lightbulb,
                            contentDescription = "Zen Tip",
                            tint = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(24.dp),
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                            Text(
                                text = "Tips Zen",
                                style =
                                    MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                    ),
                            )
                            Text(
                                text = tipText,
                                style =
                                    MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                                    ),
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddBudgetSheet) {
        AddBudgetBottomSheet(
            onDismissRequest = { showAddBudgetSheet = false },
            availableCategories = state.availableCategories,
            onSaveBudget = onSaveBudget,
            onDeleteBudget = onDeleteBudget,
            editingBudget = selectedBudgetForEdit,
        )
    }
}

@Suppress("LongMethod")
@Composable
fun BudgetCard(
    budgetInfo: CategoryBudgetInfo,
    darkTheme: Boolean,
    spacing: com.flla.zenspend.core.designsystem.theme.ZenSpendSpacing,
    onClick: () -> Unit,
) {
    val categoryColor = parseHexColor(budgetInfo.category.colorHex)
    val indicatorColor =
        if (budgetInfo.spentPercentage > 0.85f) {
            MaterialTheme.colorScheme.error
        } else {
            categoryColor
        }

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .zenSpendShadowLevel1(darkTheme)
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = onClick),
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
            horizontalArrangement = Arrangement.spacedBy(spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Circular progress indicator
            Box(
                modifier = Modifier.size(72.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    strokeWidth = 6.dp,
                    strokeCap = StrokeCap.Round,
                )
                CircularProgressIndicator(
                    progress = { budgetInfo.spentPercentage },
                    modifier = Modifier.fillMaxSize(),
                    color = indicatorColor,
                    strokeWidth = 6.dp,
                    strokeCap = StrokeCap.Round,
                )
                Text(
                    text = "${(budgetInfo.spentPercentage * 100).toInt()}%",
                    style =
                        MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = indicatorColor,
                        ),
                )
            }

            // Text info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(spacing.xs),
            ) {
                Text(
                    text = budgetInfo.category.name,
                    style =
                        MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                )

                Column {
                    Text(
                        text = "Tersisa",
                        style =
                            MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                    )
                    Text(
                        text = formatRupiah(budgetInfo.remainingAmount),
                        style =
                            MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = indicatorColor,
                            ),
                    )
                    Text(
                        text = "dari ${formatRupiah(budgetInfo.limitAmount)}",
                        style =
                            MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.outline,
                            ),
                    )
                }
            }

            Icon(
                imageVector = getIconByName(budgetInfo.category.iconName),
                contentDescription = budgetInfo.category.name,
                tint = categoryColor,
                modifier = Modifier.size(24.dp),
            )

            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = "Edit Budget",
                tint = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.padding(start = spacing.xs),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongMethod", "ComplexMethod")
@Composable
fun AddBudgetBottomSheet(
    onDismissRequest: () -> Unit,
    availableCategories: List<Category>,
    onSaveBudget: (String, Long, BudgetPeriod) -> Unit,
    onDeleteBudget: (String) -> Unit,
    editingBudget: CategoryBudgetInfo? = null,
) {
    val spacing = LocalZenSpendSpacing.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    var selectedCategory by remember {
        mutableStateOf(
            editingBudget?.category ?: availableCategories.firstOrNull(),
        )
    }
    var limitInput by remember {
        mutableStateOf(
            editingBudget?.limitAmount?.toString() ?: "",
        )
    }
    var selectedPeriod by remember {
        mutableStateOf(
            editingBudget?.limitAmount?.let { BudgetPeriod.MONTHLY } ?: BudgetPeriod.MONTHLY,
        )
    }

    var showCategorySelectorDialog by remember { mutableStateOf(false) }

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
                    text = if (editingBudget != null) "Edit Anggaran" else "Tambah Anggaran",
                    style =
                        MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                    modifier = Modifier.align(Alignment.Center),
                )

                if (editingBudget != null) {
                    IconButton(
                        onClick = {
                            onDeleteBudget(editingBudget.budgetId)
                            onDismissRequest()
                        },
                        modifier = Modifier.align(Alignment.CenterEnd),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Hapus Anggaran",
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }

            // Category Selector
            Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                Text(
                    text = "Pilih Kategori",
                    style =
                        MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.outline,
                        ),
                    modifier = Modifier.padding(start = 4.dp),
                )
                Surface(
                    onClick = {
                        if (editingBudget == null) {
                            showCategorySelectorDialog = true
                        }
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp),
                            )
                            .clip(RoundedCornerShape(12.dp)),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                ) {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(spacing.md),
                        horizontalArrangement = Arrangement.spacedBy(spacing.md),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val categoryColor =
                            selectedCategory
                                ?.let { parseHexColor(it.colorHex) }
                                ?: MaterialTheme.colorScheme.primary
                        Box(
                            modifier =
                                Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(categoryColor.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = getIconByName(selectedCategory?.iconName ?: "category"),
                                contentDescription = "Category Icon",
                                tint = categoryColor,
                            )
                        }
                        Text(
                            text = selectedCategory?.name ?: "Pilih Kategori",
                            style =
                                MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                ),
                            modifier = Modifier.weight(1f),
                        )
                        if (editingBudget == null) {
                            Icon(
                                imageVector = Icons.Rounded.ExpandMore,
                                contentDescription = "Expand",
                                tint = MaterialTheme.colorScheme.outline,
                            )
                        }
                    }
                }
            }

            // Numeric Input (Amount)
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = spacing.md),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.xs),
            ) {
                Text(
                    text = "Batas Anggaran",
                    style =
                        MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.outline,
                        ),
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = "Rp",
                        style =
                            MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                            ),
                        modifier = Modifier.padding(end = spacing.xs),
                    )
                    TextField(
                        value = limitInput,
                        onValueChange = {
                            if (it.all { char -> char.isDigit() }) {
                                limitInput = it
                            }
                        },
                        placeholder = {
                            Text(
                                text = "0",
                                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors =
                            TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                            ),
                        textStyle =
                            MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 36.sp,
                            ),
                        modifier = Modifier.width(180.dp),
                    )
                }
                Box(
                    modifier =
                        Modifier
                            .size(width = 96.dp, height = 2.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                )
            }

            // Period Selector
            Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                Text(
                    text = "Periode",
                    style =
                        MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.outline,
                        ),
                    modifier = Modifier.padding(start = 4.dp),
                )
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh, CircleShape)
                            .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    val isWeekly = selectedPeriod == BudgetPeriod.WEEKLY
                    Box(
                        modifier =
                            Modifier
                                .weight(1f)
                                .clip(CircleShape)
                                .background(
                                    if (isWeekly) {
                                        MaterialTheme.colorScheme.surfaceContainerLowest
                                    } else {
                                        Color.Transparent
                                    },
                                )
                                .clickable { selectedPeriod = BudgetPeriod.WEEKLY }
                                .padding(vertical = spacing.md),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Mingguan",
                            style =
                                MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isWeekly) FontWeight.Bold else FontWeight.Normal,
                                    color =
                                        if (isWeekly) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        },
                                ),
                        )
                    }
                    val isMonthly = selectedPeriod == BudgetPeriod.MONTHLY
                    Box(
                        modifier =
                            Modifier
                                .weight(1f)
                                .clip(CircleShape)
                                .background(
                                    if (isMonthly) {
                                        MaterialTheme.colorScheme.surfaceContainerLowest
                                    } else {
                                        Color.Transparent
                                    },
                                )
                                .clickable { selectedPeriod = BudgetPeriod.MONTHLY }
                                .padding(vertical = spacing.md),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Bulanan",
                            style =
                                MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isMonthly) FontWeight.Bold else FontWeight.Normal,
                                    color =
                                        if (isMonthly) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        },
                                ),
                        )
                    }
                }
            }

            // Zen Waves graphic representation
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Waves,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    modifier = Modifier.size(48.dp),
                )
            }

            // Save & Cancel Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                ZenSpendSecondaryButton(
                    text = "Batal",
                    onClick = onDismissRequest,
                    modifier = Modifier.weight(1f),
                )
                ZenSpendPrimaryButton(
                    text = "Simpan",
                    onClick = {
                        val amount = limitInput.toLongOrNull() ?: 0L
                        val cat = selectedCategory
                        if (cat == null) {
                            Toast.makeText(
                                context,
                                "Silakan pilih kategori terlebih dahulu!",
                                Toast.LENGTH_SHORT,
                            ).show()
                        } else if (amount <= 0) {
                            Toast.makeText(
                                context,
                                "Jumlah batas anggaran harus lebih besar dari 0!",
                                Toast.LENGTH_SHORT,
                            ).show()
                        } else {
                            onSaveBudget(cat.id, amount, selectedPeriod)
                            onDismissRequest()
                        }
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }

    if (showCategorySelectorDialog) {
        CategorySelectorDialog(
            categories = availableCategories,
            onCategorySelected = {
                selectedCategory = it
                showCategorySelectorDialog = false
            },
            onDismissRequest = { showCategorySelectorDialog = false },
        )
    }
}

@Suppress("LongMethod")
@Composable
fun CategorySelectorDialog(
    categories: List<Category>,
    onCategorySelected: (Category) -> Unit,
    onDismissRequest: () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = "Pilih Kategori",
                    style =
                        MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth().height(240.dp),
                ) {
                    items(categories) { category ->
                        val color = parseHexColor(category.colorHex)
                        Column(
                            modifier =
                                Modifier
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(12.dp),
                                    )
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onCategorySelected(category) }
                                    .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Box(
                                modifier =
                                    Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(color.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = getIconByName(category.iconName),
                                    contentDescription = category.name,
                                    tint = color,
                                    modifier = Modifier.size(24.dp),
                                )
                            }
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        }
    }
}
