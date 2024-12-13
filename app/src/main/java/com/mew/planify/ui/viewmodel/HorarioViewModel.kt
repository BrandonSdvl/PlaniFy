package com.mew.planify.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mew.planify.data.local.dao.HorarioInfo
import com.mew.planify.data.local.entities.HorarioEntity
import com.mew.planify.data.repository.HorarioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O)
class HorarioViewModel(private val repository: HorarioRepository) : ViewModel() {
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _horario = MutableStateFlow<List<HorarioEntity>>(emptyList())
    val horario: StateFlow<List<HorarioEntity>> = _horario

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

//    private val _formState = mutableStateOf(FormState())
//    val formState: State<FormState> = _formState

    private val _changed = MutableStateFlow(false)
    val changed: StateFlow<Boolean> = _changed

    private val _clase = MutableStateFlow(HorarioEntity())
    val clase: StateFlow<HorarioEntity> = _clase

    private val _currClases = MutableStateFlow<List<HorarioEntity>>(emptyList())
    val currClases: StateFlow<List<HorarioEntity>> = _currClases

    private val _formState = MutableStateFlow<List<FormState>>(emptyList())
    val formState: StateFlow<List<FormState>> = _formState

    val diasOptions = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes")

    init {
        getAll()
    }

    private fun getAll() {
        viewModelScope.launch {
            _loading.value = true // Mostrar indicador de carga

            try {
                repository.getAll()
                    .stateIn(
                        scope = viewModelScope,
                        started = SharingStarted.Lazily,
                        initialValue = emptyList()
                    )
                    .collect { horarios ->
                        _loading.value = false
                        _horario.value = horarios
                    }
            } catch (e: Exception) {
                _loading.value = false
                _error.value = e.message
            }
        }
    }

    fun add() {
        _currClases.value += HorarioEntity()
        _formState.value += FormState()
    }

    fun clean() {
        _clase.value = HorarioEntity() // Limpiar el formulario
        _formState.value = emptyList()
        _changed.value = false
        _currClases.value = emptyList()
    }

    fun setCurrClases(clases: List<HorarioEntity>) {
        _currClases.value = clases
    }

    fun deleteClase(index: Int) {
        _currClases.value = _currClases.value.toMutableList().apply {
            removeAt(index)
        }
        _formState.value = _formState.value.toMutableList().apply {
            removeAt(index)
        }
    }

    fun insertOrUpdate(): Boolean {
        if (validateAll(currClases.value)) {
            viewModelScope.launch {
                try {
                    repository.insertOrUpdate(clase.value)
                    getAll() // Actualizar la lista después de guardar
                } catch (e: Exception) {
                    _error.value = e.message
                }
            }
            return true
        } else {
            return false
        }
    }

