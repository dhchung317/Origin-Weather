package com.hyunki.origin_weather_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.hyunki.origin_weather_app.R;
import com.hyunki.origin_weather_app.controller.CityClickListener;
import com.hyunki.origin_weather_app.model.City;

import java.util.ArrayList;

public class CityRecyclerViewAdapter extends RecyclerView.Adapter<CityViewHolder> {

    private ArrayList<City> cities;
    private ArrayList<City> filteredList = new ArrayList<>();
    private CityClickListener listener;

    public CityRecyclerViewAdapter(ArrayList<City> cities) {
        this.cities = cities;
    }

    @Override
    public CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_explore, parent, false);
        Context context = parent.getContext();
        if (context instanceof CityClickListener) {
            listener = (CityClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + "must implement CityClickListener");
        }
        return new CityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CityViewHolder holder, int position) {
        if (filteredList.size() > position) {
            holder.bind(filteredList.get(position), listener);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void setList(ArrayList<City> cities) {
        this.cities = cities;
    }

    public void setFilteredList(ArrayList<City> cities) {
        this.filteredList = cities;
        notifyDataSetChanged();
    }

    public ArrayList<City> getCityList() {
        return cities;
    }
}