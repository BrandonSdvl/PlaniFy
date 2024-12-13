package com.mew.planify.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mew.planify.data.local.entities.TareaEntity
import com.mew.planify.data.repository.TareaRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date

class TareaViewModel(
    private val repository: TareaRepository
) : ViewModel() {
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _formState = mutableStateOf(FormState())
    val formState: State<FormState> = _formState

    private val _tareas = MutableStateFlow<List<TareaEntity>>(emptyList())
    val tareas: StateFlow<List<TareaEntity>> = _tareas

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _tarea = MutableStateFlow<TareaEntity>(TareaEntity())
    val tarea: StateFlow<TareaEntity> = _tarea

    private val _changed = MutableStateFlow(false)
    val changed: StateFlow<Boolean> = _changed

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage = _snackbarMessage.asStateFlow()

    init {
        getAll()
    }

    fun clean() {
        _tarea.value = TareaEntity() // Limpiar el formulario
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
                    .collect { tareas ->
                        _loading.value = false
                        _tareas.value = tareas
                    }
            } catch (e: Exception) {
                _loading.value = false
                _error.value = e.message
            }
        }
    }

    fun insertOrUpdate(): Boolean {
        if (validateTarea(tarea.value)) {
            viewModelScope.launch {
                try {
                    repository.insertOrUpdate(tarea.value)
                    clean()
                    getAll() // Actualizar la lista después de guardar
                } catch (e: Exception) {
                    _error.value = e.message
                }
            }
            _snackbarMessage.value = "Tarea guardada exitosamente"
            return true
        } else {
            return false
        }
    }

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }

    fun delete(tareaId: Int) {
        viewModelScope.launch {
            try {
                val tarea = repository.findById(tareaId).firstOrNull()
                tarea?.let {
                    repository.delete(it)
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun findById(id: Int): Flow<TareaEntity?> {
        if (id < 0) {
            _error.value = "El ID de la tarea no es válido."
            return flowOf(null) // Flujo vacío
        }
        return repository.findById(id)
    }

    fun setTarea(tarea: TareaEntity) {
        _tarea.value = tarea
    }



//    VALIDACIONES
    private fun validateTarea(tarea: TareaEntity): Boolean {
        onIdMateriaChange(tarea.idMateria)
        onTituloChange(tarea.titulo)
        onDescripcionChange(tarea.descripcion)
        onEstatusChange(tarea.estatus)
        onPrioridadChange(tarea.prioridad)
        onFechaEntregaChange(tarea.fechaEntrega)

        return formState.value.errorIdMateria == null &&
                formState.value.errorTitulo == null &&
                formState.value.errorDescripcion == null &&
                formState.value.errorEstatus == null &&
                formState.value.errorPrioridad == null &&
                formState.value.errorFechaEntrega == null
    }

    private fun validateIdMateria(idMateria: Int?): String? {
        return when {
//            idMateria == null -> "Seleccione una materia"
            else -> null
        }
    }

    private fun validateTitulo(titulo: String): String? {
        return when {
            titulo.isBlank() -> "El título no puede estar vacío"
            titulo.length > 50 -> "El título no puede tener más de 50 caracteres"
            else -> null
        }
    }

    private fun validateDescripcion(descripcion: String): String? {
        return when {
            descripcion.isBlank() -> "La descripción no puede estar vacía"
            descripcion.length > 400 -> "La descripción no puede superar los 200 caracteres"
            else -> null
        }
    }

    private fun validateEstatus(estatus: String): String? {
        return if (estatus.isBlank()) "Seleccione una opción" else null
    }

    private fun validatePrioridad(prioridad: String): String? {
        return if (prioridad.isBlank()) "Seleccione una opción" else null
    }

    private fun validateFechaEntrega(fechaEntrega: Date?): String? {
        return if (fechaEntrega == null) "La fecha de entrega no puede estar vacía" else null
    }

    fun onIdMateriaChange(nuevoIdMateria: Int?) {
        _tarea.value = _tarea.value.copy(
            idMateria = nuevoIdMateria
        )

        _formState.value = _formState.value.copy(
            errorIdMateria = validateIdMateria(nuevoIdMateria)
        )

        _changed.value = true
    }

    fun onTituloChange(nuevoTitulo: String) {
        _tarea.value = _tarea.value.copy(
            titulo = nuevoTitulo
        )

        _formState.value = _formState.value.copy(
            errorTitulo = validateTitulo(nuevoTitulo)
        )

        _changed.value = true
    }

    fun onDescripcionChange(nuevaDescripcion: String) {
        _tarea.value = _tarea.value.copy(
            descripcion = nuevaDescripcion
        )

        _formState.value = _formState.value.copy(
            errorDescripcion = validateDescripcion(nuevaDescripcion)
        )

        _changed.value = true
    }

    fun onEstatusChange(nuevoEstatus: String) {
        _tarea.value = _tarea.value.copy(estatus = nuevoEstatus)

        // Validar el campo de estatus
        _formState.value = _formState.value.copy(
            errorEstatus = validateEstatus(nuevoEstatus)
        )

        _changed.value = true
    }

    fun onPrioridadChange(nuevaPrioridad: String) {
        _tarea.value = _tarea.value.copy(prioridad = nuevaPrioridad)

        // Validar el campo de prioridad
        _formState.value = _formState.value.copy(
            errorPrioridad = validatePrioridad(nuevaPrioridad)
        )

        _changed.value = true
    }

    fun onFechaEntregaChange(nuevaFechaEntrega: Date?) {
        _tarea.value = _tarea.value.copy(fechaEntrega = nuevaFechaEntrega)

        // Validar el campo de fecha límite
        _formState.value = _formState.value.copy(
            errorFechaEntrega = validateFechaEntrega(nuevaFechaEntrega)
        )

        _changed.value = true
    }

    data class FormState(
        val errorIdMateria: String? = null,
        val errorTitulo: String? = null,
        val errorDescripcion: String? = null,
        val errorEstatus: String? = null,
        val errorPrioridad: String? = null,
        val errorFechaEntrega: String? = null
    )

}

