package com.mew.planify.data.local.dao

import java.time.LocalTime

data class HorarioInfo(
    val id: Int,
    val dia_semana: String,
    val hora_inicio: LocalTime,
    val hora_fin: LocalTime,
    val edificio: String,
    val salon: String,
    val nombre_materia: String,
    val nombre_profesor: String
)
