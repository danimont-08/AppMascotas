package com.example.huellario.ui.screens.routines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huellario.data.model.ApiResult
import com.example.huellario.data.model.RoutineRequest
import com.example.huellario.data.repository.PetRepository
import com.example.huellario.data.repository.RoutineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel encargado del formulario de creación y edición de rutinas.
 */
class RoutineFormViewModel(
    private val routineRepository: RoutineRepository = RoutineRepository(),
    private val petRepository: PetRepository = PetRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoutineFormUiState())
    val uiState: StateFlow<RoutineFormUiState> = _uiState.asStateFlow()

    init {
        loadPets()
    }

    private fun loadPets() {
        viewModelScope.launch {
            when (val result = petRepository.getPets()) {
                is ApiResult.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            pets = result.data,
                            petId = state.petId ?: result.data.firstOrNull()?.id
                        )
                    }
                }
                else -> {}
            }
        }
    }

    fun initForm(routineId: Int?, defaultPetId: Int?) {
        if (defaultPetId != null) {
            _uiState.update { it.copy(petId = defaultPetId) }
        }

        if (routineId != null) {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true) }
                when (val result = routineRepository.getRoutineById(routineId)) {
                    is ApiResult.Success -> {
                        val routine = result.data
                        _uiState.update {
                            it.copy(
                                routineId = routine.id,
                                petId = routine.petId,
                                title = routine.title,
                                category = routine.category,
                                description = routine.description ?: "",
                                scheduledTime = routine.scheduledTime.take(5),
                                frequency = routine.frequency,
                                startDate = routine.startDate,
                                endDate = routine.endDate ?: "",
                                selectedDays = routine.selectedDays ?: "",
                                isLoading = false
                            )
                        }
                    }
                    is ApiResult.Error -> {
                        _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                    }
                    else -> {}
                }
            }
        }
    }

    fun onPetSelected(petId: Int) {
        _uiState.update { it.copy(petId = petId, petIdError = null) }
    }

    fun onTitleChanged(title: String) {
        _uiState.update { it.copy(title = title, titleError = null) }
    }

    fun onCategoryChanged(category: String) {
        _uiState.update { it.copy(category = category) }
    }

    fun onDescriptionChanged(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun onTimeChanged(time: String) {
        _uiState.update { it.copy(scheduledTime = time) }
    }

    fun onFrequencyChanged(frequency: String) {
        _uiState.update { it.copy(frequency = frequency) }
    }

    fun onStartDateChanged(date: String) {
        _uiState.update { it.copy(startDate = date) }
    }

    fun saveRoutine(onSuccess: () -> Unit) {
        val state = _uiState.value
        val title = state.title.trim()
        val petId = state.petId

        var hasError = false
        var titleError: String? = null
        var petIdError: String? = null

        if (title.isEmpty()) {
            titleError = "El nombre de la actividad es obligatorio"
            hasError = true
        }

        if (petId == null) {
            petIdError = "Debes seleccionar una mascota"
            hasError = true
        }

        if (hasError) {
            _uiState.update {
                it.copy(
                    titleError = titleError,
                    petIdError = petIdError
                )
            }
            return
        }

        val request = RoutineRequest(
            petId = petId!!,
            title = title,
            category = state.category,
            description = state.description.trim().ifEmpty { null },
            scheduledTime = state.scheduledTime.ifEmpty { "08:00" },
            frequency = state.frequency,
            startDate = state.startDate,
            endDate = state.endDate.trim().ifEmpty { null },
            selectedDays = state.selectedDays.trim().ifEmpty { null }
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = if (state.routineId != null) {
                routineRepository.updateRoutine(state.routineId, request)
            } else {
                routineRepository.createRoutine(request)
            }

            when (result) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                    onSuccess()
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                else -> {}
            }
        }
    }
}
