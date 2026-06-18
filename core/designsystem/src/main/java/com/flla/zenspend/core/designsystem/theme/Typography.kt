package com.flla.zenspend.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.flla.zenspend.core.designsystem.R

// Set up Google Font Provider
private val fontProvider =
    GoogleFont.Provider(
        providerAuthority = "com.google.android.gms.fonts",
        providerPackage = "com.google.android.gms",
        certificates = R.array.com_google_android_gms_fonts_certs,
    )

private val InterFontName = GoogleFont("Inter")

val InterFontFamily =
    FontFamily(
        Font(googleFont = InterFontName, fontProvider = fontProvider, weight = FontWeight.Normal),
        Font(googleFont = InterFontName, fontProvider = fontProvider, weight = FontWeight.Medium),
        Font(googleFont = InterFontName, fontProvider = fontProvider, weight = FontWeight.SemiBold),
        Font(googleFont = InterFontName, fontProvider = fontProvider, weight = FontWeight.Bold),
    )

// Map Typography styles from DESIGN.md
val ZenSpendTypography =
    Typography(
        // display-amount: fontSize: 40px/sp, fontWeight: 600, lineHeight: 48px/sp,
        // letterSpacing: -0.02em, tabular figures
        displayLarge =
            TextStyle(
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 40.sp,
                lineHeight = 48.sp,
                letterSpacing = (-0.02 * 40).sp,
                fontFeatureSettings = "tnum",
            ),
        // headline-lg: fontSize: 32px/sp, fontWeight: 600, lineHeight: 40px/sp, letterSpacing: -0.01em
        headlineLarge =
            TextStyle(
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 32.sp,
                lineHeight = 40.sp,
                letterSpacing = (-0.01 * 32).sp,
            ),
        // headline-lg-mobile: fontSize: 24px/sp, fontWeight: 600, lineHeight: 32px/sp
        headlineMedium =
            TextStyle(
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                lineHeight = 32.sp,
            ),
        // title-md: fontSize: 18px/sp, fontWeight: 500, lineHeight: 24px/sp
        titleMedium =
            TextStyle(
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                lineHeight = 24.sp,
            ),
        // body-lg: fontSize: 16px/sp, fontWeight: 400, lineHeight: 24px/sp
        bodyLarge =
            TextStyle(
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp,
            ),
        // body-md: fontSize: 14px/sp, fontWeight: 400, lineHeight: 20px/sp
        bodyMedium =
            TextStyle(
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp,
            ),
        // label-sm: fontSize: 12px/sp, fontWeight: 500, lineHeight: 16px/sp, letterSpacing: 0.05em
        labelSmall =
            TextStyle(
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = (0.05 * 12).sp,
            ),
    )

// numeric-data: fontSize: 16px/sp, fontWeight: 500, lineHeight: 24px/sp, tabular figures
val NumericDataTextStyle =
    TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontFeatureSettings = "tnum",
    )
