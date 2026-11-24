package com.example.huellario.ui.screens.routines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huellario.data.model.ApiResult
import com.example.huellario.data.model.sortedByPriority
import com.example.huellario.data.repository.PetRepository
import com.example.huellario.data.repository.RoutineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel encargado de consultar el resumen diario y gestionar el marcado de rutinas.
 * Acepta un [initialPetId] para pre-filtrar por mascota (ej. desde PetDetailScreen).
 */
class RoutinesViewModel(
    initialPetId: Int? = null,
    private val routineRepository: RoutineRepository = RoutineRepository(),
    private val petRepository: PetRepository = PetRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoutinesUiState(selectedPetId = initialPetId))
    val uiState: StateFlow<RoutinesUiState> = _uiState.asStateFlow()

    // La carga inicial se delega al LaunchedEffect en la pantalla, no aquí
    // para evitar doble carga y respetar el ciclo de vida de Compose.

    fun loadPetsAndDailyRoutines() {
        viewModelScope.launch {
            when (val petsResult = petRepository.getPets()) {
                is ApiResult.Success -> _uiState.update { it.copy(pets = petsResult.data) }
                else -> {}
            }
            loadDailyRoutines()
        }
    }

    fun loadDailyRoutines() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val currentState = _uiState.value

            when (val result = routineRepository.getDailyRoutines(currentState.selectedDate, currentState.selectedPetId)) {
                is ApiResult.Success -> _uiState.update {
                    it.copy(isLoading = false, dailyRoutines = result.data.routines.sortedByPriority(), errorMessage = null)
                }
                is ApiResult.Error -> _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
                else -> {}
            }
        }
    }

    fun onPetFilterSelected(petId: Int?) {
        _uiState.update { it.copy(selectedPetId = petId) }
        loadDailyRoutines()
    }

    fun onDateSelected(date: String) {
        _uiState.update { it.copy(selectedDate = date) }
        loadDailyRoutines()
    }

    fun markLogStatus(routineId: Int, newStatus: String) {
        viewModelScope.launch {
            val date = _uiState.value.selectedDate
            when (val result = routineRepository.setLogStatus(routineId, date, newStatus)) {
                is ApiResult.Success -> loadDailyRoutines()
                is ApiResult.Error -> _uiState.update { it.copy(errorMessage = result.message) }
                else -> {}
            }
        }
    }
}

