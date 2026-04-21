package com.example.luontopeli.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luontopeli.data.local.entity.NatureSpot
import com.example.luontopeli.data.repository.NatureSpotRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    repository: NatureSpotRepository
) : ViewModel() {

    val spots: StateFlow<List<NatureSpot>> =
        repository.getAllSpots()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = emptyList()
            )
}
