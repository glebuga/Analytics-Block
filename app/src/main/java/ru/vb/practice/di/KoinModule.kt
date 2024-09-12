package ru.vb.practice.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.vb.practice.data.Repository
import ru.vb.practice.data.RepositoryImpl
import ru.vb.practice.domain.GetAnalyticsDataUseCase
import ru.vb.practice.presentation.MainViewModel

val appModule = module {
    // Регистрация Repository
    single<Repository> { RepositoryImpl() }

    // Регистрация UseCase
    single { GetAnalyticsDataUseCase(get()) }

    // Регистрация ViewModel
    viewModel { MainViewModel(get()) }
}