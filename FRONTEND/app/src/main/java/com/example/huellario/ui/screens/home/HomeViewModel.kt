package com.example.huellario.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huellario.data.model.ApiResult
import com.example.huellario.data.repository.PetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel encargado de gestionar el estado y carga de mascotas en la pantalla Home.
 */
class HomeViewModel(
    private val petRepository: PetRepository = PetRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadPets()
    }

    fun loadPets() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingPets = true, petsError = null) }

            when (val result = petRepository.getPets()) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoadingPets = false,
                            pets = result.data,
                            petsError = null
                        )
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoadingPets = false,
                            petsError = result.message
                        )
                    }
                }
                is ApiResult.Loading -> {
                    _uiState.update { it.copy(isLoadingPets = true) }
                }
            }
        }
    }

    fun showAddPetNotice() {
        _uiState.update {
            it.copy(featureNoticeMessage = "El formulario para agregar mascotas estará disponible en una funcionalidad posterior.")
        }
    }

    fun clearFeatureNotice() {
        _uiState.update { it.copy(featureNoticeMessage = null) }
    }
}
