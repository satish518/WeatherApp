package com.example.weatherapp.api

sealed class Response<T>(private val data: T? = null, private val errorMessage: String? = null) {
    class Loading<T>() : Response<T>()
    class Success<T>(val data: T?) : Response<T>(data = data)
    class Error<T>(val errorMessage: String) : Response<T>(errorMessage = errorMessage)
}


