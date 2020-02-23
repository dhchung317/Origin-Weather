package com.hyunki.origin_weather_app.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherResponse {
    @SerializedName("list")
    private List<Forecast> list;

    public List<Forecast> getList() {
        return list;
    }
}