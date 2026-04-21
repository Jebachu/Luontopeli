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

    fun getAllSpots(): Flow<List<NatureSpot>> {
        return dao.getAll()
    }

    fun getSpotsWithLocation(): Flow<List<NatureSpot>> {
        return dao.getSpotsWithLocation()
    }

    suspend fun insertSpot(spot: NatureSpot) {
        dao.insert(spot)
    }

    suspend fun deleteSpot(spot: NatureSpot) {
        dao.delete(spot)
    }

    suspend fun clearAll() {
        dao.deleteAll()
    }
}
