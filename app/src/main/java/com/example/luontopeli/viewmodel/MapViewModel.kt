package com.example.luontopeli.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luontopeli.data.local.entity.NatureSpot
import com.example.luontopeli.data.repository.NatureSpotRepository
import com.example.luontopeli.location.LocationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: NatureSpotRepository,
    private val locationManager: LocationManager
) : ViewModel() {

    val spots: StateFlow<List<NatureSpot>> =
        repository.getAllSpots()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = emptyList()
            )

    val currentLocation = locationManager.currentLocation
    val routePoints = locationManager.routePoints

    fun startTracking() {
        locationManager.startTracking()
    }

    fun stopTracking() {
        locationManager.stopTracking()
    }

    fun resetRoute() {
        locationManager.resetRoute()
    }

    override fun onCleared() {
        super.onCleared()
        locationManager.stopTracking()
    }
}
