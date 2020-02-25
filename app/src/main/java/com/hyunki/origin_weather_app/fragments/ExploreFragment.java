package com.hyunki.origin_weather_app.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.hyunki.origin_weather_app.R;
import com.hyunki.origin_weather_app.adapter.CityRecyclerViewAdapter;
import com.hyunki.origin_weather_app.model.City;
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

import io.reactivex.Observable;

public class ExploreFragment extends BaseFragment implements SearchView.OnQueryTextListener {
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    private SharedViewModel viewModel;

    private CityRecyclerViewAdapter cityRecyclerViewAdapter;

    private ImageButton favoriteButton;
    private ImageView weatherIcon;
    private TextView tempTextView;
    private TextView locationTextView;
    private SearchView searchView;

    private String default_city_name = "";
    private City default_city = new City();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        auth = FirebaseAuth.getInstance();

        viewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(SharedViewModel.class);

        viewModel.loadCities(getActivity().getApplicationContext(), "citylist.json");
        viewModel.getCityLiveData().observe(getViewLifecycleOwner(), this::renderCities);
        viewModel.getSingleCityLiveData().observe(getViewLifecycleOwner(), this::renderSingleCity);
        viewModel.getExploredForecastLiveData().observe(getViewLifecycleOwner(), this::renderForecast);

        authListener = firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() != null) {
                initButton();
                refresh();
            } else {
                hideButton();
            }
        };
        auth.addAuthStateListener(authListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(
                R.layout.fragment_explore, container, false);
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchView = view.findViewById(R.id.explore_searchview);
        searchView.setOnQueryTextListener(this);

        weatherIcon = view.findViewById(R.id.explore_locationIcon_imageView);
        tempTextView = view.findViewById(R.id.explore_temp_textView);
        locationTextView = view.findViewById(R.id.explore_location_textView);

        cityRecyclerViewAdapter = new CityRecyclerViewAdapter(new ArrayList<>());
        RecyclerView exploreRecyclerView = view.findViewById(R.id.explore_recycler_view);
        exploreRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        exploreRecyclerView.setAdapter(cityRecyclerViewAdapter);
        favoriteButton = view.findViewById(R.id.favorite_button);
    }

    private void initButton() {
        favoriteButton.setImageResource(R.drawable.ic_favorite_border);
        favoriteButton.setTag(R.drawable.ic_favorite_border);
        favoriteButton.setOnClickListener(view -> {
            toggleFavoriteButton();
        });
    }

    private void hideButton() {
        favoriteButton.setVisibility(View.GONE);
        favoriteButton.setOnClickListener(null);
    }

    private void renderCities(State state) {

        if (state == State.Loading.INSTANCE) {
            showProgressBar(true);

        } else if (state == State.Error.INSTANCE) {
            showProgressBar(false);
            showNetworkErrorSnack();

        } else if (state.getClass() == State.Success.OnCitiesLoaded.class) {
            showProgressBar(false);
            State.Success.OnCitiesLoaded s = (State.Success.OnCitiesLoaded) state;
            ArrayList<City> cities = s.getCities();
            cityRecyclerViewAdapter.setList(cities);
            cityRecyclerViewAdapter.setFilteredList(cities);
        }

    }

    private void renderForecast(State state) {

        if (state == State.Loading.INSTANCE) {
            showProgressBar(true);

        } else if (state == State.Error.INSTANCE) {
            showProgressBar(false);
            showNetworkErrorSnack();

        } else if (state.getClass() == State.Success.OnForecastsByIdLoaded.class) {
            showProgressBar(false);
            State.Success.OnForecastsByIdLoaded s = (State.Success.OnForecastsByIdLoaded) state;

            List<Forecast> forecasts = s.getForecasts();
            Forecast forecast = forecasts.get(0);

            int temp = TempUtil.getFahrenheitFromKelvin(forecast.getTemp().getTempKelvin());
            tempTextView.setText(String.format(Locale.US,"%d%s", temp, getString(R.string.degree_fahrenheit)));

            String icon = forecasts.get(0).getWeather().get(0).getIcon();
            String iconUri = String.format("https://openweathermap.org/img/wn/%s@2x.png", icon);
            Picasso.get().load(iconUri).into(weatherIcon);

            if (auth.getCurrentUser() != null) {
                favoriteButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private void renderSingleCity(State state) {
        if (state == State.Loading.INSTANCE) {
            showProgressBar(true);

        } else if (state == State.Error.INSTANCE) {
            showProgressBar(false);
            showNetworkErrorSnack();

        } else if (state.getClass() == State.Success.OnCityByIdLoaded.class) {
            showProgressBar(false);
            State.Success.OnCityByIdLoaded s = (State.Success.OnCityByIdLoaded) state;

            City city = s.getCity();
            default_city_name = city.getName();
            default_city = city;

            locationTextView.setText(city.getName());
            if (searchView.hasFocus()) {
                searchView.clearFocus();
            }
        }
    }

    private void toggleFavoriteButton() {
        //TODO- change logic to work with a database
        // two methods? when a city is loaded, check to see if it is in favorites,
        // if not it will be an empty button, if it is it will display filled one.
        // then when you click the button, you need to check if it is in the set or not.
        // if it is, remove from set and refresh view. if not add and refresh view.

        if ((int) favoriteButton.getTag() == R.drawable.ic_favorite_border) {
            favoriteButton.setTag(R.drawable.ic_favorite);
            favoriteButton.setImageResource(R.drawable.ic_favorite);
        } else {
            favoriteButton.setTag(R.drawable.ic_favorite_border);
            favoriteButton.setImageResource(R.drawable.ic_favorite_border);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        searchView.clearFocus();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        cityRecyclerViewAdapter.setFilteredList(cityRecyclerViewAdapter.getCityList());
        Observable.fromIterable(cityRecyclerViewAdapter.getCityList())
                .filter(city -> city.getName().toLowerCase().startsWith(s.toLowerCase()))
                .toList().subscribe(
                cities -> {
                    cityRecyclerViewAdapter.setList(new ArrayList<>(cities));
                },

                throwable -> {
                })
                .dispose();
        return false;
    }

    @Override
    void refresh() {
        if (!default_city_name.isEmpty()) {
            viewModel.loadSingleCityById(String.valueOf(default_city.getId()));
            viewModel.loadForecastsById(String.valueOf(default_city.getId()));
        }
    }
}