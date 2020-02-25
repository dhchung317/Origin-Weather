package com.hyunki.origin_weather_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.hyunki.origin_weather_app.R;
import com.hyunki.origin_weather_app.model.Forecast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ForecastRecyclerViewAdapter extends RecyclerView.Adapter<ForecastViewHolder> {

    private ArrayList<Forecast> forecasts;

    public ForecastRecyclerViewAdapter(ArrayList<Forecast> forecasts) {
        this.forecasts = forecasts;
    }

    @NotNull
    @Override
    public ForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_weather, parent, false);
        return new ForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ForecastViewHolder holder, int position) {
        holder.bind(forecasts.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return forecasts.size();
    }

    public void setList(ArrayList<Forecast> forecasts) {
        this.forecasts = forecasts;
        notifyDataSetChanged();
    }
}