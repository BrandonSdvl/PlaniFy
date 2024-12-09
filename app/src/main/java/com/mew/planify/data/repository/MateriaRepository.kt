package com.mew.planify.data.repository

import com.mew.planify.data.local.dao.MateriaDao
import com.mew.planify.data.local.entities.MateriaEntity
import kotlinx.coroutines.flow.Flow

class MateriaRepository(private val materiaDao: MateriaDao) {
    fun getAll(): Flow<List<MateriaEntity>> {
        return materiaDao.getAll()
    }

    fun findById(id: Int): Flow<MateriaEntity?> {
        return materiaDao.findById(id)
    }

    suspend fun insertOrUpdate(materia: MateriaEntity) {
        materiaDao.insertOrUpdate(materia)
    }

    suspend fun delete(materia: MateriaEntity) {
        materiaDao.delete(materia)
    }
}