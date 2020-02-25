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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class WeatherFragment extends BaseFragment {
    public static final int PERMISSION_ID = 317;

    private SharedViewModel viewModel;

    private ImageView weatherIcon;
    private TextView tempTextView;
    private TextView locationTextView;
    private ForecastRecyclerViewAdapter forecastRecyclerViewAdapter;

    private String myLocation;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(SharedViewModel.class);

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

        viewModel.getDefaultLocation().observe(getViewLifecycleOwner(), this::onLocationLoaded);
        viewModel.getForecastLiveData().observe(getViewLifecycleOwner(), this::renderForecast);
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
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        weatherIcon = view.findViewById(R.id.my_weather_locationIcon_imageView);
        tempTextView = view.findViewById(R.id.my_weather_temp_textView);
        locationTextView = view.findViewById(R.id.my_weather_location_textView);

        forecastRecyclerViewAdapter = new ForecastRecyclerViewAdapter(new ArrayList<>());
        RecyclerView weatherRecyclerView = view.findViewById(R.id.my_weather_recycler_view);
        weatherRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        weatherRecyclerView.setAdapter(forecastRecyclerViewAdapter);
    }

    private void renderForecast(State state) {

        if (state == State.Loading.INSTANCE) {
            showProgressBar(true);

        } else if (state == State.Error.INSTANCE) {
            showProgressBar(false);
            showNetworkErrorSnack();

        } else if (state.getClass() == State.Success.OnForecastsLoaded.class) {
            showProgressBar(false);
            State.Success.OnForecastsLoaded s = (State.Success.OnForecastsLoaded) state;

            List<Forecast> forecasts = s.getForecasts();
            Forecast forecast = forecasts.remove(0);
            forecastRecyclerViewAdapter.setList(s.getForecasts());


            int temp = TempUtil.getFahrenheitFromKelvin(forecast.getTemp().getTempKelvin());
            tempTextView.setText(String.format(
                    Locale.US, "%d%s", temp,
                    Objects.requireNonNull(getContext()).getString(R.string.degree_fahrenheit)));

            locationTextView.setText(String.format("%s %s",
                    Objects.requireNonNull(getActivity())
                            .getString(R.string.today_in), myLocation));

            String icon = forecasts.get(0).getWeather().get(0).getIcon();
            String iconUri = String.format("https://openweathermap.org/img/wn/%s@2x.png", icon);
            Picasso.get().load(iconUri).into(weatherIcon);
        }
    }

    private void onLocationLoaded(State state) {
        if (state == State.Loading.INSTANCE) {
            showProgressBar(true);

        } else if (state == State.Error.INSTANCE) {
            showProgressBar(false);
            showNetworkErrorSnack();

        } else if (state.getClass() == State.Success.OnDefaultLocationLoaded.class) {
            showProgressBar(false);
            State.Success.OnDefaultLocationLoaded s = (State.Success.OnDefaultLocationLoaded) state;
            myLocation = s.getCityString();
            viewModel.loadForecasts(s.getCityString());
        }
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) Objects.requireNonNull(getContext()).getSystemService(Context.LOCATION_SERVICE);
        return Objects.requireNonNull(locationManager).isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                Objects.requireNonNull(getActivity(), getString(R.string.require_non_null_activity)),
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            viewModel.loadLastLocation();
        }
    }
}