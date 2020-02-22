package com.hyunki.origin_weather_app.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hyunki.origin_weather_app.R;
import com.hyunki.origin_weather_app.model.City;

public class CityViewHolder extends RecyclerView.ViewHolder {
    private TextView textView;

    public CityViewHolder(View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.list_item);
    }

    public void bind(City city) {
        textView.setText(city.getName());
    }

}
