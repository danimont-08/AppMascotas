package com.example.huellario.ui.screens.routines

import com.example.huellario.data.model.PetDto
import java.time.LocalDate
import java.time.ZoneId

/**
 * Estado UI inmutable para la pantalla de formulario de rutina.
 */
data class RoutineFormUiState(
    val routineId: Int? = null,
    val petId: Int? = null,
    val pets: List<PetDto> = emptyList(),
    val title: String = "",
    val category: String = "ALIMENTACION",
    val description: String = "",
    val priority: String = "MEDIA",
    val scheduledTime: String = "08:00",
    val frequency: String = "DIARIA",
    val startDate: String = LocalDate.now(ZoneId.of("America/Bogota")).toString(),
    val endDate: String = "",
    val selectedDays: String = "",
    val titleError: String? = null,
    val petIdError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

