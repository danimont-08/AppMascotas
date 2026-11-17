package com.example.huellario.ui.screens.pets

import com.example.huellario.data.model.PetDto

/**
 * Estado UI inmutable para el detalle de la mascota.
 */
data class PetDetailUiState(
    val pet: PetDto? = null,
    val isLoading: Boolean = false,
    val isDeleting: Boolean = false,
    val isDeleteSuccess: Boolean = false,
    val errorMessage: String? = null
)
