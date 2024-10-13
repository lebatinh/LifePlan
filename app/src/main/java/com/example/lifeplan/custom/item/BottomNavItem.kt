package com.example.lifeplan.custom.item

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(var title: String, val icon: ImageVector, val screenRoute: String) {
    data object Home : BottomNavItem("Home", Icons.Default.Home, "home")
    data object Schedule : BottomNavItem("Schedule", Icons.Default.DateRange, "schedule")
    data object Expenditure : BottomNavItem("Expenditure", Icons.Default.AccountBox, "expenditure")
}