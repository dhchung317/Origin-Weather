package com.hyunki.origin_weather_app.repository;

import android.content.Context;

import com.hyunki.origin_weather_app.model.City;
import com.hyunki.origin_weather_app.model.Forecast;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

public interface Repository {

    Observable<List<Forecast>> getForecasts(String location);

    Observable<List<Forecast>> getForecastsById(String id);

    Observable<City> getCityById(String id);

    City[] getCities(Context context, String filename);

}
