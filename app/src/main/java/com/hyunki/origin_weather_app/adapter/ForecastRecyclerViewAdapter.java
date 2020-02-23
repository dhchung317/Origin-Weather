package com.hyunki.origin_weather_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.hyunki.origin_weather_app.R;
import com.hyunki.origin_weather_app.model.Forecast;

import java.util.List;

public class ForecastRecyclerViewAdapter extends RecyclerView.Adapter<ForecastViewHolder> {

    List<Forecast> forecasts;

    public ForecastRecyclerViewAdapter(List<Forecast> forecasts) {
        this.forecasts = forecasts;
    }

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

    public void setList(List<Forecast> forecasts){
        this.forecasts = forecasts;
        notifyDataSetChanged();
    }
}