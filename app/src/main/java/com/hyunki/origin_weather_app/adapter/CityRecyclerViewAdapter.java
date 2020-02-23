package com.hyunki.origin_weather_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.hyunki.origin_weather_app.R;
import com.hyunki.origin_weather_app.model.City;

public class CityRecyclerViewAdapter extends RecyclerView.Adapter<CityViewHolder> {

    City[] cities;

    public CityRecyclerViewAdapter(City[] cities) {
        this.cities = cities;
    }

    @Override
    public CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_explore, parent, false);
        return new CityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CityViewHolder holder, int position) {
        holder.bind(cities[position]);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return cities.length;
    }

    public void setList(City[] cities){
        this.cities = cities;
    }
}