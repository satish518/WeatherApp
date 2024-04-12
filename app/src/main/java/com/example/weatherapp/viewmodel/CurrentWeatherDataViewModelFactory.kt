package com.example.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.repositories.CurrentWeatherDataRepository
import javax.inject.Inject

class CurrentWeatherDataViewModelFactory @Inject constructor(private val currentWeatherDataRepository: CurrentWeatherDataRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CurrentWeatherDataViewModel(currentWeatherDataRepository) as T
    }
}