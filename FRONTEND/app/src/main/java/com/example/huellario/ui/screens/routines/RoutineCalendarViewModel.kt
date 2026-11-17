package com.example.huellario.ui.screens.routines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huellario.data.model.ApiResult
import com.example.huellario.data.repository.RoutineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RoutineCalendarViewModel(
    private val routineRepository: RoutineRepository = RoutineRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoutineCalendarUiState())
    val uiState: StateFlow<RoutineCalendarUiState> = _uiState.asStateFlow()

    init {
        loadRoutines()
    }

    fun loadRoutines() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = routineRepository.listRoutines()) {
                is ApiResult.Success -> _uiState.update {
                    it.copy(routines = result.data, isLoading = false)
                }
                is ApiResult.Error -> _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
                is ApiResult.Loading -> Unit
            }
        }
    }
}
