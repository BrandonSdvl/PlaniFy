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

    init {
        obtenerProfesores()
    }

    fun limipiarProfesor() {
        _profesor.value = ProfesorEntity() // Limpiar el formulario después de guardar
    }

    private fun obtenerProfesores() {
        viewModelScope.launch {
            _loading.value = true // Mostrar indicador de carga

            try {
                repository.obtenerProfesores()
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

    fun guardarProfesor(): Boolean {
        if (validarProfesor(profesor.value)) {
            viewModelScope.launch {
                try {
                    repository.insertProfesor(profesor.value)
                    limipiarProfesor()
                    obtenerProfesores() // Actualizar la lista después de guardar
                } catch (e: Exception) {
                    _error.value = e.message
                }
            }
            return true
        } else {
            return false
        }
    }

    fun eliminarProfesor(profesorId: Int) {
        viewModelScope.launch {
            try {
                val profesor = repository.obtenerProfesorPorId(profesorId).firstOrNull()
                profesor?.let {
                    repository.eliminarProfesor(it)
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun obtenerProfesorPorId(profesorId: Int): Flow<ProfesorEntity?> {
        if(profesorId < 0) {
            _error.value = "El ID del profesor no es válido."
            return flowOf(null) // Flujo vacío
        }
        return repository.obtenerProfesorPorId(profesorId)
    }

    fun setProfesor(profesor: ProfesorEntity) {
        _profesor.value = profesor
    }


    private fun validarProfesor(profesor: ProfesorEntity): Boolean {
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

    private fun validarNombre(nombre: String): String? {
        return when {
            nombre.isBlank() -> "El nombre no puede estar vacío"
            nombre.length > 40 -> "El nombre no puede tener más de 40 caracteres"
            else -> null
        }
    }

    private fun validarCorreo(correo: String): String? {
        return when {
            correo.isBlank() -> null
            correo.length > 40 -> "El correo no puede tener más de 40 caracteres"
            !correo.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")) ->
                "El correo tiene un formato inválido"
            else -> null
        }
    }

    private fun validarTelefono(telefono: String): String? {
        return when {
            telefono.isBlank() -> null
            telefono.length > 13 -> "El teléfono no puede tener más de 13 caracteres"
            !telefono.matches(Regex("^\\d+\$")) -> "El teléfono solo puede contener números"
            else -> null
        }
    }

    private fun validarCubiculo(cubiculo: String): String? {
        return when {
            cubiculo.isBlank() -> null
            !cubiculo.matches(Regex("^\\d+\$")) -> "El cubiculo solo puede contener números"
            else -> null
        }
    }

    private fun validarAcademia(academia: String): String? {
        return when {
            academia.length > 20 -> "La academia no puede tener más de 20 caracteres"
            else -> null
        }
    }

    private fun validarNota(nota: String): String? {
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
            errorNombre = validarNombre(nuevoNombre)
        )
    }

    fun onCorreoChange(nuevoCorreo: String) {
        _profesor.value = _profesor.value.copy(
            correo = nuevoCorreo
        )

        _formState.value = _formState.value.copy(
            errorCorreo = validarCorreo(nuevoCorreo)
        )
    }

    fun onTelefonoChange(nuevoTelefono: String) {
        _profesor.value = _profesor.value.copy(
            telefono = nuevoTelefono
        )

        _formState.value = _formState.value.copy(
            errorTelefono = validarTelefono(nuevoTelefono)
        )
    }

    fun onCubiculoChange(nuevoCubiculo: String) {
        _profesor.value = _profesor.value.copy(
            cubiculo = nuevoCubiculo
        )

        _formState.value = _formState.value.copy(
            errorCubiculo = validarCubiculo(nuevoCubiculo)
        )
    }

    fun onAcademiaChange(nuevaAcademia: String) {
        _profesor.value = _profesor.value.copy(
            academia = nuevaAcademia
        )

        _formState.value = _formState.value.copy(
            errorAcademia = validarAcademia(nuevaAcademia)
        )
    }

    fun onNotaChange(nuevaNota: String) {
        _profesor.value = _profesor.value.copy(
            nota = nuevaNota
        )

        _formState.value = _formState.value.copy(
            errorNota = validarNota(nuevaNota)
        )
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

