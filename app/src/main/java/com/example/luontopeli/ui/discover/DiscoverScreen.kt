package com.example.luontopeli.ui.discover

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.luontopeli.viewmodel.MapViewModel

@Composable
fun DiscoverScreen(
    viewModel: MapViewModel = hiltViewModel()
) {
    val spots by viewModel.spots.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Havainnot (${spots.size})",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (spots.isEmpty()) {
            Text("Ei havaintoja vielä")
        } else {
            LazyColumn {
                items(spots) { spot ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {

                            Text(
                                text = spot.name,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // 🔥 korvaus descriptionille
                            Text(
                                text = "Luontohavainto tallennettu",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Lat: ${spot.latitude}, Lng: ${spot.longitude}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}