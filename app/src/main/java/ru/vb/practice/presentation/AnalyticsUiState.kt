package ru.vb.practice.presentation

import ru.vb.practice.data.AnalyticsData

data class AnalyticsUiState(
    val isLoading: Boolean = false,
    val data: AnalyticsData? = null,
    val errorMessage: String? = null
)