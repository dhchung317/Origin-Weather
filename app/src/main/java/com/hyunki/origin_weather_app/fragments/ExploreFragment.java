package com.hyunki.origin_weather_app.fragments;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

import java.util.List;

import io.reactivex.Observable;

public class ExploreFragment extends BaseFragment implements SearchView.OnQueryTextListener {
    public static final String TAG = "explore-fragment";

    private FirebaseAuth auth;

    private FirebaseAuth.AuthStateListener authListener;

    private SharedViewModel viewModel;

    private ProgressBar progressBar;

    private RecyclerView exploreRecyclerView;
    private CityRecyclerViewAdapter cityRecyclerViewAdapter;

    private ImageButton favoriteButton;
    private ImageView weatherIcon;
    private TextView tempTextView;
    private TextView locationTextView;
    private SearchView searchView;

    private String default_id = "";

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        viewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);

        progressBar = getActivity().findViewById(R.id.progress_bar);

        viewModel.loadCities(getActivity().getApplicationContext(), "citylist.json");

        viewModel.getCityLiveData().observe(getViewLifecycleOwner(), state -> renderCities(state));
        viewModel.getSingleCityLiveData().observe(getViewLifecycleOwner(), state -> renderSingleCity(state));
        viewModel.getExploredForecastLiveData().observe(getViewLifecycleOwner(), state -> renderForecast(state));

        authListener = firebaseAuth -> {
            if(firebaseAuth.getCurrentUser() != null){

                initButton();
            }else{
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchView = view.findViewById(R.id.explore_searchview);
        searchView.setOnQueryTextListener(this);

        weatherIcon = view.findViewById(R.id.explore_locationIcon_imageView);
        tempTextView = view.findViewById(R.id.explore_temp_textView);
        locationTextView = view.findViewById(R.id.explore_location_textView);

        cityRecyclerViewAdapter = new CityRecyclerViewAdapter(new City[0]);
        exploreRecyclerView = view.findViewById(R.id.explore_recycler_view);
        exploreRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        exploreRecyclerView.setAdapter(cityRecyclerViewAdapter);
        favoriteButton = view.findViewById(R.id.favorite_button);

    }

    private void initButton(){
        Log.d(TAG, "initButton: reached");
        favoriteButton.setVisibility(View.VISIBLE);
        favoriteButton.setImageResource(R.drawable.ic_favorite_border);
        favoriteButton.setTag(R.drawable.ic_favorite_border);
        favoriteButton.setOnClickListener(view -> {
            toggleFavoriteButton();
        });
    }

    private void hideButton(){
        favoriteButton.setVisibility(View.GONE);
        favoriteButton.setOnClickListener(null);
    }

    private void renderCities(State state) {

        if (state == State.Loading.INSTANCE) {
            showProgressBar(true);
            Log.d(TAG, "render: state was loading");

        } else if (state == State.Error.INSTANCE) {
            showProgressBar(false);
            Log.d(TAG, "render: state error");

        } else if (state.getClass() == State.Success.class) {
            showProgressBar(false);
            Log.d(TAG, "render: state was success");
            State.Success s = (State.Success) state;

            City[] cities = (City[]) ((State.Success) state).getAny();

            cityRecyclerViewAdapter.setList(cities);
            cityRecyclerViewAdapter.setFilteredList(cities);

            Log.d(TAG, "render: successful" + ((City[]) s.getAny()).length);
        }

    }

    private void renderForecast(State state) {

        if (state == State.Loading.INSTANCE) {
            showProgressBar(true);
            Log.d(TAG, "render: state was loading");

        } else if (state == State.Error.INSTANCE) {
            showProgressBar(false);
            Log.d(TAG, "render: state error");

        } else if (state.getClass() == State.Success.class) {
            showProgressBar(false);
            Log.d(TAG, "render: state was success");
            State.Success s = (State.Success) state;

            List<Forecast> forecasts = (List<Forecast>) s.getAny();
            Forecast forecast = forecasts.get(0);

            Log.d(TAG, "renderForecast: " + forecast.getTemp().getTemp());
            Log.d(TAG, "renderForecast: " + TempUtil.getFahrenheitFromKelvin(forecast.getTemp().getTemp()));

            int temp = TempUtil.getFahrenheitFromKelvin(forecast.getTemp().getTemp());
            tempTextView.setText((temp) + getString(R.string.degree_fahrenheit));

            Log.d(TAG, "renderForecast: " + forecast.getDate());
            String icon = forecasts.get(0).getWeather().get(0).getIcon();

            String iconUri = String.format("https://openweathermap.org/img/wn/%s@2x.png", icon);
            Picasso.get().load(iconUri).into(weatherIcon);

            Log.d(TAG, "render: successful" + ((List<Forecast>) s.getAny()).size());
        }
    }

    private void renderSingleCity(State state) {
        if (state == State.Loading.INSTANCE) {
            showProgressBar(true);
            Log.d(TAG, "render: state was loading");

        } else if (state == State.Error.INSTANCE) {
            showProgressBar(false);
            Log.d(TAG, "render: state error");

        } else if (state.getClass() == State.Success.class) {
            showProgressBar(false);
            Log.d(TAG, "render: state was success");
            State.Success s = (State.Success) state;

            City city = (City) s.getAny();

            default_id = city.getName();

            locationTextView.setText(city.getName());
            if(searchView.hasFocus()) {
                searchView.clearFocus();
            }
        }
    }

    private void showProgressBar(boolean isVisible) {

        if (isVisible) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void toggleFavoriteButton(){

        if((int)favoriteButton.getTag() == R.drawable.ic_favorite_border){
            favoriteButton.setTag(R.drawable.ic_favorite);
            favoriteButton.setImageResource(R.drawable.ic_favorite);
        }else{
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

        Observable.fromArray(cityRecyclerViewAdapter.getCityList())
                .filter(city -> city.getName().toLowerCase().startsWith(s.toLowerCase()))
//                .filter(state -> (s.length() > 1) && state.name.toLowerCase().contains(s.toLowerCase()))
                .toList().subscribe(cities -> {
            City[] cityList = cities.toArray(new City[cities.size()]);
            Log.d(TAG, "onQueryTextChange: " + cities.size());
            cityRecyclerViewAdapter.setFilteredList(cityList);
        }).dispose();
        return false;
    }

    @Override
    void refresh() {
        if(!default_id.isEmpty()){
            viewModel.loadSingleCityById(String.valueOf(default_id));
            viewModel.loadForecastsById(String.valueOf(default_id));
        }
    }
}