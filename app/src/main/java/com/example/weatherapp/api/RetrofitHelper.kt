package com.example.weatherapp.api

import com.example.weatherapp.utils.Utils
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {

    private fun getRetrofit() : Retrofit {

        val retrofit = Retrofit.Builder()
            .baseUrl(Utils.baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit
    }

    fun getRetrofitApi() : ApiInterface {
        return getRetrofit().create(ApiInterface::class.java)
    }
}