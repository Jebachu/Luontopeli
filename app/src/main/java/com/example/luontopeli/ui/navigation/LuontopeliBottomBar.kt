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

    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {

        screens.forEach { screen ->

            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {

                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {

                            popUpTo(Screen.Map.route) {
                                saveState = true
                            }

                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}