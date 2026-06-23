package com.flla.zenspend.core.designsystem.component

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Data class representing a category option in the bottom sheet.
 */
data class CategoryItem(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val containerColor: Color,
)
