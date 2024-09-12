package ru.vb.practice.data

data class AnalyticsData(
    val productSales: Int,
    val targetProductSales: Int,
    val averageCheck: Int,
    val visitPercentageWithAdditionalServices: Int,
    val dailyRevenue: Int,
    val returnRate: Int,
    val visits: Int,
    val salaryPercentage: Int,
    val mainServicesRevenue: Int,
    val additionalServicesRevenue: Int,
    val productRevenue: Int
)