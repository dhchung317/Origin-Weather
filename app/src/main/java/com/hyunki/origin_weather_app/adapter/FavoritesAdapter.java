package com.hyunki.origin_weather_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hyunki.origin_weather_app.R;
import com.hyunki.origin_weather_app.controller.CityClickListener;
import com.hyunki.origin_weather_app.controller.FavoritesClickListener;
import com.hyunki.origin_weather_app.model.City;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesViewHolder> {
    private ArrayList<City> favorites = new ArrayList<>();
    private FavoritesClickListener listener;

    public FavoritesAdapter(ArrayList<City> favorites) {
        this.favorites = favorites;
    }

    @Override
    public FavoritesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_favorite, parent, false);
        Context context = parent.getContext();
        if (context instanceof CityClickListener) {
            listener = (FavoritesClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + "must implement CityClickListener");
        }
        return new FavoritesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesViewHolder holder, int position) {
        holder.bind(favorites.get(position),listener);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }


    public void setFavorites(ArrayList<City> favorites) {
        this.favorites = favorites;
        notifyDataSetChanged();
    }
}