    fun delete(id: Int) {
        viewModelScope.launch {
            try {
                val horario = repository.findById(id).firstOrNull()

                horario?.let {
                    repository.delete(it)
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun findById(id: Int): Flow<HorarioEntity?> {
        if (id < 0) {
            _error.value = "El ID del horario no es válido."
            return flowOf(null) // Flujo vacío
        }

        return repository.findById(id)
    }

    fun setHorario(horario: HorarioEntity) {
        _clase.value = horario
    }

    fun getHorarioInfo(): List<HorarioInfo> {
        return repository.getHorarioInfo()
    }

    suspend fun getHorarioInfoByDay(day: String): List<HorarioInfo> {
        return repository.getHorarioInfoByDay(day)
    }

    private fun validate(index: Int, horario: HorarioEntity): Boolean {
        onIdMateriaChange(index, horario.idMateria)
        onDiaSemanaChange(index, horario.diaSemana)
        onHoraInicioChange(index, horario.horaInicio.hour, horario.horaInicio.minute)
        onHoraFinChange(index, horario.horaFin.hour, horario.horaFin.minute)
        onEdificioChange(index, horario.edificio)
        onSalonChange(index, horario.salon)

        val errors = formState.value[index]
        return errors.errorIdMateria == null &&
                errors.errorDiaSemana == null &&
                errors.errorHoraInicio == null &&
                errors.errorHoraFin == null &&
                errors.errorEdificio == null &&
                errors.errorSalon == null
    }

    fun validateAll(horarios: List<HorarioEntity>): Boolean {
        return horarios.indices.all { index ->
            validate(index, horarios[index])
        }
    }



    private fun validateIdMateria(idMateria: Int?): String? {
        return when {
            idMateria == null -> "Seleccione una materia"
            else -> null
        }
    }

    private fun validateDiaSemana(diaSemana: String): String? {
        return when {
            diaSemana.isBlank() -> "Seleccione un dia de la semana"
            else -> null
        }
    }

    private fun validateHoraInicio(horas: Int, minutos: Int): String? {
        return when {
            else -> null
        }
    }

    private fun validateHoraFin(index: Int, hora: Int, minutos: Int): String? {
        return when {
            hora == currClases.value[index].horaInicio.hour && minutos == currClases.value[index].horaInicio.minute -> "La hora de fin no puede ser igual a la hora de inicio."
            LocalTime.of(hora, minutos).isBefore(currClases.value[index].horaInicio) -> "La hora de fin debe ser posterior a la hora de inicio"
            else -> null
        }
    }

    private fun validateEdificio(edificio: String): String? {
        return when {
            edificio.length > 20 -> "El edificio no puede tener más de 20 caracteres"
            else -> null
        }
    }

    private fun validateSalon(salon: String): String? {
        return when {
            salon.length > 20 -> "El salón no puede tener más de 20 caracteres"
            else -> null
        }
    }

    fun onIdMateriaChange(index: Int, nuevoIdMateria: Int) {
        _currClases.value = _currClases.value.toMutableList().apply {
            this[index] = this[index].copy(idMateria = nuevoIdMateria)
        }

        _formState.value = _formState.value.toMutableList().apply {
            this[index] = this[index].copy(errorIdMateria = validateIdMateria(nuevoIdMateria))
        }

        _changed.value = true
    }

    fun onDiaSemanaChange(index: Int, nuevoDiaSemana: String) {
        _currClases.value = _currClases.value.toMutableList().apply {
            this[index] = this[index].copy(diaSemana = nuevoDiaSemana)
        }

        _formState.value = _formState.value.toMutableList().apply {
            this[index] = this[index].copy(errorDiaSemana = validateDiaSemana(nuevoDiaSemana))
        }

        _changed.value = true
    }

    fun onHoraInicioChange(index: Int, horas: Int, minutos: Int) {
        _currClases.value = _currClases.value.toMutableList().apply {
            this[index] = this[index].copy(horaInicio = LocalTime.of(horas, minutos))
        }

        _formState.value = _formState.value.toMutableList().apply {
            this[index] = this[index].copy(errorHoraInicio = validateHoraInicio(horas, minutos))
        }

        _changed.value = true
    }

    fun onHoraFinChange(index: Int, horas: Int, minutos: Int) {
        _currClases.value = _currClases.value.toMutableList().apply {
            this[index] = this[index].copy(horaFin = LocalTime.of(horas, minutos))
        }

        _formState.value = _formState.value.toMutableList().apply {
            this[index] = this[index].copy(errorHoraFin = validateHoraFin(index, horas, minutos))
        }

        _changed.value = true
    }

    fun onEdificioChange(index: Int, nuevoEdificio: String) {
        _currClases.value = _currClases.value.toMutableList().apply {
            this[index] = this[index].copy(edificio = nuevoEdificio)
        }

        _formState.value = _formState.value.toMutableList().apply {
            this[index] = this[index].copy(errorEdificio = validateEdificio(nuevoEdificio))
        }

        _changed.value = true
    }

    fun onSalonChange(index: Int, nuevoSalon: String) {
        _currClases.value = _currClases.value.toMutableList().apply {
            this[index] = this[index].copy(salon = nuevoSalon)
        }

        _formState.value = _formState.value.toMutableList().apply {
            this[index] = this[index].copy(errorSalon = validateSalon(nuevoSalon))
        }

        _changed.value = true
    }

    fun setFormState(size: Int) {
        _formState.value = List(size) {
            FormState()
        }
    }

    data class FormState(
        val errorIdMateria: String? = null,
        val errorDiaSemana: String? = null,
        val errorHoraInicio: String? = null,
        val errorHoraFin: String? = null,
        val errorEdificio: String? = null,
        val errorSalon: String? = null
    )
}