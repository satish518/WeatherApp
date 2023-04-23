package com.example.weatherapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.weatherapp.databinding.ActivityMainBinding;
import com.example.weatherapp.viewmodel.CityWeatherModel;
import com.example.weatherapp.viewmodel.CurrentWeatherViewModel;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String appID = "c9b6c69b57713fda21579671f994dac3";

    CityWeatherModel cityWeatherModel;
    CurrentWeatherViewModel currentWeatherViewModel;

    FusedLocationProviderClient fusedClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mCallback;

    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */

    boolean appLocationPermission = false;

    public static final int MULTIPLE_PERMISSIONS = 10;
    String[] permissions = new String[]{
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
    };

    double currentlatitude = 0.0, currentlongitude = 0.0;

    ActivityMainBinding binding;
    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//         setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {

        sessionManager = new SessionManager(getApplicationContext());

        cityWeatherModel = new ViewModelProvider(this).get(CityWeatherModel.class);
        currentWeatherViewModel = new ViewModelProvider(this).get(CurrentWeatherViewModel.class);

        binding.detailsLayout.setVisibility(View.GONE);

        if (!TextUtils.isEmpty(sessionManager.getLastSearchDetails().get(SessionManager.CITY_NAME))) {
            if (Internet.isNetworkConnected(getApplicationContext())) {
                cityWeatherModel.getWeatherDetails(getApplicationContext(), sessionManager.getLastSearchDetails().get(SessionManager.CITY_NAME), appID);
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection Found!", Toast.LENGTH_SHORT).show();
            }
        }

        binding.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(binding.searchCityEdt.getText().toString().trim())) {
                    Toast.makeText(MainActivity.this, "Please Enter US City Name", Toast.LENGTH_SHORT).show();
                } else {
                    if (Internet.isNetworkConnected(getApplicationContext())) {
                        cityWeatherModel.getWeatherDetails(getApplicationContext(), binding.searchCityEdt.getText().toString().trim(), appID);
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection Found!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

         cityWeatherModel.getWeatherObserver().observe(this, weatherDetails -> {

             if (weatherDetails != null) {
                 if(weatherDetails.getCod() == 200 && weatherDetails.getSys().getCountry().equalsIgnoreCase("US")) {
                     binding.detailsLayout.setVisibility(View.VISIBLE);
                     String imageLink = "https://openweathermap.org/img/wn/"+weatherDetails.getWeather().get(0).getIcon()+"@2x.png";
                     Glide.with(getApplicationContext()).load(imageLink).into(binding.weatherImgView);
                     sessionManager.setLastSearchDetails(weatherDetails.getName());
                     binding.cityNameTxt.setText("City Name: " + weatherDetails.getName());
                     binding.latlngTxt.setText("Coordinates: " + weatherDetails.getCoord().getLat() + "," + weatherDetails.getCoord().getLon());
                     binding.weatherTxt.setText("Weather: " + weatherDetails.getWeather().get(0).getDescription());
                     binding.tempNameTxt.setText("Temperature: " + weatherDetails.getMain().getTemp() + "(Min. " + weatherDetails.getMain().getTempMin() + ", Max: " + weatherDetails.getMain().getTempMax() + ")");
                     binding.visibilityTxt.setText("Visibility: " + weatherDetails.getVisibility());
                     binding.windTxt.setText("Wind Speed: " + weatherDetails.getWind().getSpeed());
                     binding.sunriseTxt.setText("Sunrise: " + weatherDetails.getSys().getSunrise());
                     binding.sunsetTxt.setText("Sunset: " + weatherDetails.getSys().getSunset());
                     binding.timezoneTxt.setText("Timezone: " + weatherDetails.getTimezone());

                 } else {
                     binding.detailsLayout.setVisibility(View.GONE);
                     Toast.makeText(this, "City Not Found!", Toast.LENGTH_SHORT).show();
                 }
             } else {
                 binding.detailsLayout.setVisibility(View.GONE);
                 Toast.makeText(this, "City Not Found!", Toast.LENGTH_SHORT).show();
             }

        });

        initLocation();

        currentWeatherViewModel.getCurWeatherObserver().observe(this, weatherDetails -> {

            if (weatherDetails != null) {
                    binding.detailsLayout.setVisibility(View.VISIBLE);
                    String imageLink = "https://openweathermap.org/img/wn/"+weatherDetails.getWeather().get(0).getIcon()+"@2x.png";
                    Glide.with(getApplicationContext()).load(imageLink).into(binding.weatherImgView);
                    binding.cityNameTxt.setText("City Name: " + weatherDetails.getName());
                    binding.latlngTxt.setText("Coordinates: " + weatherDetails.getCoord().getLat() + "," + weatherDetails.getCoord().getLon());
                    binding.weatherTxt.setText("Weather: " + weatherDetails.getWeather().get(0).getDescription());
                    binding.tempNameTxt.setText("Temperature: " + weatherDetails.getMain().getTemp() + "(Min. " + weatherDetails.getMain().getTempMin() + ", Max: " + weatherDetails.getMain().getTempMax() + ")");
                    binding.visibilityTxt.setText("Visibility: " + weatherDetails.getVisibility());
                    binding.windTxt.setText("Wind Speed: " + weatherDetails.getWind().getSpeed());
                    binding.sunriseTxt.setText("Sunrise: " + weatherDetails.getSys().getSunrise());
                    binding.sunsetTxt.setText("Sunset: " + weatherDetails.getSys().getSunset());
                    binding.timezoneTxt.setText("Timezone: " + weatherDetails.getTimezone());


            } else {
                binding.detailsLayout.setVisibility(View.GONE);

            }

        });

    }

    private void initLocation() {
        mCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.d(TAG, "locationResult null");
                    return;
                }

                for (Location loc : locationResult.getLocations()) {
                    currentlatitude = loc.getLatitude();
                    currentlongitude = loc.getLongitude();
                }

                Log.d(TAG, "Lat: " + currentlatitude + ", Lng: " + currentlongitude);
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                Log.d(TAG, "locationAvailability is " + locationAvailability.isLocationAvailable());
                super.onLocationAvailability(locationAvailability);
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
//        if (checkPermissions()) {
//            // permissions granted.
//            currentLocation();
//            appLocationPermission = true;
//            Log.e("Bool1: ", Boolean.toString(appLocationPermission));
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkPermissions()) {
            // permissions granted.
//            startLocationUpdates();
            appLocationPermission = true;
            Log.e("Bool1: ", Boolean.toString(appLocationPermission));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        fusedClient.removeLocationUpdates(mCallback);
    }

    @SuppressLint("MissingPermission")
    private void currentLocation() {
        fusedClient = LocationServices.getFusedLocationProviderClient(this);
        //Initially, get last known location. We can refine this estimate later
        fusedClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentlatitude = location.getLatitude();
                    currentlongitude = location.getLongitude();
                    Log.e("CurLatLng: ", "Lat: " + currentlatitude + ",  Lng: " + currentlongitude);

                    if (Internet.isNetworkConnected(getApplicationContext())) {
                        currentWeatherViewModel.getCurWeatherDetails(Double.toString(currentlatitude), Double.toString(currentlongitude), appID);
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection Found!", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });


        //now for receiving constant location updates:
        createLocRequest();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(MainActivity.this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this, 500);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(MainActivity.this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case MULTIPLE_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permissions granted.
                    currentLocation();

                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    // no permissions granted.
                    appLocationPermission = false;
                    Log.e("Bool3: ", Boolean.toString(appLocationPermission));
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    protected void createLocRequest() {
        mLocationRequest = new com.google.android.gms.location.LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, UPDATE_INTERVAL).build();
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedClient.requestLocationUpdates(mLocationRequest, mCallback, null);
    }
}