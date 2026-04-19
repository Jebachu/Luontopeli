package com.example.luontopeli.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Map : Screen("map", "Kartta", Icons.Default.Map)
    object Discover : Screen("discover", "Löydä", Icons.Default.Search)
    object Stats : Screen("stats", "Tilastot", Icons.Default.BarChart)
}