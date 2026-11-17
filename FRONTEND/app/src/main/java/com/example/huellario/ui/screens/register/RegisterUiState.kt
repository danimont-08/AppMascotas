package com.example.huellario.ui.screens.register

import com.example.huellario.data.model.UserDto

/**
 * Estado UI inmutable para la pantalla de Registro de Propietario.
 */
data class RegisterUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val fullNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successUser: UserDto? = null
)
