package com.example.huellario.ui.screens.routines

import com.example.huellario.data.model.RoutineDto

data class RoutineCalendarUiState(
    val routines: List<RoutineDto> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
