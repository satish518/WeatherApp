package com.example.weatherapp.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.weatherapp.api.RetrofitService;
import com.example.weatherapp.models.citywiseweather.WeatherDetails;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CityWeatherModel extends ViewModel {

    private MutableLiveData<WeatherDetails> mutableLiveData;

    public CityWeatherModel() {
        mutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<WeatherDetails> getWeatherObserver() {
        return mutableLiveData;
    }

    public void getWeatherDetails(Context context, String cityName, String appid) {
        Call<WeatherDetails> call = RetrofitService.initializeApi().cityWeatherDetails(cityName, appid);

        call.enqueue(new Callback<WeatherDetails>() {
            @Override
            public void onResponse(Call<WeatherDetails> call, Response<WeatherDetails> response) {
                if (response.body() != null) {
                    mutableLiveData.postValue(response.body());
//                    Log.e("Details: ", response.body().getWeather().get(0).getDescription());
                } else {
                    mutableLiveData.setValue(null);
                    Log.e("Error: " , response.code() + " : " + response.message());
                }

            }

            @Override
            public void onFailure(Call<WeatherDetails> call, Throwable t) {
                t.getLocalizedMessage();
                mutableLiveData.setValue(null);
            }
        });
    }


}
