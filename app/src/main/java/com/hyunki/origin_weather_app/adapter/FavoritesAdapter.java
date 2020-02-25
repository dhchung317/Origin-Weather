package com.hyunki.origin_weather_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hyunki.origin_weather_app.R;
import com.hyunki.origin_weather_app.model.City;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FavoritesAdapter extends ArrayAdapter<City> {

    public FavoritesAdapter(Context context, ArrayList<City> cities) {
        super(context, 0, cities);
    }

    @NotNull
    @Override
    public View getView(int position, View convertView, @NotNull ViewGroup parent) {
        City city = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_favorite, parent, false);
        }
        TextView textView = convertView.findViewById(R.id.favorite_list_item_textView);
        textView.setText(city != null ? city.getName() : "");
        return convertView;
    }
}