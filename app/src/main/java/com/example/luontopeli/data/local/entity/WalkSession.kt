package com.example.luontopeli.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "walk_sessions")
data class WalkSession(
    @PrimaryKey
    val id: String = java.util.UUID.randomUUID().toString(),
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val stepCount: Int = 0,
    val distanceMeters: Float = 0f,
    val spotsFound: Int = 0,
    val isActive: Boolean = true
)
