package com.example.luontopeli.data.repository

import com.example.luontopeli.data.local.dao.NatureSpotDao
import com.example.luontopeli.data.local.entity.NatureSpot
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NatureSpotRepository @Inject constructor(
    private val dao: NatureSpotDao
) {

    // 🟢 Kaikki spotit (UI / debug)
    fun getAllSpots(): Flow<List<NatureSpot>> {
        return dao.getAll()
    }

    // 🗺️ Kartalle vain GPS-spots
    fun getSpotsWithLocation(): Flow<List<NatureSpot>> {
        return dao.getSpotsWithLocation()
    }

    // 📸 Tallennus kamerasta
    suspend fun insertSpot(spot: NatureSpot) {
        dao.insert(spot)
    }

    // 🧹 yksittäinen poisto
    suspend fun deleteSpot(spot: NatureSpot) {
        dao.delete(spot)
    }

    // 🧨 kaikki pois (debug)
    suspend fun clearAll() {
        dao.deleteAll()
    }
}