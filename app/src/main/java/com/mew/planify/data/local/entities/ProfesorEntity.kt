package com.mew.planify.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profesores")
data class ProfesorEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String = "",
    val correo: String? = null,
    val telefono: String? = null,
    val cubiculo: String? = null,
    val academia: String? = null,
    val nota: String? = null
)