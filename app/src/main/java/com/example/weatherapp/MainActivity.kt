package com.example.weatherapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.api.Response
import com.example.weatherapp.api.RetrofitHelper
import com.example.weatherapp.repositories.CurrentWeatherDataRepository
import com.example.weatherapp.utils.Utils
import com.example.weatherapp.viewmodel.CurrentWeatherDataViewModel
import com.example.weatherapp.viewmodel.CurrentWeatherDataViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.Util
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var currentWeatherDataViewModel: CurrentWeatherDataViewModel

    lateinit var locationManager: LocationManager



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        (application as MyApplication).applicationComponent.getWeatherReportComponentFactory().create(this, 3).inject(this)

//        val currentWeatherDataRepository = CurrentWeatherDataRepository(RetrofitHelper.getRetrofitApi())
//        currentWeatherDataViewModel = ViewModelProvider(this, currentWeatherDataViewModelFactory).get(CurrentWeatherDataViewModel::class.java)

    }

    override fun onResume() {
        super.onResume()

        if (Utils.checkPermission(this)) {
            getCurrentWeatherResponse()
        } else {
            Utils.requestPermission(this)
        }

    }

    fun getCurrentWeatherResponse() {

        Utils.getLocation(this, locationManager)

        Utils.currentLocation?.latitude?.let { latitude ->
            Utils.currentLocation?.longitude?.let {
                    longitude ->
                currentWeatherDataViewModel.getCurrentWeatherData(latitude, longitude, Utils.WEATHER_API_KEY)
            }
        }

        currentWeatherDataViewModel.currentWeatherData.observe(this, Observer {

            when(it) {
                is Response.Loading -> {}
                is Response.Success -> {
                    it.data?.let {
                        Log.d("Result: ", it.joinToString())
                    }
                }

                is Response.Error -> {
                    Toast.makeText(this, it.errorMessage, Toast.LENGTH_SHORT).show()
                    Log.d("Error: ", it.errorMessage)
                }
            }
        })

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == Utils.LOCATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                getCurrentWeatherResponse()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                Utils.requestPermission(this)
            }
        }
    }
}