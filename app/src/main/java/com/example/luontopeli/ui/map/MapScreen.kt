package com.example.luontopeli.ui.map

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.luontopeli.data.local.entity.NatureSpot
import com.example.luontopeli.viewmodel.MapViewModel
import com.example.luontopeli.viewmodel.WalkViewModel
import com.google.accompanist.permissions.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    mapViewModel: MapViewModel = hiltViewModel(),
    walkViewModel: WalkViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val permissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val isWalking by walkViewModel.isWalking.collectAsState()
    val routePoints by mapViewModel.routePoints.collectAsState()
    val currentLocation by mapViewModel.currentLocation.collectAsState()
    val spots by mapViewModel.spots.collectAsState(initial = emptyList())

    var selectedSpot by remember { mutableStateOf<NatureSpot?>(null) }

    LaunchedEffect(isWalking, permissionState.allPermissionsGranted) {
        if (permissionState.allPermissionsGranted && isWalking) {
            mapViewModel.startTracking()
        } else {
            mapViewModel.stopTracking()
        }
    }

    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
    }

    if (!permissionState.allPermissionsGranted) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Tarvitaan sijaintilupa")
            Spacer(Modifier.height(8.dp))
            Button(onClick = {
                permissionState.launchMultiplePermissionRequest()
            }) {
                Text("Myönnä lupa")
            }
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Box(modifier = Modifier.weight(1f)) {

            val mapView = remember { MapView(context) }

            AndroidView(
                factory = {
                    mapView.apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        controller.setZoom(15.0)
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { map ->

                    map.overlays.clear()

                    // 🟢 REITTI
                    if (routePoints.size > 1) {
                        val polyline = Polyline().apply {
                            setPoints(routePoints)
                            outlinePaint.strokeWidth = 8f
                        }
                        map.overlays.add(polyline)
                    }

                    // 📍 MARKERIT
                    spots.forEach { spot ->

                        if (spot.latitude != 0.0 && spot.longitude != 0.0) {

                            val marker = Marker(map).apply {
                                position = GeoPoint(spot.latitude, spot.longitude)

                                title = spot.plantLabel ?: spot.name
                                snippet = "Klikkaa nähdäksesi kuvan"

                                setAnchor(
                                    Marker.ANCHOR_CENTER,
                                    Marker.ANCHOR_BOTTOM
                                )

                                setOnMarkerClickListener { _, _ ->
                                    selectedSpot = spot
                                    true
                                }
                            }

                            map.overlays.add(marker)
                        }
                    }

                    // 📍 KESKITYS
                    currentLocation?.let { loc ->
                        if (loc.latitude != 0.0 && loc.longitude != 0.0) {
                            map.controller.setCenter(
                                GeoPoint(loc.latitude, loc.longitude)
                            )
                        }
                    }

                    map.invalidate()
                }
            )
        }

        WalkControls(walkViewModel)
    }

    // 🟢 BOTTOM SHEET (kuva + tiedot)
    selectedSpot?.let { spot ->

        Surface(
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                Text(
                    text = spot.name,
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(12.dp))

                AsyncImage(
                    model = spot.imagePath,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("📍 Lat: ${spot.latitude}")
                Text("📍 Lon: ${spot.longitude}")

                Spacer(modifier = Modifier.height(12.dp))

                Button(onClick = { selectedSpot = null }) {
                    Text("Sulje")
                }
            }
        }
    }
}

@Composable
fun WalkControls(viewModel: WalkViewModel) {

    val session by viewModel.currentSession.collectAsState()
    val isWalking by viewModel.isWalking.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Askeleet: ${session?.stepCount ?: 0}")
        Text("Matka: ${session?.distanceMeters ?: 0f} m")

        Spacer(Modifier.height(8.dp))

        if (!isWalking) {
            Button(onClick = { viewModel.startWalk() }) {
                Text("Aloita kävely")
            }
        } else {
            Button(onClick = { viewModel.stopWalk() }) {
                Text("Lopeta")
            }
        }
    }
}