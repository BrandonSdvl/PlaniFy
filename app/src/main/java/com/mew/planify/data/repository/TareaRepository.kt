package com.mew.planify.data.repository

import com.mew.planify.data.local.dao.TareaDao
import com.mew.planify.data.local.entities.TareaEntity
import kotlinx.coroutines.flow.Flow

class TareaRepository(private val tareaDao: TareaDao) {
    fun obtenerTareasPorMateria(idMateria: Int): Flow<List<TareaEntity>> {
        return tareaDao.obtenerTareasPorMateria(idMateria)
    }

    suspend fun insertTarea(tarea: TareaEntity) {
        tareaDao.upsert(tarea)
    }

    fun obtenerTodasLasTareas(): Flow<List<TareaEntity>> {
        return tareaDao.obtenerTodasLasTareas()
    }

    fun obtenerTareaPorId(id: Int): Flow<TareaEntity?> {
        return tareaDao.obtenerTareaPorId(id)
    }

    suspend fun eliminarTarea(tarea: TareaEntity) {
        tareaDao.delete(tarea)
    }
}
