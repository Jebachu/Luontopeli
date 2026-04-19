package com.example.luontopeli.ui.map

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
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
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    navController: NavController,
    mapViewModel: MapViewModel = hiltViewModel(),
    walkViewModel: WalkViewModel = hiltViewModel()
) {

    val context = LocalContext.current

    val permissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACTIVITY_RECOGNITION
        )
    )

    val isWalking by walkViewModel.isWalking.collectAsState()
    val routePoints by mapViewModel.routePoints.collectAsState()
    val currentLocation by mapViewModel.currentLocation.collectAsState()

    // 🔥 FIX: pakota initialValue ettei jää "tyhjäksi"
    val spots by mapViewModel.spots.collectAsState(initial = emptyList())

    var selectedSpot by remember { mutableStateOf<NatureSpot?>(null) }

    val mapView = remember { MapView(context) }

    val myLocationOverlay = remember {
        MyLocationNewOverlay(
            GpsMyLocationProvider(context),
            mapView
        ).apply {
            enableMyLocation()
            enableFollowLocation()
        }
    }

    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
    }

    LaunchedEffect(isWalking, permissionState.allPermissionsGranted) {
        if (permissionState.allPermissionsGranted && isWalking) {
            mapViewModel.startTracking()
        } else {
            mapViewModel.stopTracking()
        }
    }

    if (!permissionState.allPermissionsGranted) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Button(
                onClick = {
                    permissionState.launchMultiplePermissionRequest()
                }
            ) {
                Text("Myönnä sijaintilupa")
            }
        }
        return
    }

    Box(Modifier.fillMaxSize()) {

        AndroidView(
            factory = {
                mapView.apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(16.0)
                    overlays.add(myLocationOverlay)
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { map ->

                // ❗ EI clear() → vain päivitys
                map.overlays.removeAll { it is Marker || it is Polyline }

                // reitti
                if (routePoints.size > 1) {
                    val line = Polyline().apply {
                        setPoints(routePoints)
                        outlinePaint.strokeWidth = 8f
                    }
                    map.overlays.add(line)
                }

                // pinnit
                spots.forEach { spot ->
                    if (spot.latitude != 0.0 && spot.longitude != 0.0) {

                        val marker = Marker(map).apply {
                            position = GeoPoint(spot.latitude, spot.longitude)
                            title = spot.name

                            setOnMarkerClickListener { _, _ ->
                                selectedSpot = spot
                                true
                            }
                        }

                        map.overlays.add(marker)
                    }
                }

                currentLocation?.let { loc ->
                    map.controller.setCenter(
                        GeoPoint(loc.latitude, loc.longitude)
                    )
                }

                map.invalidate()
            }
        )

        FloatingActionButton(
            onClick = { navController.navigate("camera") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text("📷")
        }

        FloatingActionButton(
            onClick = {
                currentLocation?.let {
                    mapView.controller.animateTo(
                        GeoPoint(it.latitude, it.longitude)
                    )
                }
            },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text("🎯")
        }

        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(12.dp)
        ) {
            Column(Modifier.padding(12.dp)) {

                val session = walkViewModel.currentSession.collectAsState().value

                Text("Askeleet: ${session?.stepCount ?: 0}")
                Text("Matka: ${session?.distanceMeters ?: 0f} m")

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (isWalking) walkViewModel.stopWalk()
                        else walkViewModel.startWalk()
                    }
                ) {
                    Text(if (isWalking) "Lopeta" else "Aloita kävely")
                }
            }
        }
    }

    selectedSpot?.let { spot ->
        Surface(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(spot.name)

                AsyncImage(
                    model = spot.imagePath,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

                Button(onClick = { selectedSpot = null }) {
                    Text("Sulje")
                }
            }
        }
    }
}