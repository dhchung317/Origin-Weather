package com.hyunki.origin_weather_app.repository;

import android.content.Context;

import com.hyunki.origin_weather_app.model.City;
import com.hyunki.origin_weather_app.model.Forecast;

import java.util.ArrayList;

import io.reactivex.Observable;

public interface Repository {

    Observable<ArrayList<Forecast>> getForecasts(String location);

    Observable<ArrayList<Forecast>> getForecastsById(String id);

    Observable<City> getCityById(String id);

    ArrayList<City> getCities(Context context, String filename);

}
