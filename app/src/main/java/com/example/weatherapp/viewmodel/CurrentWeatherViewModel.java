package com.example.weatherapp.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.weatherapp.api.RetrofitService;
import com.example.weatherapp.models.citywiseweather.WeatherDetails;
import com.example.weatherapp.models.latlngwiseweather.CurLocationWeatherDetails;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CurrentWeatherViewModel extends ViewModel {
    private MutableLiveData<CurLocationWeatherDetails> mutableLiveData;

    public CurrentWeatherViewModel() {
        mutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<CurLocationWeatherDetails> getCurWeatherObserver() {
        return mutableLiveData;
    }

    public void getCurWeatherDetails(String lat, String lon, String appid) {
        Call<CurLocationWeatherDetails> call = RetrofitService.initializeApi().curLocationWeatherDetails(lat, lon, appid);

        call.enqueue(new Callback<CurLocationWeatherDetails>() {
            @Override
            public void onResponse(Call<CurLocationWeatherDetails> call, Response<CurLocationWeatherDetails> response) {
                if (response.body() != null) {
                    mutableLiveData.postValue(response.body());
                } else {
                    mutableLiveData.setValue(null);
                    Log.e("Error: " , response.code() + " : " + response.message());
                }

            }

            @Override
            public void onFailure(Call<CurLocationWeatherDetails> call, Throwable t) {
                t.getLocalizedMessage();
                mutableLiveData.setValue(null);
            }
        });
    }
}
