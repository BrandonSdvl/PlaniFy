package com.mew.planify.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.mew.planify.data.local.entities.TareaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TareaDao {
    @Query("SELECT * FROM tareas WHERE id_materia = :idMateria")
    fun obtenerTareasPorMateria(idMateria: Int): Flow<List<TareaEntity>>

    @Upsert
    suspend fun upsert(tarea: TareaEntity)

    @Delete
    suspend fun delete(tarea: TareaEntity)

    @Query("SELECT * FROM tareas")
    fun obtenerTodasLasTareas(): Flow<List<TareaEntity>>

    @Query("SELECT * FROM tareas WHERE id = :id")
    fun obtenerTareaPorId(id: Int): Flow<TareaEntity?>
}
