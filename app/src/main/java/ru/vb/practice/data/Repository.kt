package ru.vb.practice.data

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface Repository {
    fun getAnalyticsData(startDate: LocalDate, endDate: LocalDate): Flow<AnalyticsData>
}