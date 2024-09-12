package ru.vb.practice.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.vb.practice.data.AnalyticsData
import ru.vb.practice.domain.GetAnalyticsDataUseCase
import java.time.LocalDate

class MainViewModel(private val getAnalyticsDataUseCase: GetAnalyticsDataUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState(isLoading = true))
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        // Устанавливаем начальный диапазон дат
        val today = LocalDate.now()
        val defaultStartDate = today.minusMonths(1)
        getAnalyticsData(defaultStartDate, today)
    }

    fun getAnalyticsData(startDate: LocalDate, endDate: LocalDate) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                getAnalyticsDataUseCase(startDate, endDate).collect { data ->
                    _uiState.value = _uiState.value.copy(isLoading = false, data = data)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error loading data: ${e.message}"
                )
            }
        }
    }
}