package com.example.weatherapp.di

import com.example.weatherapp.api.ApiInterface
import com.example.weatherapp.repositories.CurrentWeatherDataRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ViewModelModule {

    @Singleton
    @Provides
    fun getCurrentWeatherRepository(apiInterface: ApiInterface) : CurrentWeatherDataRepository {
        return CurrentWeatherDataRepository(apiInterface)
    }
}