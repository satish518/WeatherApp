package com.example.weatherapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.api.Response
import com.example.weatherapp.model.current_weather_data.CurrentWeatherData
import com.example.weatherapp.repositories.CurrentWeatherDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class CurrentWeatherDataViewModel @Inject constructor(private val currentWeatherDataRepository: CurrentWeatherDataRepository) : ViewModel() {

    fun getCurrentWeatherData(latitude: Double, longitude: Double, apiKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            currentWeatherDataRepository.getCurrentWeatherLiveData(latitude, longitude, apiKey)
        }
    }

    val currentWeatherData : LiveData<Response<List<CurrentWeatherData>>>
        get() = currentWeatherDataRepository.currentWeatherLiveData


}