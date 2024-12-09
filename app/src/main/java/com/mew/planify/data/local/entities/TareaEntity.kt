package com.mew.planify.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "tareas")
data class TareaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "id_materia") val idMateria: Int? = null,
    val titulo: String = "",
    val descripcion: String = "",
    val prioridad: String = "",
    @ColumnInfo(name = "fecha_entrega") val fechaEntrega: Date? = null,
    val estatus: String = "Pendiente"
)
