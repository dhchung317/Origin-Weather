package com.hyunki.origin_weather_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.recyclerview.widget.RecyclerView;

import com.hyunki.origin_weather_app.R;
import com.hyunki.origin_weather_app.controller.CityClickListener;
import com.hyunki.origin_weather_app.model.City;

public class CityRecyclerViewAdapter extends RecyclerView.Adapter<CityViewHolder> {

    City[] cities;
    City[] filteredList=new City[0];
    CityClickListener listener;

    public CityRecyclerViewAdapter(City[] cities) {
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
        if(filteredList.length > position) {
            holder.bind(filteredList[position], listener);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return filteredList.length;
    }

    public void setList(City[] cities){
        this.cities = cities;
    }

    public void setFilteredList(City[] cities) {
        this.filteredList = cities;
        notifyDataSetChanged();
    }

    public City[] getCityList(){
        return cities;
    }
}