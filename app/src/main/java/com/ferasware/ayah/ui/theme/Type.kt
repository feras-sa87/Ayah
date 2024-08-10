package com.ferasware.ayah.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.sp
import com.ferasware.ayah.R

val kitabFont = FontFamily(
    Font(R.font.kitab_regular, FontWeight.Normal), Font(R.font.kitab_bold, FontWeight.Bold)
)

val Typography = Typography(
    // For Ayah & Page screen
    bodyLarge = TextStyle(
        fontFamily = kitabFont,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 60.sp,
        textAlign = TextAlign.Center,
        letterSpacing = 2.5.sp,
        textDirection = TextDirection.Rtl,
    ),
    // For Azkar screen
    bodyMedium = TextStyle(
        fontFamily = kitabFont,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        textDirection = TextDirection.Rtl
    ),
    // For Ayah & Page screen Details
    bodySmall = TextStyle(
        fontFamily = kitabFont,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic,
        textDirection = TextDirection.Rtl
    ),
)