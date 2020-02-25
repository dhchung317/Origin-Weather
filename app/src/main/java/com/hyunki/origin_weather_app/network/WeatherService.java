package com.hyunki.origin_weather_app.network;

import com.hyunki.origin_weather_app.model.WeatherResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {

    @GET("/data/2.5/forecast/")
    Observable<WeatherResponse> getResponse(@Query("q") String location,
                                            @Query("id") String locationId,
                                            @Query("appid") String apiKey);
}
