package com.mew.planify.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.mew.planify.data.local.entities.ProfesorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfesorDao {
    @Query("SELECT * FROM profesores")
    fun getAll(): Flow<List<ProfesorEntity>>

    @Upsert
    suspend fun insertOrUpdate(profesor: ProfesorEntity)

    @Delete
    suspend fun delete(profesor: ProfesorEntity)

    @Query("SELECT * FROM profesores WHERE id = :profesorId")
    fun findById(profesorId: Int): Flow<ProfesorEntity?>

}