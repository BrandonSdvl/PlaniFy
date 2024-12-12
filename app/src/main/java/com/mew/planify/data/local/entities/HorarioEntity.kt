package com.mew.planify.data.local.entities

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalTime

@Entity(
    tableName = "horario",
    foreignKeys = [
        ForeignKey(
            entity = MateriaEntity::class,
            parentColumns = ["id"],
            childColumns = ["id_materia"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [androidx.room.Index(value = ["id_materia"])]
)
@RequiresApi(Build.VERSION_CODES.O)
data class HorarioEntity (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "id_materia") val idMateria: Int = 0,
    @ColumnInfo(name = "dia_semana") val diaSemana: String = "",
    @ColumnInfo(name = "hora_inicio") val horaInicio: LocalTime = LocalTime.MIN,
    @ColumnInfo(name = "hora_fin") val horaFin: LocalTime = LocalTime.MIN,
    val edificio: String = "",
    val salon: String = ""
)
