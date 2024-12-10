package com.mew.planify.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mew.planify.data.local.entities.MateriaEntity
import com.mew.planify.data.repository.MateriaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MateriaViewModel(
    private val repository: MateriaRepository
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

    init {
        getAll()
    }

    fun clean() {
        _materia.value = MateriaEntity() // Limpiar el formulario
        _formState.value = FormState()
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
            return true
        } else {
            return false
        }
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
    }

    fun onNombreChange(nuevoNombre: String) {
        _materia.value = _materia.value.copy(
            nombre = nuevoNombre
        )

        _formState.value = _formState.value.copy(
            errorNombre = validateNombre(nuevoNombre)
        )
    }

    fun onIdProfesorChange(nuevoIdProfesor: Int?) {
        _materia.value = _materia.value.copy(
            idProfesor = nuevoIdProfesor
        )

        _formState.value = _formState.value.copy(
            errorIdProfesor = validateIdProfesor(nuevoIdProfesor)
        )
    }

    data class FormState(
        val errorSecuencia: String? = null,
        val errorNombre: String? = null,
        val errorIdProfesor: String? = null
    )
}
