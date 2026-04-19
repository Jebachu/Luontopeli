package com.example.luontopeli.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "nature_spots")
data class NatureSpot(

    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    val name: String = "Luontokohde",

    val latitude: Double,

    val longitude: Double,

    val imagePath: String,

    val plantLabel: String? = null,

    val confidence: Float? = null,

    val timestamp: Long = System.currentTimeMillis()
)