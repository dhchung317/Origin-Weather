package com.hyunki.origin_weather_app.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.hyunki.origin_weather_app.R;
import com.hyunki.origin_weather_app.adapter.CityRecyclerViewAdapter;
import com.hyunki.origin_weather_app.controller.UpdateFavoritesListener;
import com.hyunki.origin_weather_app.model.City;
import com.hyunki.origin_weather_app.model.Forecast;
import com.hyunki.origin_weather_app.model.util.TempUtil;
import com.hyunki.origin_weather_app.viewmodel.SharedViewModel;
import com.hyunki.origin_weather_app.viewmodel.State;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import io.reactivex.Observable;

public class ExploreFragment extends BaseFragment implements SearchView.OnQueryTextListener {
    private FirebaseUtil firebaseUtil;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private UpdateFavoritesListener updateFavoritesListener;
    private ArrayList<City> favoriteCities = new ArrayList<>();

    private Set<Integer> cityIdSet = new HashSet<>();

    private SharedViewModel viewModel;

    private CityRecyclerViewAdapter cityRecyclerViewAdapter;

    private ImageButton favoriteButton;
    private ImageView weatherIcon;
    private TextView tempTextView;
    private TextView locationTextView;
    private TextView conditionTextView;
    private TextView conditionDetailTextView;
    private SearchView searchView;

//    private String default_city_name = "";
//    private City default_city = new City();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(SharedViewModel.class);
        firebaseUtil = new FirebaseUtil();
        auth = FirebaseAuth.getInstance();

        viewModel.loadCities(getActivity(), "citylist.json");
        viewModel.getCityLiveData().observe(getViewLifecycleOwner(), this::renderCities);
        viewModel.getSingleCityLiveData().observe(getViewLifecycleOwner(), this::renderSingleCity);
        viewModel.getExploredForecastLiveData().observe(getViewLifecycleOwner(), this::renderForecast);

        updateFavoritesListener = (UpdateFavoritesListener) getActivity();
        updateFavoritesListener.updateFavorites();

        authListener = firebaseAuth -> {
            Log.d("explorefragment", "onActivityCreated: listener hit");
            if (firebaseAuth.getCurrentUser() != null) {
                initButton();
                refresh();

                favoriteCities = firebaseUtil.getFavorites();
                Log.d("explorefragment", "onActivityCreated: " + favoriteCities.size());


                for (int i = 0; i < favoriteCities.size(); i++) {
                    cityIdSet.add(favoriteCities.get(i).getId());
                    Log.d("explorefragment", "onActivityCreated: " + favoriteCities.get(i).getId());
                }
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
        firebaseUtil = new FirebaseUtil();
//        auth = FirebaseAuth.getInstance();
//        firebaseUtil.loadFavorites();

        searchView = view.findViewById(R.id.explore_searchview);
        searchView.setOnQueryTextListener(this);

        weatherIcon = view.findViewById(R.id.explore_locationIcon_imageView);
        tempTextView = view.findViewById(R.id.explore_temp_textView);
        locationTextView = view.findViewById(R.id.explore_location_textView);
        conditionTextView = view.findViewById(R.id.explore_condition_textView);
        conditionDetailTextView = view.findViewById(R.id.explore_condition_detail_textView);

        cityRecyclerViewAdapter = new CityRecyclerViewAdapter(new ArrayList<>());
        RecyclerView exploreRecyclerView = view.findViewById(R.id.explore_recycler_view);
        exploreRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        exploreRecyclerView.setAdapter(cityRecyclerViewAdapter);
        favoriteButton = view.findViewById(R.id.favorite_button);

//        if(firebaseUtil.auth.getCurrentUser() != null){
//        }

    }

    private void initButton() {
        favoriteButton.setImageResource(R.drawable.ic_favorite_border);
        favoriteButton.setTag(R.drawable.ic_favorite_border);
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

            conditionTextView.setText(forecast.getWeather().get(0).getMain());
            conditionDetailTextView.setText(forecast.getWeather().get(0).getDescription());

            int temp = TempUtil.getFahrenheitFromKelvin(forecast.getTemp().getTempKelvin());
            tempTextView.setText(String.format(Locale.US, "%d%s", temp, getString(R.string.degree_fahrenheit)));

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
//            default_city_name = city.getName();
//            default_city = city;
            viewModel.setCurrentExploredCity(city);

            if(firebaseUtil.auth != null) {
                if (firebaseUtil.isFavorite(city)) {
                    favoriteButton.setTag(R.drawable.ic_favorite);
                    favoriteButton.setImageResource(R.drawable.ic_favorite);
                }else{
                    favoriteButton.setTag(R.drawable.ic_favorite_border);
                    favoriteButton.setImageResource(R.drawable.ic_favorite_border);
                }
            }

            favoriteButton.setOnClickListener(view -> {
                toggleFavoriteButton();
            });

            locationTextView.setText(city.getName());
            if (searchView.hasFocus()) {
                searchView.clearFocus();
            }
        }
    }

    private void toggleFavoriteButton() {
        //TODO- change logic to work with firebase
        // -when a city is loaded, check to see if it is in favorites,
        // -if not, the heart button will be an empty button, if it is, it will display filled one.
        // -then when you click to favorite something, you need to check if it is in the dataset or not.
        // -if it is, remove from dataset and refresh view. if not add to dataset and refresh view.


        if ((int) favoriteButton.getTag() == R.drawable.ic_favorite_border) {
            favoriteButton.setTag(R.drawable.ic_favorite);
            favoriteButton.setImageResource(R.drawable.ic_favorite);
            addCurrentExploredCityToFavorites(viewModel.getCurrentExploredCity());
        } else if((int) favoriteButton.getTag() == R.drawable.ic_favorite) {
            favoriteButton.setTag(R.drawable.ic_favorite_border);
            favoriteButton.setImageResource(R.drawable.ic_favorite_border);
            removeCurrentExploredCityFromFavorites(viewModel.getCurrentExploredCity());
        }
        updateFavoritesListener.updateFavorites();
    }

    private void addCurrentExploredCityToFavorites(City city){
        firebaseUtil.addFavorite(city);
    }

    private void removeCurrentExploredCityFromFavorites(City city){
        firebaseUtil.removeFavorite(city);
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
                    cityRecyclerViewAdapter.setFilteredList(new ArrayList<>(cities));
                },

                throwable -> {
                })
                .dispose();
        return false;
    }

    @Override
    void refresh() {

        if (!viewModel.getCurrentExploredCity().getName().isEmpty()) {
            viewModel.loadSingleCityById(String.valueOf(viewModel.getCurrentExploredCity().getId()));
            viewModel.loadForecastsById(String.valueOf(viewModel.getCurrentExploredCity().getId()));
        }
    }
}