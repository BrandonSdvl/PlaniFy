package com.mew.planify.data.local.dao

import androidx.room.Embedded
import androidx.room.Relation
import com.mew.planify.data.local.entities.HorarioEntity
import com.mew.planify.data.local.entities.MateriaEntity

data class MateriaConHorarios(
    @Embedded val materia: MateriaEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id_materia"
    )
    val horarios: List<HorarioEntity>
)
