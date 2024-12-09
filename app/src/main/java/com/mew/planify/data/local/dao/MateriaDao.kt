package com.mew.planify.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.mew.planify.data.local.entities.MateriaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MateriaDao {
    @Query("SELECT * FROM materias")
    fun getAll(): Flow<List<MateriaEntity>>

    @Query("SELECT * FROM materias WHERE id = :id")
    fun findById(id: Int): Flow<MateriaEntity?>

    @Upsert
    suspend fun insertOrUpdate(materia: MateriaEntity)

    @Delete
    suspend fun delete(materia: MateriaEntity)
}