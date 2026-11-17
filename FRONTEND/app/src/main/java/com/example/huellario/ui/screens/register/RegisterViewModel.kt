package com.example.huellario.ui.screens.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huellario.data.model.ApiResult
import com.example.huellario.data.model.UserDto
import com.example.huellario.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel encargado de la lógica y estado del registro del propietario.
 */
class RegisterViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onFullNameChanged(fullName: String) {
        _uiState.update { it.copy(fullName = fullName, fullNameError = null, errorMessage = null) }
    }

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, emailError = null, errorMessage = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null, errorMessage = null) }
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword, confirmPasswordError = null, errorMessage = null) }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun register(onSuccess: (UserDto?) -> Unit) {
        val currentState = _uiState.value
        val fullName = currentState.fullName.trim()
        val email = currentState.email.trim()
        val password = currentState.password
        val confirmPassword = currentState.confirmPassword

        var hasError = false
        var fullNameError: String? = null
        var emailError: String? = null
        var passwordError: String? = null
        var confirmPasswordError: String? = null

        if (fullName.isEmpty()) {
            fullNameError = "El nombre completo es obligatorio"
            hasError = true
        } else if (fullName.length < 2) {
            fullNameError = "Ingresa tu nombre y apellido"
            hasError = true
        }

        if (email.isEmpty()) {
            emailError = "El correo electrónico es obligatorio"
            hasError = true
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Ingresa un correo electrónico válido"
            hasError = true
        }

        if (password.isEmpty()) {
            passwordError = "La contraseña es obligatoria"
            hasError = true
        } else if (password.length < 6) {
            passwordError = "La contraseña debe tener al menos 6 caracteres"
            hasError = true
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordError = "Confirma tu contraseña"
            hasError = true
        } else if (password != confirmPassword) {
            confirmPasswordError = "Las contraseñas no coinciden"
            hasError = true
        }

        if (hasError) {
            _uiState.update {
                it.copy(
                    fullNameError = fullNameError,
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmPasswordError
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = repository.register(fullName, email, password)) {
                is ApiResult.Success -> {
                    val user = result.data.user
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successUser = user
                        )
                    }
                    onSuccess(user)
                }
                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
                is ApiResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }
}
