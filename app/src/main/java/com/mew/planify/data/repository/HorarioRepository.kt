package com.mew.planify.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.mew.planify.data.local.dao.HorarioDao
import com.mew.planify.data.local.entities.HorarioEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalTime

class HorarioRepository(private val horarioDao: HorarioDao) {
    fun getAll(): Flow<List<HorarioEntity>> {
        return horarioDao.getAll()
    }

    suspend fun insertOrUpdate(horario: HorarioEntity) {
        horarioDao.insertOrUpdate(horario)
    }

    fun findById(id: Int): Flow<HorarioEntity?> {
        return horarioDao.findById(id)
    }

    suspend fun delete(horario: HorarioEntity) {
        horarioDao.delete(horario)
    }

    suspend fun getHorariosExcluyendoMateria(idMateriaExcluida: Int): List<HorarioEntity> {
        return horarioDao.getHorariosExcluyendoMateria(idMateriaExcluida)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getConflictingHorario(
        idProfesor: Int,
        diaSemana: String,
        horaInicio: LocalTime,
        horaFin: LocalTime,
        idMateria: Int
    ): HorarioEntity? {
        val horarios = horarioDao.getHorariosByProfesor(idProfesor, idMateria)
        return horarios.firstOrNull { horario ->
            horario.diaSemana == diaSemana &&
                    (horaInicio.isBefore(horario.horaFin) && horaFin.isAfter(horario.horaInicio))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun verificarTraslapesConOtrasMaterias(
        nuevosHorarios: List<HorarioEntity>,
        idMateriaExcluida: Int,
        repository: HorarioRepository
    ): List<HorarioEntity>? {
        // Recuperar horarios de otras materias (excluyendo la materia actual)
        val horariosExistentes = repository.getHorariosExcluyendoMateria(idMateriaExcluida)

        // Comparar cada nuevo horario con los horarios existentes
        nuevosHorarios.forEach { nuevoHorario ->
            horariosExistentes.forEach { horarioExistente ->
                if (nuevoHorario.diaSemana == horarioExistente.diaSemana &&
                    nuevoHorario.horaInicio.isBefore(horarioExistente.horaFin) &&
                    nuevoHorario.horaFin.isAfter(horarioExistente.horaInicio)
                ) {
                    return listOf(nuevoHorario, horarioExistente) // Devuelve el primer horario en conflicto
                }
            }
        }

        return null // No hay conflictos
    }



}