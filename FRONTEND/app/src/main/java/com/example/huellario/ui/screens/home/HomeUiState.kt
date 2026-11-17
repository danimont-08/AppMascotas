package com.example.huellario.ui.screens.home

import com.example.huellario.data.model.PetDto

/**
 * Estado de UI inmutable para la pantalla Home.
 */
data class HomeUiState(
    val pets: List<PetDto> = emptyList(),
    val isLoadingPets: Boolean = false,
    val petsError: String? = null,
    val featureNoticeMessage: String? = null
)
