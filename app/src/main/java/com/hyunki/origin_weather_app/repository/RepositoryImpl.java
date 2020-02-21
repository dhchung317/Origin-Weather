package com.hyunki.origin_weather_app.repository;

import android.content.Context;

import com.hyunki.origin_weather_app.model.City;
import com.hyunki.origin_weather_app.model.Forecast;
import com.hyunki.origin_weather_app.model.WeatherResponse;
import com.hyunki.origin_weather_app.model.json.JSONUtil;
import com.hyunki.origin_weather_app.network.RetrofitFactory;
import com.hyunki.origin_weather_app.network.WeatherService;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class RepositoryImpl implements Repository {

    public static final String API_KEY = "872ec5abb676430b71b8708ce8e2e1ba";

    @Override
    public Observable<List<Forecast>> getForecasts(String location) {
        return RetrofitFactory.getInstance()
                .create(WeatherService.class)
                .getResponse(location,API_KEY)
                .map(new Function<WeatherResponse, List<Forecast>>() {
                    @Override
                    public List<Forecast> apply(WeatherResponse weatherResponse) throws Exception {
                        return weatherResponse.getList();
                    }
                });
    }

    @Override
    public City[] getCities(String JSON) {
        return JSONUtil.cityStringListToGson(JSON);
    }


}
