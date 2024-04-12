package com.example.weatherapp.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherapp.api.ApiInterface
import com.example.weatherapp.api.Response
import com.example.weatherapp.model.current_weather_data.CurrentWeatherData
import java.lang.Exception
import javax.inject.Inject

class CurrentWeatherDataRepository @Inject constructor(private val apiInterface: ApiInterface) {

    private val currentWeatherMutableLiveData : MutableLiveData<Response<List<CurrentWeatherData>>> = MutableLiveData<Response<List<CurrentWeatherData>>>()

    val currentWeatherLiveData: LiveData<Response<List<CurrentWeatherData>>>
        get() = currentWeatherMutableLiveData

    suspend fun getCurrentWeatherLiveData(latitude: Double, longitude: Double, apiKey: String) {
        try {

            val result = apiInterface.getCurrentWeatherData(latitude, longitude, apiKey)

            if (result.isSuccessful && result.body() != null) {
//                Log.d("ResponseData: ", result.body().toString())

                currentWeatherMutableLiveData.postValue(Response.Success(listOf(result.body()!!)))
            } else {
                currentWeatherMutableLiveData.postValue(Response.Error(result.message()))
            }

        } catch (e: Exception) {
            currentWeatherMutableLiveData.postValue(Response.Error(e.message.toString()))
        }
    }

}