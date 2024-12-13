package com.mew.planify.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.mew.planify.data.local.entities.TareaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TareaDao {
    @Upsert
    suspend fun insertOrUpdate(tarea: TareaEntity)

    @Delete
    suspend fun delete(tarea: TareaEntity)

    @Query("SELECT * FROM tareas ORDER BY fecha_entrega ASC")
    fun getAll(): Flow<List<TareaEntity>>

    @Query("SELECT * FROM tareas WHERE id = :id")
    fun findById(id: Int): Flow<TareaEntity?>
}
