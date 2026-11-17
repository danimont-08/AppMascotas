package com.example.huellario.ui.screens.login

import com.example.huellario.data.model.UserDto

/**
 * Estado UI inmutable para la pantalla de Inicio de Sesión.
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successUser: UserDto? = null
)
