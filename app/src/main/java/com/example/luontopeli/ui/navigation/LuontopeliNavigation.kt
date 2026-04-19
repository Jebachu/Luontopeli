package com.example.luontopeli.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*

import com.example.luontopeli.ui.map.MapScreen
import com.example.luontopeli.ui.discover.DiscoverScreen
import com.example.luontopeli.ui.stats.StatsScreen
import com.example.luontopeli.camera.CameraScreen

import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding

@Composable
fun LuontopeliNavigation() {

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            LuontopeliBottomBar(navController)
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = Screen.Map.route,
            modifier = Modifier.padding(padding)
        ) {

            composable(Screen.Map.route) {
                MapScreen(navController = navController)
            }

            composable(Screen.Discover.route) {
                DiscoverScreen()
            }

            composable(Screen.Stats.route) {
                StatsScreen()
            }

            composable("camera") {
                CameraScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
