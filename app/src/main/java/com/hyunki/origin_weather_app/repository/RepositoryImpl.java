package com.hyunki.origin_weather_app.repository;

import android.content.Context;

import com.hyunki.origin_weather_app.model.City;
import com.hyunki.origin_weather_app.model.Forecast;
import com.hyunki.origin_weather_app.model.WeatherResponse;
import com.hyunki.origin_weather_app.model.util.JSONUtil;
import com.hyunki.origin_weather_app.network.RetrofitFactory;
import com.hyunki.origin_weather_app.network.WeatherService;

import java.util.ArrayList;

import io.reactivex.Observable;

public class RepositoryImpl implements Repository {

    //TODO- hide api key
    private static final String API_KEY = "872ec5abb676430b71b8708ce8e2e1ba";

    @Override
    public Observable<ArrayList<Forecast>> getForecasts(String location) {
        return RetrofitFactory.getInstance()
                .create(WeatherService.class)
                .getResponse(location, null, API_KEY)
                .map(WeatherResponse::getList);
    }

    @Override
    public Observable<ArrayList<Forecast>> getForecastsById(String id) {
        return RetrofitFactory.getInstance()
                .create(WeatherService.class)
                .getResponse(null, id, API_KEY)
                .map(WeatherResponse::getList);
    }

    @Override
    public Observable<City> getCityById(String id) {
        return RetrofitFactory.getInstance()
                .create(WeatherService.class)
                .getResponse(null, id, API_KEY)
                .map(WeatherResponse::getCity);
    }

    @Override
    public ArrayList<City> getCities(Context context, String filename) {
        return JSONUtil.cityJSONtoJACKSON(context, filename);
    }
}
