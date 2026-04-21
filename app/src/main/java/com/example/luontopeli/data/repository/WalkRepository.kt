package com.example.luontopeli.data.repository

import com.example.luontopeli.data.local.dao.WalkSessionDao
import com.example.luontopeli.data.local.entity.WalkSession
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WalkRepository @Inject constructor(
    private val dao: WalkSessionDao
) {

    fun getAllSessions(): Flow<List<WalkSession>> {
        return dao.getAllSessions()
    }

    suspend fun insertWalk(session: WalkSession) {
        dao.insert(session)
    }

    suspend fun updateWalk(session: WalkSession) {
        dao.update(session)
    }

    suspend fun deleteWalk(session: WalkSession) {
        dao.delete(session)
    }

    suspend fun getActiveSession(): WalkSession? {
        return dao.getActiveSession()
    }
}
