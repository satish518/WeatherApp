package com.example.weatherapp.api;

import com.example.weatherapp.models.citywiseweather.WeatherDetails;
import com.example.weatherapp.models.latlngwiseweather.CurLocationWeatherDetails;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("/data/2.5/weather")
    Call<WeatherDetails> cityWeatherDetails(@Query("q") String cityName, @Query("appid") String appId);

    @GET("/data/2.5/weather")
    Call<CurLocationWeatherDetails> curLocationWeatherDetails(@Query("lat") String lat, @Query("lon") String lon, @Query("appid") String appId);



}
