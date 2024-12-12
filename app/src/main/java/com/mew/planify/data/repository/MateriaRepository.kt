package com.mew.planify.data.repository

import com.mew.planify.data.local.dao.MateriaConHorarios
import com.mew.planify.data.local.dao.MateriaDao
import com.mew.planify.data.local.entities.HorarioEntity
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

    suspend fun insertarMateria(materia: MateriaEntity): Long {
        return materiaDao.insertarMateria(materia)
    }

    suspend fun insertarHorarios(horarios: List<HorarioEntity>) {
        materiaDao.insertarHorarios(horarios)
    }

    suspend fun insertarMateriaConHorarios(materia: MateriaEntity, horarios: List<HorarioEntity>) {

        materiaDao.insertarMateriaConHorarios(materia, horarios)
    }

    suspend fun obtenerMateriaConHorarios(materiaId: Int): MateriaConHorarios {
        return materiaDao.obtenerMateriaConHorarios(materiaId)
    }
}