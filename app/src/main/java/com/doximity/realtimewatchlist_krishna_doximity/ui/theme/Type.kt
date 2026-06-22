package com.doximity.realtimewatchlist_krishna_doximity.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.doximity.realtimewatchlist_krishna_doximity.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val HankenGroteskFont = GoogleFont("Hanken Grotesk")
val InterFont = GoogleFont("Inter")
val JetBrainsMonoFont = GoogleFont("JetBrains Mono")

val HankenGroteskFamily = FontFamily(
    Font(googleFont = HankenGroteskFont, fontProvider = provider),
    Font(googleFont = HankenGroteskFont, fontProvider = provider, weight = FontWeight.Bold),
    Font(googleFont = HankenGroteskFont, fontProvider = provider, weight = FontWeight.SemiBold)
)

val InterFamily = FontFamily(
    Font(googleFont = InterFont, fontProvider = provider),
    Font(googleFont = InterFont, fontProvider = provider, weight = FontWeight.SemiBold)
)

val JetBrainsMonoFamily = FontFamily(
    Font(googleFont = JetBrainsMonoFont, fontProvider = provider, weight = FontWeight.Medium)
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = HankenGroteskFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp,
        lineHeight = 56.sp,
        letterSpacing = (-0.02).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = HankenGroteskFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.01).sp
    ),
    titleMedium = TextStyle(
        fontFamily = HankenGroteskFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodySmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelSmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.05.sp
    )
)
