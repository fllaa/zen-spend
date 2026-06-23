@file:Suppress("MatchingDeclarationName")

package com.flla.zenspend.core.designsystem.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CardGiftcard
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class CategoryVisualStyle(
    val icon: ImageVector,
    val tint: Color,
    val containerTint: Color,
)

fun parseCategoryColor(hex: String): Color =
    runCatching { Color(android.graphics.Color.parseColor(hex)) }
        .getOrDefault(Color(0xFF37474F))

@Suppress("CyclomaticComplexMethod")
fun categoryIcon(iconName: String): ImageVector =
    when (iconName.lowercase()) {
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
        "more_horiz" -> Icons.Rounded.MoreHoriz
        else -> Icons.Rounded.MoreHoriz
    }

fun categoryVisualStyle(
    iconName: String,
    colorHex: String,
    containerAlpha: Float = 0.12f,
): CategoryVisualStyle {
    val tint = parseCategoryColor(colorHex)
    return CategoryVisualStyle(
        icon = categoryIcon(iconName),
        tint = tint,
        containerTint = tint.copy(alpha = containerAlpha),
    )
}
