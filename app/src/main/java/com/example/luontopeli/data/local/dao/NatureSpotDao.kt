package com.example.luontopeli.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.luontopeli.data.local.entity.NatureSpot
import kotlinx.coroutines.flow.Flow

@Dao
interface NatureSpotDao {

    // 🟢 Kaikki havainnot (EI SUODATUSTA)
    @Query("SELECT * FROM nature_spots ORDER BY timestamp DESC")
    fun getAll(): Flow<List<NatureSpot>>

    // 🗺️ Sama data kartalle (ei enää 0.0-suodatusta)
    @Query("SELECT * FROM nature_spots ORDER BY timestamp DESC")
    fun getSpotsWithLocation(): Flow<List<NatureSpot>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(spot: NatureSpot)

    @Delete
    suspend fun delete(spot: NatureSpot)

    @Query("DELETE FROM nature_spots")
    suspend fun deleteAll()
}