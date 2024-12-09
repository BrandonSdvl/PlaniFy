package com.mew.planify.data.repository

import com.mew.planify.data.local.dao.TareaDao
import com.mew.planify.data.local.entities.TareaEntity
import kotlinx.coroutines.flow.Flow

class TareaRepository(private val tareaDao: TareaDao) {
    suspend fun insertOrUpdate(tarea: TareaEntity) {
        tareaDao.insertOrUpdate(tarea)
    }

    fun getAll(): Flow<List<TareaEntity>> {
        return tareaDao.getAll()
    }

    fun findById(id: Int): Flow<TareaEntity?> {
        return tareaDao.findById(id)
    }

    suspend fun delete(tarea: TareaEntity) {
        tareaDao.delete(tarea)
    }
}
