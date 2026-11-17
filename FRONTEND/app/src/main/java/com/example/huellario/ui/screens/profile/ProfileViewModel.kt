package com.example.huellario.ui.screens.profile

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huellario.data.model.ApiResult
import com.example.huellario.data.model.UserDto
import com.example.huellario.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel encargado de la lógica y actualización del perfil del propietario.
 */
class ProfileViewModel(
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun initUserData(user: UserDto?) {
        if (user != null) {
            _uiState.update {
                it.copy(
                    fullName = user.fullName,
                    email = user.email,
                    fullNameError = null,
                    emailError = null,
                    errorMessage = null,
                    successMessage = null
                )
            }
        }
    }

    fun onFullNameChanged(fullName: String) {
        _uiState.update { it.copy(fullName = fullName, fullNameError = null, errorMessage = null, successMessage = null) }
    }

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, emailError = null, errorMessage = null, successMessage = null) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    fun saveProfile(onProfileUpdated: (UserDto) -> Unit) {
        val currentState = _uiState.value
        val fullName = currentState.fullName.trim()
        val email = currentState.email.trim()

        var hasError = false
        var fullNameError: String? = null
        var emailError: String? = null

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

        if (hasError) {
            _uiState.update {
                it.copy(
                    fullNameError = fullNameError,
                    emailError = emailError
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }

            when (val result = userRepository.updateProfile(fullName, email)) {
                is ApiResult.Success -> {
                    val updatedUser = result.data.user
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = result.data.message ?: "Perfil actualizado correctamente",
                            updatedUser = updatedUser
                        )
                    }
                    if (updatedUser != null) {
                        onProfileUpdated(updatedUser)
                    }
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
