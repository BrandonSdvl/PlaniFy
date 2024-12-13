package com.mew.planify.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mew.planify.data.local.dao.MateriaConHorarios
import com.mew.planify.data.local.entities.HorarioEntity
import com.mew.planify.data.local.entities.MateriaEntity
import com.mew.planify.data.repository.HorarioRepository
import com.mew.planify.data.repository.MateriaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MateriaViewModel(
    private val repository: MateriaRepository,
    private val horarioRepository: HorarioRepository
): ViewModel() {
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _formState = mutableStateOf(FormState())
    val formState: State<FormState> = _formState

    private val _materias = MutableStateFlow<List<MateriaEntity>>(emptyList())
    val materias: StateFlow<List<MateriaEntity>> = _materias

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _materia = MutableStateFlow<MateriaEntity>(MateriaEntity())
    val materia: StateFlow<MateriaEntity> = _materia

    private val _changed = MutableStateFlow(false)
    val changed: StateFlow<Boolean> = _changed

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage = _snackbarMessage.asStateFlow()

    init {
        getAll()
    }

    fun clean() {
        _materia.value = MateriaEntity() // Limpiar el formulario
        _formState.value = FormState()
        _changed.value = false
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
                    .collect { materias ->
                        _loading.value = false
                        _materias.value = materias
                    }
            } catch (e: Exception) {
                _loading.value = false
                _error.value = e.message
            }
        }
    }

    fun insertOrUpdate(): Boolean {
        if (validate(materia.value)) {
            viewModelScope.launch {
                try {
                    repository.insertOrUpdate(materia.value)
                    clean()
                    getAll() // Actualizar la lista después de guardar
                } catch (e: Exception) {
                    _error.value = e.message
                }
            }
            _snackbarMessage.value = "Materia agregada exitosamente"
            return true
        } else {
            return false
        }
    }

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }

    fun delete(secuencia: Int) {
        viewModelScope.launch {
            try {
                val materia = repository.findById(secuencia).firstOrNull()

                materia?.let {
                    repository.delete(it)
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    suspend fun insertarMateria(materia: MateriaEntity): Long {
        return repository.insertarMateria(materia)
    }

    suspend fun insertarHorarios(horarios: List<HorarioEntity>) {
        repository.insertarHorarios(horarios)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun insertarMateriaConHorarios(horarios: List<HorarioEntity>): Boolean {
        // Valida que la materia sea válida
        if (validate(materia.value)) {
            // Verifica si la materia tiene un profesor asignado
            val idProfesor = materia.value.idProfesor

            // Si hay un profesor asignado, verifica disponibilidad
            if (idProfesor != null) {
                horarios.forEachIndexed { index, horario ->
                    val horarioConflictivo = horarioRepository.getConflictingHorario(
                        idProfesor = idProfesor,
                        diaSemana = horario.diaSemana,
                        horaInicio = horario.horaInicio,
                        horaFin = horario.horaFin,
                        idMateria = materia.value.id
                    )

                    if (horarioConflictivo != null) {
                        _error.value = "Conflicto: El profesor ya tiene un horario asignado el ${horarioConflictivo.diaSemana} de ${horarioConflictivo.horaInicio} a ${horarioConflictivo.horaFin}."
                        return false
                    }
                }
            }

            // Verificacion de traslape con otras materias
            val conflicto = horarioRepository.verificarTraslapesConOtrasMaterias(
                nuevosHorarios = horarios,
                idMateriaExcluida = materia.value.id,
                repository = horarioRepository
            )

            if (conflicto != null) {
                val materiaConflicto = repository.findById(conflicto[1].idMateria).firstOrNull()
                _error.value = """
                    Conflicto: Se ha detectado un traslape entre las siguientes materias:
                    ${materiaConflicto?.nombre}: ${conflicto[1].diaSemana} de ${conflicto[1].horaInicio} a ${conflicto[1].horaFin}
                    ${materia.value.nombre}: ${conflicto[0].diaSemana} de ${conflicto[0].horaInicio} a ${conflicto[0].horaFin}
                """.trimIndent()
                return false
            }

            // Validacion dentro de la misma materia
            horarios.forEachIndexed { i, horarioActual ->
                horarios.forEachIndexed { j, otroHorario ->
                    if (i != j && // Evita comparar el mismo horario
                        horarioActual.diaSemana == otroHorario.diaSemana &&
                        horarioActual.horaInicio.isBefore(otroHorario.horaFin) &&
                        horarioActual.horaFin.isAfter(otroHorario.horaInicio)
                    ) {
                        _error.value = "Conflicto: Se ha detectado un traslape entre los siguientes horarios " +
                                "\n${horarioActual.diaSemana} de ${horarioActual.horaInicio} a ${horarioActual.horaFin} y" +
                                "\n${otroHorario.diaSemana} de ${otroHorario.horaInicio} a ${otroHorario.horaFin}"
                        return false
                    }
                }
            }

            // Si la validación pasa, inserta la materia con los horarios
            viewModelScope.launch {
                try {
                    repository.insertarMateriaConHorarios(materia.value, horarios)
                    clean()
                    getAll() // Actualiza la lista después de guardar
                } catch (e: Exception) {
                    _error.value = e.message
                }
            }
            _snackbarMessage.value = "Materia guardada exitosamente"
            return true
        } else {
            return false
        }
    }

    fun cleanError() {
        _error.value = null
    }

//    suspend fun insertarMateriaConHorarios(horarios: List<HorarioEntity>): Boolean {
//        if (validate(materia.value)) {
//            viewModelScope.launch {
//                try {
//                    repository.insertarMateriaConHorarios(materia.value, horarios)
//                    clean()
//                    getAll() // Actualizar la lista después de guardar
//                } catch (e: Exception) {
//                    _error.value = e.message
//                }
//            }
//            return true
//        } else {
//            return false
//        }
//    }

    suspend fun obtenerMateriaConHorarios(materiaId: Int): MateriaConHorarios {
        return repository.obtenerMateriaConHorarios(materiaId)
    }

    fun findById(id: Int): Flow<MateriaEntity?> {
        if (id < 0) {
            _error.value = "El ID de la materia no es válido."
            return flowOf(null) // Flujo vacío
        }
        return repository.findById(id)
    }

    fun setMateria(materia: MateriaEntity) {
        _materia.value = materia
    }

    private fun validate(materia: MateriaEntity): Boolean {
        onSecuenciaChange(materia.secuencia)
        onNombreChange(materia.nombre)
        onIdProfesorChange(materia.idProfesor)

        return formState.value.errorSecuencia == null &&
                formState.value.errorNombre == null &&
                formState.value.errorIdProfesor == null
    }

    private fun validateSecuencia(secuencia: String): String? {
        return when {
            secuencia.isBlank() -> "La secuencia no puede estar vacía"
            else -> null
        }
    }

    private fun validateNombre(nombre: String): String? {
        return when {
            nombre.isBlank() -> "El nombre no puede estar vacío"
            else -> null
        }
    }

    private fun validateIdProfesor(idProfesor: Int?): String? {
        return when {
//            idProfesor == null -> "Seleccione un profesor"
            else -> null
        }
    }

    fun onSecuenciaChange(nuevaSecuencia: String) {
        _materia.value = _materia.value.copy(
            secuencia = nuevaSecuencia
        )

        _formState.value = _formState.value.copy(
            errorSecuencia = validateSecuencia(nuevaSecuencia)
        )

        _changed.value = true
    }

    fun onNombreChange(nuevoNombre: String) {
        _materia.value = _materia.value.copy(
            nombre = nuevoNombre
        )

        _formState.value = _formState.value.copy(
            errorNombre = validateNombre(nuevoNombre)
        )

        _changed.value = true
    }

    fun onIdProfesorChange(nuevoIdProfesor: Int?) {
        _materia.value = _materia.value.copy(
            idProfesor = nuevoIdProfesor
        )

        _formState.value = _formState.value.copy(
            errorIdProfesor = validateIdProfesor(nuevoIdProfesor)
        )

        _changed.value = true
    }

    data class FormState(
        val errorSecuencia: String? = null,
        val errorNombre: String? = null,
        val errorIdProfesor: String? = null
    )
}
