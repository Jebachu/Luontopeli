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

    // Kaikki havainnot (uusin ensin)
    @Query("SELECT * FROM nature_spots ORDER BY timestamp DESC")
    fun getAll(): Flow<List<NatureSpot>>

    // Karttaa varten (vain validit GPS-pisteet)
    @Query("""
        SELECT * FROM nature_spots 
        WHERE latitude != 0.0 AND longitude != 0.0
        ORDER BY timestamp DESC
    """)
    fun getSpotsWithLocation(): Flow<List<NatureSpot>>

    // Insert (tärkeä: REPLACE ettei tuplaudu)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(spot: NatureSpot)

    // Poista yksittäinen
    @Delete
    suspend fun delete(spot: NatureSpot)

    // Tyhjennys
    @Query("DELETE FROM nature_spots")
    suspend fun deleteAll()
}