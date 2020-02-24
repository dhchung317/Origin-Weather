package com.hyunki.origin_weather_app.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.hyunki.origin_weather_app.R;
import com.hyunki.origin_weather_app.adapter.ForecastRecyclerViewAdapter;
import com.hyunki.origin_weather_app.model.Forecast;
import com.hyunki.origin_weather_app.model.util.TempUtil;
import com.hyunki.origin_weather_app.viewmodel.SharedViewModel;
import com.hyunki.origin_weather_app.viewmodel.State;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class WeatherFragment extends BaseFragment {
    public static final String TAG = "weather-fragment";
    public static final int PERMISSION_ID = 317;

    private SharedViewModel viewModel;

    private ProgressBar progressBar;
    private RecyclerView weatherRecyclerView;
    private ImageView weatherIcon;
    private TextView tempTextView;
    private TextView locationTextView;
    private ForecastRecyclerViewAdapter forecastRecyclerViewAdapter;
    private CoordinatorLayout coordinatorLayout;

    private String myLocation;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        progressBar = getActivity().findViewById(R.id.progress_bar);
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);

        viewModel.getDefaultLocation().observe(getViewLifecycleOwner(), s -> {
            viewModel.loadForecasts(s);
            myLocation = s;
            Log.d(TAG, "onCreate: location " + s);
        });

        if (checkPermissions()) {
            if (isLocationEnabled()) {
                viewModel.loadLastLocation();
            } else {
                showLocationErrorSnack();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
        viewModel.getForecastLivedata().observe(getViewLifecycleOwner(), state -> renderForecast(state));
    }

    @Override
    void refresh() {
        viewModel.loadLastLocation();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weather, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = getActivity().findViewById(R.id.progress_bar);

        weatherIcon = view.findViewById(R.id.my_weather_locationIcon_imageView);
        tempTextView = view.findViewById(R.id.my_weather_temp_textView);
        locationTextView = view.findViewById(R.id.my_weather_location_textView);

        forecastRecyclerViewAdapter = new ForecastRecyclerViewAdapter(new ArrayList<>());
        weatherRecyclerView = view.findViewById(R.id.my_weather_recycler_view);
        weatherRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        weatherRecyclerView.setAdapter(forecastRecyclerViewAdapter);
    }

    private void renderForecast(State state) {

        if (state == State.Loading.INSTANCE) {
            showProgressBar(true);
            Log.d(TAG, "render: state was loading");

        } else if (state == State.Error.INSTANCE) {
            showProgressBar(false);
            Log.d(TAG, "render: state error");
            showNetworkErrorSnack();

        } else if (state.getClass() == State.Success.class) {
            showProgressBar(false);
            Log.d(TAG, "render: state was success");
            State.Success s = (State.Success) state;

            forecastRecyclerViewAdapter.setList((List<Forecast>) s.getAny());
            List<Forecast> forecasts = (List<Forecast>) s.getAny();
            Forecast forecast = forecasts.get(0);

            Log.d(TAG, "renderForecast: " + forecast.getTemp().getTemp());
            Log.d(TAG, "renderForecast: " + TempUtil.getFahrenheitFromKelvin(forecast.getTemp().getTemp()));

            int temp = TempUtil.getFahrenheitFromKelvin(forecast.getTemp().getTemp());
            tempTextView.setText((temp) + getString(R.string.degree_fahrenheit));

            locationTextView.setText(getActivity().getString(R.string.today_in) + " " + myLocation);
            String icon = forecasts.get(0).getWeather().get(0).getIcon();

            String iconUri = String.format("https://openweathermap.org/img/wn/%s@2x.png", icon);
            Picasso.get().load(iconUri).into(weatherIcon);
        }
    }

    private void showProgressBar(boolean isVisible) {
        if (isVisible) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    public void showSnackBar(View v, String message) {
        Snackbar.make(v, message,
                Snackbar.LENGTH_SHORT)
                .show();
    }

    public void showNetworkErrorSnack() {
        showSnackBar(coordinatorLayout, getString(R.string.network_error));
    }

    public void showLocationErrorSnack() {
        showSnackBar(getActivity().findViewById(R.id.coordinatorLayout), getString(R.string.location_error));
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                getActivity(),
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
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
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            viewModel.loadLastLocation();
        }
    }
}