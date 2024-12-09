package com.mew.planify.data.repository

import com.mew.planify.data.local.dao.ProfesorDao
import com.mew.planify.data.local.entities.ProfesorEntity
import kotlinx.coroutines.flow.Flow

class ProfesorRepository(private val profesorDao: ProfesorDao) {

    suspend fun insertProfesor(profesor: ProfesorEntity) {
        profesorDao.upsert(profesor)
    }

    fun obtenerProfesores(): Flow<List<ProfesorEntity>> {
        return profesorDao.obtenerProfesores()
    }

    fun obtenerProfesorPorId(profesorId: Int): Flow<ProfesorEntity?> {
        return profesorDao.obtenerProfesorPorId(profesorId)
    }

    suspend fun eliminarProfesor(profesor: ProfesorEntity) {
        profesorDao.delete(profesor)
    }
}