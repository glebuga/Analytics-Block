package ru.vb.practice.domain

import kotlinx.coroutines.flow.Flow
import ru.vb.practice.data.AnalyticsData
import ru.vb.practice.data.Repository
import java.time.LocalDate

class GetAnalyticsDataUseCase(private val repository: Repository) {
    suspend operator fun invoke(startDate: LocalDate, endDate: LocalDate): Flow<AnalyticsData> {
        return repository.getAnalyticsData(startDate, endDate)
    }
}