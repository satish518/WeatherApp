package com.example.weatherapp.di

import android.content.Context
import com.example.weatherapp.MainActivity
import dagger.BindsInstance
import dagger.Subcomponent
import javax.inject.Singleton

@Singleton
@Subcomponent(modules = [NetworkModule::class, ViewModelModule::class])
interface WeatherReportComponent {

    fun inject(activity: MainActivity)


    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance context: Context, @BindsInstance retryCount: Int) : WeatherReportComponent

    }
}