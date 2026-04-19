package com.example.luontopeli.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luontopeli.data.local.entity.NatureSpot
import com.example.luontopeli.data.local.entity.WalkSession
import com.example.luontopeli.data.repository.NatureSpotRepository
import com.example.luontopeli.data.repository.WalkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class StatsUiState(
    val totalSteps: Int = 0,
    val totalDistance: Float = 0f,
    val totalWalks: Int = 0,
    val totalSpots: Int = 0,
    val lastSpotName: String? = null
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    walkRepository: WalkRepository,
    spotRepository: NatureSpotRepository
) : ViewModel() {

    private val sessionsFlow: Flow<List<WalkSession>> =
        walkRepository.getAllSessions()

    private val spotsFlow: Flow<List<NatureSpot>> =
        spotRepository.getAllSpots()

    val stats: StateFlow<StatsUiState> =
        combine(sessionsFlow, spotsFlow) { sessions, spots ->

            val totalSteps = sessions.sumOf { it.stepCount }
            val totalDistance = sessions.sumOf { it.distanceMeters.toDouble() }.toFloat()

            val lastSpot = spots.maxByOrNull { it.timestamp }

            StatsUiState(
                totalSteps = totalSteps,
                totalDistance = totalDistance,
                totalWalks = sessions.size,
                totalSpots = spots.size,
                lastSpotName = lastSpot?.name
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = StatsUiState()
            )
}