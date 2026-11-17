package com.example.huellario.ui.screens.routines

import com.example.huellario.data.model.PetDto
import com.example.huellario.data.model.RoutineDto
import java.time.LocalDate
import java.time.ZoneId

/**
 * Estado UI inmutable para la pantalla de Rutinas y Diario de Cumplimiento.
 * Usa la zona horaria de Colombia (America/Bogota) para la fecha actual.
 */
data class RoutinesUiState(
    val selectedDate: String = LocalDate.now(ZoneId.of("America/Bogota")).toString(),
    val selectedPetId: Int? = null,
    val pets: List<PetDto> = emptyList(),
    val dailyRoutines: List<RoutineDto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

