package com.hyunki.origin_weather_app.controller;

import com.hyunki.origin_weather_app.model.City;

public interface FavoritesClickListener {
    void showForecastForSelectedFavorite(City city);
}
