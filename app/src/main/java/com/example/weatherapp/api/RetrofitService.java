package com.example.weatherapp.api;

import androidx.lifecycle.MutableLiveData;

import com.example.weatherapp.models.citywiseweather.WeatherDetails;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {

    private static final String BASE_URL = "https://api.openweathermap.org";

    private static Retrofit retrofit;
    public static Retrofit getInstance() {
       if (retrofit == null) {
           retrofit = new Retrofit.Builder()
                   .baseUrl(BASE_URL)
                   .addConverterFactory(GsonConverterFactory.create())
                   .build();
       }
       return retrofit;
    }

    public static ApiInterface initializeApi() {
        ApiInterface apiInterface =RetrofitService.getInstance().create(ApiInterface.class);
        return apiInterface;
    }
}
