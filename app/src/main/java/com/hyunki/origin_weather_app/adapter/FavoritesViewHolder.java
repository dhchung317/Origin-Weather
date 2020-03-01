package com.hyunki.origin_weather_app.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hyunki.origin_weather_app.R;
import com.hyunki.origin_weather_app.controller.FavoritesClickListener;
import com.hyunki.origin_weather_app.model.City;

public class FavoritesViewHolder extends RecyclerView.ViewHolder {
    private TextView textView;

    public FavoritesViewHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.favorite_list_item_textView);
    }

    void bind(City city, FavoritesClickListener listener) {
        textView.setText(city.getName());
        textView.setOnClickListener(view -> listener.showForecastForSelectedFavorite(city));
    }
}
