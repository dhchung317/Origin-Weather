package com.hyunki.origin_weather_app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.hyunki.origin_weather_app.adapter.WeatherPagerAdapter;
import com.hyunki.origin_weather_app.controller.CityClickListener;
import com.hyunki.origin_weather_app.model.City;
import com.hyunki.origin_weather_app.viewmodel.SharedViewModel;

import static com.hyunki.origin_weather_app.fragments.WeatherFragment.PERMISSION_ID;

public class MainActivity extends AppCompatActivity implements CityClickListener {
    public static final String TAG = "main--";

    private FirebaseAuth auth;

    private SharedViewModel viewModel;

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private WeatherPagerAdapter viewPagerAdapter;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        invalidateOptionsMenu();

        auth = FirebaseAuth.getInstance();

        viewModel = ViewModelProviders.of(this).get(SharedViewModel.class);

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

    @Override
    public void refreshFragmentWithCityInfo(City city) {
        viewModel.loadSingleCityById(String.valueOf(city.getId()));
        viewModel.loadForecastsById(String.valueOf(city.getId()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getTitle().toString()) {

            case "Sign In": {
                Log.d(TAG, "onOptionsItemSelected: item selected");
                showLoginActivity();
            }

            case "Sign Out": {
                revokeAccess();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.login_switch);

        if (auth.getCurrentUser() != null) {
            item.setTitle("Sign Out");
        } else {
            item.setTitle("Sign In");
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void showLoginActivity() {
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);

    }

    private void revokeAccess() {
        // Firebase sign out
        auth.signOut();

        //update ui
    }
}

//        TODO- Displays users location and local weather (in Fahrenheit) upon opening app
//        TODO- Present detailed weather conditions (rain, sleet, sunny, etc.) with strong attention to design
//        TODO- Allows users to search for weather in other cities
//        TODO- Allow users to be able to register and login using Firebase api
//        TODO- Styling is key!
//        Bonus:
//        TODO- Be able to save certain cities you’re interested in (or visit frequently),
//         and have that data present from a navigation perspective.
//        TODO- Present a few future days of generic (less-defined) weather for a city

//TODO- parse the dates in forecast list to show only unique days
//TODO- overall color scheme and styling of app
//TODO- navigation drawer that displays favorites, available to signed in users

//TODO- rebuild the model and api call to get one day in metric

//TODO EXTRA- show the extended forecast throughout each day by time

//Certificate fingerprints:
//	 MD5:  88:56:84:2E:DF:4D:43:67:4F:52:3E:0D:64:70:96:E6
//	 SHA1: 82:47:06:C6:3B:B4:EF:01:A2:90:82:82:CB:1A:21:9A:BC:09:07:4B
//	 SHA256: A8:AA:F9:21:09:B5:43:80:EA:01:3E:B1:FE:0B:E4:76:EC:77:15:AD:92:97:71:E4:FC:BF:0B:ED:71:95:74:DE