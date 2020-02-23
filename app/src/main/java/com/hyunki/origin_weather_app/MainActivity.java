package com.hyunki.origin_weather_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.hyunki.origin_weather_app.adapter.WeatherPagerAdapter;
import com.hyunki.origin_weather_app.viewmodel.SharedViewModel;

import static com.hyunki.origin_weather_app.fragments.WeatherFragment.PERMISSION_ID;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "main--";

    private SharedViewModel viewModel;

    private FusedLocationProviderClient fusedLocationClient;

    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private WeatherPagerAdapter viewPagerAdapter;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progress_bar);
        viewModel = ViewModelProviders.of(this).get(SharedViewModel.class);
//        swipeRefreshLayout = findViewById(R.id.swiperefresh);
//        swipeRefreshLayout.setOnRefreshListener(() -> requestNewLocationData());
        viewPager = findViewById(R.id.viewpager);
        viewPagerAdapter = new WeatherPagerAdapter(getSupportFragmentManager(), this.getLifecycle());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout = findViewById(R.id.tabs);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText(R.string.tab_weather_label);
                    } else if (position == 1) {
                        tab.setText(R.string.tab_explore_label);
                    }
                }).attach();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: granted");
                viewModel.loadLastLocation();
            }
        }
    }

//    public void showSnackBar(View v, String message) {
//        Snackbar.make(v, message,
//                Snackbar.LENGTH_SHORT)
//                .show();
//    }
//
//
//    private void showProgressBar(boolean isVisible) {
//        if (isVisible) {
//            progressBar.setVisibility(View.VISIBLE);
//        } else {
//            progressBar.setVisibility(View.GONE);
//        }
//    }


//    @SuppressLint("MissingPermission")
//    private void getLastLocation() {
//        if (checkPermissions()) {
//            if (isLocationEnabled()) {
//                fusedLocationClient.getLastLocation().addOnCompleteListener(
//                        task -> {
//                            Location location = task.getResult();
//                            if (location != null) {
//                                Log.d(TAG, "getLastLocation: location succesful");
//                                viewModel.getDefaultLocation().setValue(getLocationString(location));
//                            } else {
//                                showLocationErrorSnack();
//                            }
//                        });
//            }else{
//                showLocationErrorSnack();
//                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivity(intent);
//            }
//        } else {
//            requestPermissions();
//        }
//    }
    //refactor to call a method from viewmodel

//    private String getLocationString(Location location) {
//        Geocoder gcd = new Geocoder(getBaseContext(),
//                Locale.getDefault());
//        Address address;
//        String locationString = "";
//        try {
//            address = gcd.getFromLocation(location.getLatitude(),location.getLongitude(),1).get(0);
//            String locality = address.getSubLocality();
//            String state = address.getAdminArea();
//            locationString = String.format("%s,%s", locality, state);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return locationString;
//    }

//    @SuppressLint("MissingPermission")
//    private void requestNewLocationData() {
//
//        LocationRequest locationRequest = new LocationRequest()
//                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//                .setInterval(0)
//                .setFastestInterval(0)
//                .setNumUpdates(1);
//
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        fusedLocationClient.requestLocationUpdates(
//                locationRequest, locationCallback,
//                Looper.myLooper()
//        );
//
//    }


//    private LocationCallback locationCallback = new LocationCallback() {
//        @Override
//        public void onLocationResult(LocationResult locationResult) {
//            if(swipeRefreshLayout.isRefreshing()) {
//                swipeRefreshLayout.setRefreshing(false);
//            }
//            defaultLocation.setValue(
//                    getLocationString(locationResult.getLastLocation()));
//        }
//    };

//    private void requestPermissions() {
//        ActivityCompat.requestPermissions(
//                this,
//                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
//                PERMISSION_ID
//        );
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == PERMISSION_ID) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//            }
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (checkPermissions()) {
//            getLastLocation();
//        }
//    }
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
