package com.example.weatherapp.di

import dagger.Component


@Component
interface ApplicationComponent {

    fun getWeatherReportComponentFactory() : WeatherReportComponent.Factory

}