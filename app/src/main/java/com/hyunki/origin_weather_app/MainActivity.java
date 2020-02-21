package com.hyunki.origin_weather_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.hyunki.origin_weather_app.model.Forecast;
import com.hyunki.origin_weather_app.model.WeatherResponse;
import com.hyunki.origin_weather_app.network.RetrofitFactory;
import com.hyunki.origin_weather_app.network.WeatherService;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "main--";

    public static final String API_KEY = "872ec5abb676430b71b8708ce8e2e1ba";

    public static final int PERMISSION_ID = 317;

    FusedLocationProviderClient fusedLocationClient;

//icons - http://openweathermap.org/img/wn/ {10d} @2x.png




    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        RetrofitFactory.getInstance()
                .create(WeatherService.class)
                .getResponse("london",API_KEY)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d(TAG, "accept: " + throwable);
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<WeatherResponse>() {
                    @Override
                    public void accept(WeatherResponse weatherResponse) throws Exception {
                        Log.d(TAG, "accept: " + weatherResponse.getList().size());
                        Log.d(TAG, "accept: " + weatherResponse.getList().get(0).getWeather().get(0).getDescription());
                        Log.d(TAG, "accept: " + weatherResponse.getList().get(0).getWeather().get(0).getIcon());
                        Log.d(TAG, "accept: " + weatherResponse.getList().get(0).getWeather().get(0).getMain());
                        Log.d(TAG, "accept: " + weatherResponse.getList().get(0).getTemp().getTemp());
                        Log.d(TAG, "accept: " + weatherResponse.getList().get(0).getDate());
                        Log.d(TAG, "accept: " + weatherResponse.getList().get(1).getDate());
                        Log.d(TAG, "accept: " + weatherResponse.getList().get(2).getDate());

                    }
                });

//        getLastLocation();
        requestNewLocationData();

    }


    private boolean checkPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    private void requestPermissions(){
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // Granted. Start getting the location information
            }
        }
    }


    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                fusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {

                                    Geocoder gcd = new Geocoder(getBaseContext(),Locale.getDefault());
                                    try {
                                        Address firstAddy = gcd.getFromLocation(location.getLatitude(),
                                                location.getLongitude(), 1).get(0);

                                        String locality = firstAddy.getLocality();
                                        String state = firstAddy.getAdminArea();

                                        Log.d(TAG, "onComplete: " + locality + state);

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest
                .PRIORITY_HIGH_ACCURACY)
                .setInterval(0)
                .setFastestInterval(0)
                .setNumUpdates(1);



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.requestLocationUpdates(
                locationRequest, locationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location location = locationResult.getLastLocation();

            Geocoder gcd = new Geocoder(getBaseContext(),Locale.getDefault());


            try {

                Address firstAddy = gcd.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 1).get(0);
                String locality = firstAddy.getLocality();
                String state = firstAddy.getAdminArea();
                Log.d(TAG, "onComplete: " + locality + state);
            } catch (IOException e) {
                e.printStackTrace();
            }



        }
    };
}

//        - Displays users location and local weather (in Fahrenheit) upon opening app
//        - Present detailed weather conditions (rain, sleet, sunny, etc.) with strong attention to design
//        - Allows users to search for weather in other cities
//        - Allow users to be able to register and login using Firebase api
//        - Styling is key!
//        Bonus:
//        - Be able to save certain cities you’re interested in (or visit frequently),
//        and have that data present from a navigation perspective.
//        - Present a few future days of generic (less-defined) weather for a city
