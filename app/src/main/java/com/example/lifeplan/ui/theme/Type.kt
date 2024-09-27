package com.example.lifeplan.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.lifeplan.R

// Tạo FontFamily (Roboto)
val CustomFontFamily = FontFamily(
    Font(R.font.normal, FontWeight.Normal),
    Font(R.font.light, FontWeight.Light),
    Font(R.font.medium, FontWeight.Medium),
    Font(R.font.regular, FontWeight.SemiBold),
    Font(R.font.bold, FontWeight.Bold)
)

// Set of Material typography styles to start with
val Typography = Typography(

    //Tiêu đề chính
    displayLarge = TextStyle(
        fontFamily = CustomFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),

    //Mục lớn
    titleLarge = TextStyle(
        fontFamily = CustomFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp
    ),

    //item
    bodyLarge = TextStyle(
        fontFamily = CustomFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),

    //Mục nhỏ
    bodyMedium = TextStyle(
        fontFamily = CustomFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 14.sp
    ),

    //item bottom nav
    labelLarge = TextStyle(
        fontFamily = CustomFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp
    ),

    //Mô tả, chú thích
    labelMedium = TextStyle(
        fontFamily = CustomFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp
    )
)