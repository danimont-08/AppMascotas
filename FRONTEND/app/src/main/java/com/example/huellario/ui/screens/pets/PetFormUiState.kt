package com.example.huellario.ui.screens.pets

import android.net.Uri

/**
 * Estado UI inmutable para la pantalla de formulario de mascota.
 */
data class PetFormUiState(
    val petId: Int? = null,
    val name: String = "",
    val species: String = "PERRO",
    val specificSpecies: String = "",
    val breed: String = "",
    val birthDate: String = "",
    val photoUri: Uri? = null,
    val existingPhotoUrl: String? = null,
    val nameError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
