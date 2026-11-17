package com.example.huellario.ui.screens.pets

import com.example.huellario.data.model.PetDto

/**
 * Estado UI inmutable para la pantalla Mis Mascotas.
 */
data class PetsUiState(
    val pets: List<PetDto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
