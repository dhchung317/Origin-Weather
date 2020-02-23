package com.hyunki.origin_weather_app.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hyunki.origin_weather_app.R;
import com.hyunki.origin_weather_app.adapter.CityRecyclerViewAdapter;
import com.hyunki.origin_weather_app.adapter.ForecastRecyclerViewAdapter;
import com.hyunki.origin_weather_app.model.City;
import com.hyunki.origin_weather_app.viewmodel.SharedViewModel;
import com.hyunki.origin_weather_app.viewmodel.State;

import java.util.ArrayList;
import java.util.List;

public class ExploreFragment extends Fragment {
    public static final String TAG = "explore-fragment";

    private SharedViewModel viewModel;
    private ProgressBar progressBar;
    private RecyclerView exploreRecyclerView;
    private CityRecyclerViewAdapter cityRecyclerViewAdapter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        progressBar = getActivity().findViewById(R.id.progress_bar);
        viewModel.loadCities(getActivity().getApplicationContext(),"citylist.json");
        viewModel.getCitylivedata().observe(getViewLifecycleOwner(), state -> renderCities(state));

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

        cityRecyclerViewAdapter = new CityRecyclerViewAdapter(new City[0]);


        exploreRecyclerView = view.findViewById(R.id.explore_recycler_view);
        exploreRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        exploreRecyclerView.setAdapter(cityRecyclerViewAdapter);
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
//            for(City c : (List<City>) s.getAny()){
//                Log.d(TAG, "render: successful" + c.getName());
//
//            }
            Log.d(TAG, "render: successful" + ((City[]) s.getAny()).length);
        }

    }

    private void showProgressBar(boolean isVisible){

        if(isVisible){
            progressBar.setVisibility(View.VISIBLE);
        }else{
            progressBar.setVisibility(View.GONE);
        }

    }
}