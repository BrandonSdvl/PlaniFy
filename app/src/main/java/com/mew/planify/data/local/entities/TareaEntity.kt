package com.mew.planify.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "tareas",
    foreignKeys = [
        ForeignKey(
            entity = MateriaEntity::class, // Tabla a la que hace referencia
            parentColumns = ["id"], // Columna de MateriaEntity
            childColumns = ["id_materia"], // Columna de TareaEntity
            onDelete = ForeignKey.SET_NULL // Deja el campo como null si la materia es eliminada
        )
    ],
    indices = [Index(value = ["id_materia"])] // Agrega un índice para optimizar búsquedas por id_materia
)
data class TareaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "id_materia") val idMateria: Int? = null,
    val titulo: String = "",
    val descripcion: String = "",
    val prioridad: String = "",
    @ColumnInfo(name = "fecha_entrega") val fechaEntrega: Date? = null,
    val estatus: String = "Pendiente"
)
