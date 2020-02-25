package com.hyunki.origin_weather_app.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class WeatherResponse {
    @SerializedName("list")
    private ArrayList<Forecast> list;

    public ArrayList<Forecast> getList() {
        return list;
    }

    public City city;

    public City getCity() {
        return city;
    }
}