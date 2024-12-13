package com.mew.planify.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mew.planify.data.local.entities.ProfesorEntity
import com.mew.planify.data.repository.ProfesorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfesorViewModel(
    private val repository: ProfesorRepository
) : ViewModel() {
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _formState = mutableStateOf(FormState())
    val formState: State<FormState> = _formState

    private val _profesores = MutableStateFlow<List<ProfesorEntity>>(emptyList())
    val profesores: StateFlow<List<ProfesorEntity>> = _profesores

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _profesor = MutableStateFlow<ProfesorEntity>(ProfesorEntity())
    val profesor: StateFlow<ProfesorEntity> = _profesor

    private val _changed = MutableStateFlow(false)
    val changed: StateFlow<Boolean> = _changed

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage = _snackbarMessage.asStateFlow()

    init {
        getAll()
    }

    fun clean() {
        _profesor.value = ProfesorEntity() // Limpiar el formulario
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
                    .collect { profesores ->
                        _loading.value = false
                        _profesores.value = profesores
                    }
            } catch (e: Exception) {
                _loading.value = false
                _error.value = e.message
            }
        }
    }

    fun insertOrUpdate(): Boolean {
        if (validate(profesor.value)) {
            viewModelScope.launch {
                try {
                    repository.insertOrUpdate(profesor.value)
                    clean()
                    getAll() // Actualizar la lista después de guardar
                } catch (e: Exception) {
                    _error.value = e.message
                }
            }
            _snackbarMessage.value = "Profesor guardado exitosamente"
            return true
        } else {
            return false
        }
    }

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }

    fun delete(profesorId: Int) {
        viewModelScope.launch {
            try {
                val profesor = repository.findById(profesorId).firstOrNull()
                profesor?.let {
                    repository.delete(it)
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun findById(profesorId: Int): Flow<ProfesorEntity?> {
        if(profesorId < 0) {
            _error.value = "El ID del profesor no es válido."
            return flowOf(null) // Flujo vacío
        }
        return repository.findById(profesorId)
    }

    fun setProfesor(profesor: ProfesorEntity) {
        _profesor.value = profesor
    }


    private fun validate(profesor: ProfesorEntity): Boolean {
        onNombreChange(profesor.nombre)
        onCorreoChange(profesor.correo ?: "")
        onTelefonoChange(profesor.telefono ?: "")
        onCubiculoChange(profesor.cubiculo ?: "")
        onAcademiaChange(profesor.academia ?: "")
        onNotaChange(profesor.nota ?: "")

        return formState.value.errorNombre == null &&
                formState.value.errorCorreo == null &&
                formState.value.errorTelefono == null &&
                formState.value.errorCubiculo == null &&
                formState.value.errorAcademia == null &&
                formState.value.errorNota == null
    }

    private fun validateNombre(nombre: String): String? {
        return when {
            nombre.isBlank() -> "El nombre no puede estar vacío"
            nombre.length > 40 -> "El nombre no puede tener más de 40 caracteres"
            else -> null
        }
    }

    private fun validateCorreo(correo: String): String? {
        return when {
            correo.isBlank() -> null
            correo.length > 30 -> "El correo no puede tener más de 30 caracteres"
            !correo.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")) ->
                "El correo tiene un formato inválido"
            else -> null
        }
    }

    private fun validateTelefono(telefono: String): String? {
        return when {
            telefono.isBlank() -> null
            telefono.length > 13 -> "El teléfono no puede tener más de 13 caracteres"
            !telefono.matches(Regex("^\\d+\$")) -> "El teléfono solo puede contener números"
            else -> null
        }
    }

    private fun validateCubiculo(cubiculo: String): String? {
        return when {
            cubiculo.isBlank() -> null
            !cubiculo.matches(Regex("^\\d+\$")) -> "El cubiculo solo puede contener números"
            else -> null
        }
    }

    private fun validateAcademia(academia: String): String? {
        return when {
            academia.length > 20 -> "La academia no puede tener más de 20 caracteres"
            else -> null
        }
    }

    private fun validateNota(nota: String): String? {
        return when {
            nota.length > 300 -> "La nota no puede tener más de 300 caracteres"
            else -> null
        }
    }

    fun onNombreChange(nuevoNombre: String) {
        _profesor.value = _profesor.value.copy(
            nombre = nuevoNombre
        )

        _formState.value = _formState.value.copy(
            errorNombre = validateNombre(nuevoNombre)
        )

        _changed.value = true
    }

    fun onCorreoChange(nuevoCorreo: String) {
        _profesor.value = _profesor.value.copy(
            correo = nuevoCorreo
        )

        _formState.value = _formState.value.copy(
            errorCorreo = validateCorreo(nuevoCorreo)
        )

        _changed.value = true
    }

    fun onTelefonoChange(nuevoTelefono: String) {
        _profesor.value = _profesor.value.copy(
            telefono = nuevoTelefono
        )

        _formState.value = _formState.value.copy(
            errorTelefono = validateTelefono(nuevoTelefono)
        )

        _changed.value = true
    }

    fun onCubiculoChange(nuevoCubiculo: String) {
        _profesor.value = _profesor.value.copy(
            cubiculo = nuevoCubiculo
        )

        _formState.value = _formState.value.copy(
            errorCubiculo = validateCubiculo(nuevoCubiculo)
        )

        _changed.value = true
    }

    fun onAcademiaChange(nuevaAcademia: String) {
        _profesor.value = _profesor.value.copy(
            academia = nuevaAcademia
        )

        _formState.value = _formState.value.copy(
            errorAcademia = validateAcademia(nuevaAcademia)
        )

        _changed.value = true
    }

    fun onNotaChange(nuevaNota: String) {
        _profesor.value = _profesor.value.copy(
            nota = nuevaNota
        )

        _formState.value = _formState.value.copy(
            errorNota = validateNota(nuevaNota)
        )

        _changed.value = true
    }

    data class FormState(
        val errorNombre: String? = null,
        val errorCorreo: String? = null,
        val errorTelefono: String? = null,
        val errorCubiculo: String? = null,
        val errorAcademia: String? = null,
        val errorNota: String? = null
    )
}

