package com.example.collegeclientandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegeclientandroid.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StudyPlanUiState(
    val isLoading: Boolean = false,
    val rows: List<String> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class StudyPlanViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {
    private val _state = MutableStateFlow(StudyPlanUiState())
    val state: StateFlow<StudyPlanUiState> = _state.asStateFlow()

    fun load(group: String) {
        if (group.isBlank()) {
            _state.value = StudyPlanUiState(error = "Группа не указана")
            return
        }

        _state.value = StudyPlanUiState(isLoading = true)
        viewModelScope.launch {
            runCatching { apiService.getStudyPlan(group) }
                .onSuccess { rows ->
                    _state.value = StudyPlanUiState(
                        isLoading = false,
                        rows = rows,
                        error = null
                    )
                }
                .onFailure { e ->
                    _state.value = StudyPlanUiState(
                        isLoading = false,
                        rows = emptyList(),
                        error = "Ошибка загрузки учебного плана: ${e.message}"
                    )
                }
        }
    }
}
