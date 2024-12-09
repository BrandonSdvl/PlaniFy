package com.mew.planify.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "materias",
    foreignKeys = [
        ForeignKey(
            entity = ProfesorEntity::class,
            parentColumns = ["id"],
            childColumns = ["idProfesor"],
            onDelete = ForeignKey.SET_NULL // Deja el campo como null si el profesor es eliminado.
        )
    ],
    indices = [Index(value = ["idProfesor"])] // Agrega un índice para optimizar búsquedas por idProfesor.
)
data class MateriaEntity (
    @PrimaryKey (autoGenerate = true) val id: Int = 0,
    val secuencia: String = "",
    val nombre: String = "",
    val idProfesor: Int? = null,
)