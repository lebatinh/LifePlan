package com.example.lifeplan.custom.item

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Book
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(var title: String, val icon: ImageVector) {
    data object Home : BottomNavItem("Home", Icons.Default.Home)
    data object Schedule : BottomNavItem("Schedule", Icons.Default.CalendarMonth)
    data object Expenditure : BottomNavItem("Expenditure", Icons.Outlined.Book)
}