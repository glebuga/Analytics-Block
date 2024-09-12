package ru.vb.practice.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.time.LocalDate
import kotlin.random.Random

class RepositoryImpl : Repository {
    override fun getAnalyticsData(startDate: LocalDate, endDate: LocalDate): Flow<AnalyticsData> = flow {
        delay(5000) // Имитация задержки при запросе данных
        emit(
            AnalyticsData(
                productSales = Random.nextInt(1000, 20000),
                targetProductSales = Random.nextInt(20000, 50000),
                averageCheck = Random.nextInt(1000, 5000),
                visitPercentageWithAdditionalServices = Random.nextInt(10, 50),
                dailyRevenue = Random.nextInt(10000, 100000),
                returnRate = Random.nextInt(0, 50),
                visits = Random.nextInt(100, 500),
                salaryPercentage = Random.nextInt(30, 60),
                mainServicesRevenue = Random.nextInt(10000, 100000),
                additionalServicesRevenue = Random.nextInt(100000, 1000000),
                productRevenue = Random.nextInt(0, 10000),
            )
        )
    }.flowOn(Dispatchers.IO)
}