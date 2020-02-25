package com.hyunki.origin_weather_app.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hyunki.origin_weather_app.R;
import com.hyunki.origin_weather_app.controller.CityClickListener;
import com.hyunki.origin_weather_app.model.City;

class CityViewHolder extends RecyclerView.ViewHolder {
    private TextView textView;

    CityViewHolder(View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.explore_list_item_textView);
    }

    void bind(City city, CityClickListener listener) {
        textView.setText(city.getName());
        textView.setOnClickListener(view -> listener.updateFragmentWithCityInfo(city));

    }

}
