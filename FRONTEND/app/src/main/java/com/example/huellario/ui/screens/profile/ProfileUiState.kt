package com.example.huellario.ui.screens.profile

import com.example.huellario.data.model.UserDto

/**
 * Estado de UI inmutable para la pantalla Mi Perfil.
 */
data class ProfileUiState(
    val fullName: String = "",
    val email: String = "",
    val fullNameError: String? = null,
    val emailError: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val updatedUser: UserDto? = null
)
