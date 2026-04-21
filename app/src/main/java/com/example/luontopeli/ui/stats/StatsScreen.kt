package com.example.luontopeli.ui.stats

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.luontopeli.viewmodel.StatsViewModel

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel()
) {
    val stats by viewModel.stats.collectAsState(initial = com.example.luontopeli.viewmodel.StatsUiState())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "📊 Tilastot",
            style = MaterialTheme.typography.headlineMedium
        )

        Card {
            Column(Modifier.padding(16.dp)) {
                Text("🚶‍♂️ Askeleet yhteensä: ${stats.totalSteps}")
                Text("📏 Matka yhteensä: ${"%.2f".format(stats.totalDistance)} m")
                Text("🧍 Kävelykertoja: ${stats.totalWalks}")
            }
        }

        Card {
            Column(Modifier.padding(16.dp)) {
                Text("🌿 Luontohavainnot: ${stats.totalSpots}")
                Text("📍 Viimeisin: ${stats.lastSpotName ?: "Ei vielä havaintoja"}")
            }
        }
    }
}
