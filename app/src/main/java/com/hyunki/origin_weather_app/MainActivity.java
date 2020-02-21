package com.hyunki.origin_weather_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;
import com.hyunki.origin_weather_app.model.City;
import com.hyunki.origin_weather_app.model.Forecast;
import com.hyunki.origin_weather_app.viewmodel.MainViewModel;
import com.hyunki.origin_weather_app.viewmodel.State;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "main--";

//    public static final int PERMISSION_ID = 317;

    private MainViewModel viewModel;

    private FusedLocationProviderClient fusedLocationClient;

    private MutableLiveData<String> defaultLocation = new MutableLiveData<>();

//icons - http://openweathermap.org/img/wn/ {10d} @2x.png

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

//        getLastLocation();
//
//        defaultLocation.observe(this, s -> {
//            viewModel.getForecasts(s);
//            Log.d(TAG, "onCreate: location " + s);
//
//        });
//
//        viewModel.getForecastLivedata().observe(this, state -> {
//            renderForecast(state);
//        });

        viewModel.loadJSONString(getApplicationContext(),"citylist.json");

        viewModel.getJSONStringLivedata().observe(this, new Observer<State>() {
            @Override
            public void onChanged(State state) {
                onJSONStringParsed(state);
            }
        });

        viewModel.getCitylivedata().observe(this, new Observer<State>() {
            @Override
            public void onChanged(State state) {
                renderCities(state);
            }
        });

    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions() && isLocationEnabled()) {
            fusedLocationClient.getLastLocation().addOnCompleteListener(
                    task -> {
                        Location location = task.getResult();
                        if (location != null) {
                            defaultLocation.setValue(getLocationString(location));
                        }else{
                            showLocationErrorSnack();
                        }
                    });
        }
    }

    private String getLocationString(Location location) {
        Geocoder gcd = new Geocoder(getBaseContext(),
                Locale.getDefault());
        Address address;
        String locationString = "";
        try {
            address = gcd.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1).get(0);
            String locality = address.getLocality();
            String state = address.getAdminArea();
            locationString = String.format("%s,%s", locality, state);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return locationString;
    }

    private void renderForecast(State state) {

        if (state == State.Loading.INSTANCE) {
            Log.d(TAG, "render: state was loading");

        } else if (state == State.Error.INSTANCE) {
            Log.d(TAG, "render: state error");
            showNetworkErrorSnack();

        } else if (state.getClass() == State.Success.class) {
            Log.d(TAG, "render: state was success");
            State.Success s = (State.Success) state;

            for(Forecast f : (List<Forecast>) s.getAny()){
                Log.d(TAG, "render: successful" + f.getDate());
            }
        }

    }

    private void renderCities(State state) {

        if (state == State.Loading.INSTANCE) {
            Log.d(TAG, "render: state was loading");

        } else if (state == State.Error.INSTANCE) {
            Log.d(TAG, "render: state error");
            showNetworkErrorSnack();

        } else if (state.getClass() == State.Success.class) {
            Log.d(TAG, "render: state was success");
            State.Success s = (State.Success) state;

            for(City c : (City[]) s.getAny()){
                Log.d(TAG, "render: successful" + c.getCity());
            }
        }

    }

    private void onJSONStringParsed(State state) {

        if (state == State.Loading.INSTANCE) {
            Log.d(TAG, "render: state was loading");

        } else if (state == State.Error.INSTANCE) {
            Log.d(TAG, "render: state error");


        } else if (state.getClass() == State.Success.class) {
            Log.d(TAG, "render: state was success");
            State.Success s = (State.Success) state;
            String string = (String) s.getAny();
            Log.d(TAG, "render: state was success: " + string);
            viewModel.loadCities(string);
        }

    }

    public void showSnackBar(View v, String message) {
        // parametrised constructor

        Snackbar.make(v, message,
                Snackbar.LENGTH_SHORT)
                .show();
    }

    public void showNetworkErrorSnack() {
        showSnackBar(findViewById(R.id.coordinatorLayout), getString(R.string.network_error));
    }

    public void showLocationErrorSnack(){
        showSnackBar(findViewById(R.id.coordinatorLayout), getString(R.string.location_error));
    }
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
