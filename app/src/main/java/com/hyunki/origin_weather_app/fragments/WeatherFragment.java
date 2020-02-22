package com.hyunki.origin_weather_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hyunki.origin_weather_app.R;
import com.hyunki.origin_weather_app.adapter.CityRecyclerViewAdapter;
import com.hyunki.origin_weather_app.model.City;
import com.hyunki.origin_weather_app.viewmodel.SharedViewModel;

public class WeatherFragment extends Fragment {

    SharedViewModel viewModel;

    private RecyclerView weatherRecyclerView;
    private ImageView weatherIcon;
    private TextView tempTextView;
    private TextView locationTextView;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tempTextView = view.findViewById(R.id.temp_textView);
        locationTextView = view.findViewById(R.id.location_textView);

        CityRecyclerViewAdapter adapter = new CityRecyclerViewAdapter(new City[0]);
        weatherRecyclerView = view.findViewById(R.id.recycler_view);
        weatherRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        weatherRecyclerView.setAdapter(adapter);


    }
}