package com.mew.planify.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.mew.planify.data.local.entities.HorarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HorarioDao {
    @Query("SELECT * FROM horario")
    fun getAll(): Flow<List<HorarioEntity>>

    @Upsert
    suspend fun insertOrUpdate(horario: HorarioEntity)

    @Query("SELECT * FROM horario WHERE id = :id")
    fun findById(id: Int): Flow<HorarioEntity?>

    @Delete
    suspend fun delete(horario: HorarioEntity)

    @Query("""
        SELECT h.*
        FROM horario h
        INNER JOIN materias m ON h.id_materia = m.id
        WHERE m.idProfesor = :idProfesor
        AND h.id_materia != :idMateria
    """)
    suspend fun getHorariosByProfesor(idProfesor: Int, idMateria: Int): List<HorarioEntity>

    @Query("""
    SELECT h.*
    FROM horario h
    WHERE h.id_materia != :idMateriaExcluida
""")
    suspend fun getHorariosExcluyendoMateria(idMateriaExcluida: Int): List<HorarioEntity>

    @Query("SELECT h.id, h.dia_semana, h.hora_inicio, h.hora_fin, h.edificio, h.salon, m.nombre AS nombre_materia, p.nombre AS nombre_profesor " +
            "FROM horario h " +
            "INNER JOIN materias m ON h.id_materia = m.id " +
            "INNER JOIN profesores p ON m.idProfesor = p.id")
    fun getHorarioInfo(): List<HorarioInfo>


    @Query("SELECT h.id, h.dia_semana, h.hora_inicio, h.hora_fin, h.edificio, h.salon, m.nombre AS nombre_materia, p.nombre AS nombre_profesor " +
            "FROM horario h " +
            "INNER JOIN materias m ON h.id_materia = m.id " +
            "INNER JOIN profesores p ON m.idProfesor = p.id " +
            "WHERE h.dia_semana = :day")
    suspend fun getHorarioInfoByDay(day: String): List<HorarioInfo>
}