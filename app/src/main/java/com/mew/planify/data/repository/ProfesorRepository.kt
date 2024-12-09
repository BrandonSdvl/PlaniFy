package com.mew.planify.data.repository

import com.mew.planify.data.local.dao.ProfesorDao
import com.mew.planify.data.local.entities.ProfesorEntity
import kotlinx.coroutines.flow.Flow

class ProfesorRepository(private val profesorDao: ProfesorDao) {

    suspend fun insertOrUpdate(profesor: ProfesorEntity) {
        profesorDao.insertOrUpdate(profesor)
    }

    fun getAll(): Flow<List<ProfesorEntity>> {
        return profesorDao.getAll()
    }

    fun findById(profesorId: Int): Flow<ProfesorEntity?> {
        return profesorDao.findById(profesorId)
    }

    suspend fun delete(profesor: ProfesorEntity) {
        profesorDao.delete(profesor)
    }
}