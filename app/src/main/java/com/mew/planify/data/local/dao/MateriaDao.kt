package com.mew.planify.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.mew.planify.data.local.entities.HorarioEntity
import com.mew.planify.data.local.entities.MateriaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MateriaDao {
    @Query("SELECT * FROM materias ORDER BY nombre ASC")
    fun getAll(): Flow<List<MateriaEntity>>

    @Query("SELECT * FROM materias WHERE id = :id")
    fun findById(id: Int): Flow<MateriaEntity?>

    @Upsert
    suspend fun insertOrUpdate(materia: MateriaEntity)

    @Delete
    suspend fun delete(materia: MateriaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarMateria(materia: MateriaEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarHorarios(horarios: List<HorarioEntity>)

    @Transaction
    suspend fun insertarMateriaConHorarios(materia: MateriaEntity, horarios: List<HorarioEntity>) {
        // Inserta la materia y obtiene su ID generado.
        val materiaId = insertarMateria(materia)

        // Asigna el ID generado a cada horario y luego inserta los horarios.
        val horariosConMateriaId = horarios.map { it.copy(idMateria = materiaId.toInt()) }
        insertarHorarios(horariosConMateriaId)
    }

    @Transaction
    @Query("SELECT * FROM materias WHERE id = :materiaId")
    suspend fun obtenerMateriaConHorarios(materiaId: Int): MateriaConHorarios
}