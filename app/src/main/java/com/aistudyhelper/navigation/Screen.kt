package com.aistudyhelper.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.SmartToy
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null
) {
    data object AIChat : Screen("ai_chat", "AI Chat", Icons.Rounded.SmartToy)
    data object Subjects : Screen("subjects", "Subjects", Icons.AutoMirrored.Rounded.MenuBook)
    data object Results : Screen("results", "Results", Icons.Rounded.BarChart)
    data object Flashcards : Screen("flashcards", "Flashcards")
}
