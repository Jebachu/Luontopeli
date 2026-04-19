package com.example.luontopeli.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

class LocationManager @Inject constructor(
    @ApplicationContext context: Context
) {

    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()

    private val _routePoints = MutableStateFlow<List<GeoPoint>>(emptyList())
    val routePoints: StateFlow<List<GeoPoint>> = _routePoints.asStateFlow()

    private var isTracking = false

    private val locationListener = object : LocationListener {

        override fun onLocationChanged(location: Location) {

            _currentLocation.value = location

            val newPoint = GeoPoint(location.latitude, location.longitude)

            val last = _routePoints.value.lastOrNull()

            if (last == null ||
                last.latitude != newPoint.latitude ||
                last.longitude != newPoint.longitude
            ) {
                _routePoints.value = _routePoints.value + newPoint
            }
        }

        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}

        @Deprecated("Deprecated in Java")
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    }

    @SuppressLint("MissingPermission")
    fun startTracking() {
        if (isTracking) return
        isTracking = true

        try {
            val provider = when {
                locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) ->
                    android.location.LocationManager.GPS_PROVIDER

                locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER) ->
                    android.location.LocationManager.NETWORK_PROVIDER

                else -> return
            }

            locationManager.getLastKnownLocation(provider)?.let { lastKnown ->
                _currentLocation.value = lastKnown
            }

            locationManager.requestLocationUpdates(
                provider,
                1000L,
                1f,
                locationListener
            )

        } catch (e: SecurityException) {
            isTracking = false
        }
    }

    fun stopTracking() {
        isTracking = false
        locationManager.removeUpdates(locationListener)
    }

    fun resetRoute() {
        _routePoints.value = emptyList()
    }
}