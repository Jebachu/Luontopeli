package com.example.luontopeli.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun LuontopeliBottomBar(navController: NavController) {

    val screens = listOf(
        Screen.Map,
        Screen.Discover,
        Screen.Stats
    )

    NavigationBar {
        val currentRoute =
            navController.currentBackStackEntryAsState().value?.destination?.route

        screens.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo("map")
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

