package com.hyunki.origin_weather_app.repository;

import com.hyunki.origin_weather_app.model.Forecast;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

public interface Repository {

    Observable<List<Forecast>> getForecasts(String location);
}
