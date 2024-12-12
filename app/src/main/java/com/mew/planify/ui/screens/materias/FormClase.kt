package com.mew.planify.ui.screens.materias

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mew.planify.ui.common.DropdownField
import com.mew.planify.ui.common.TextErrorMessage
import com.mew.planify.ui.common.ValidatedTextField
import com.mew.planify.ui.viewmodel.HorarioViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormClase(viewModel: HorarioViewModel, index: Int) {
    val currentTime = Calendar.getInstance()
    val showHoraInicioDialog = remember { mutableStateOf(false) }
    val showHoraFinDialog = remember { mutableStateOf(false) }

    val diasOptions = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes")

    val timePickerStateHoraInicio = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = false,
    )

    val timePickerStateHoraFin = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = false,
    )

    val horaInicioSource = remember {
        MutableInteractionSource()
    }

    if ( horaInicioSource.collectIsPressedAsState().value)
        showHoraInicioDialog.value = true

    val horaFinSource = remember {
        MutableInteractionSource()
    }

    if ( horaFinSource.collectIsPressedAsState().value)
        showHoraFinDialog.value = true

    val formatter = DateTimeFormatter.ofPattern("HH:mm");

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
//        ValidatedTextField(
//            value = viewModel.currClases.collectAsState().value[index].diaSemana,
//            label = "Día de la semana",
//            onValueChange = { viewModel.onDiaSemanaChange(index, it) },
//            errorMessage = viewModel.formState.collectAsState().value[index].errorDiaSemana
//        )

        DropdownField(
            label = "Día de la semana",
            options = diasOptions,
            selectedValue = if (viewModel.currClases.collectAsState().value[index].diaSemana == "") "Seleccione una opción" else viewModel.currClases.collectAsState().value[index].diaSemana,
            onOptionSelected = { _, option -> viewModel.onDiaSemanaChange(index, option) },
            errorMessage = viewModel.formState.collectAsState().value[index].errorDiaSemana
        )


        OutlinedTextField(
            value = viewModel.currClases.collectAsState().value[index].horaInicio.format(formatter),
            onValueChange = { viewModel.onHoraInicioChange(index, timePickerStateHoraInicio.hour, timePickerStateHoraInicio.minute) },
            label = { Text("Hora de inicio") },
            modifier = Modifier.fillMaxWidth(),
            interactionSource = horaInicioSource,
            readOnly = true,
            isError = viewModel.formState.collectAsState().value[index].errorHoraInicio != null
        )

        TextErrorMessage(viewModel.formState.collectAsState().value[index].errorHoraInicio)

        OutlinedTextField(
            value = viewModel.currClases.collectAsState().value[index].horaFin.format(formatter),
            onValueChange = { viewModel.onHoraFinChange(index, timePickerStateHoraFin.hour, timePickerStateHoraFin.minute) },
            label = { Text("Hora de fin") },
            modifier = Modifier.fillMaxWidth(),
            interactionSource = horaFinSource,
            readOnly = true,
            isError = viewModel.formState.collectAsState().value[index].errorHoraFin != null
        )

        TextErrorMessage(viewModel.formState.collectAsState().value[index].errorHoraFin)

        if (showHoraInicioDialog.value) {
            TimePickerDialog(
                onDismiss = { showHoraInicioDialog.value = false },

                onConfirm = {
                    viewModel.onHoraInicioChange(
                        index,
                        timePickerStateHoraInicio.hour,
                        timePickerStateHoraInicio.minute
//                        "${timePickerStateHoraInicio.hour}:${if (timePickerStateHoraInicio.minute == 0) "00" else timePickerStateHoraInicio.minute}"
                    )
                    showHoraInicioDialog.value = false
                }
            ) {
                TimePicker(
                    state = timePickerStateHoraInicio,
                )
            }
        }

        if (showHoraFinDialog.value) {
            TimePickerDialog(
                onDismiss = { showHoraFinDialog.value = false },

                onConfirm = {
                    viewModel.onHoraFinChange(
                        index,
                        timePickerStateHoraFin.hour,
                        timePickerStateHoraFin.minute
//                        "${timePickerStateHoraFin.hour}:${if (timePickerStateHoraFin.minute == 0) "00" else timePickerStateHoraFin.minute}"
                    )
                    showHoraFinDialog.value = false
                }
            ) {
                TimePicker(
                    state = timePickerStateHoraFin,
                )
            }
        }

        ValidatedTextField(
            value = viewModel.currClases.collectAsState().value[index].edificio,
            label = "Edificio",
            onValueChange = { viewModel.onEdificioChange(index, it) },
            errorMessage = viewModel.formState.collectAsState().value[index].errorEdificio
        )

        ValidatedTextField(
            value = viewModel.currClases.collectAsState().value[index].salon,
            label = "Salón",
            onValueChange = { viewModel.onSalonChange(index, it) },
            errorMessage = viewModel.formState.collectAsState().value[index].errorSalon
        )
    }
}
