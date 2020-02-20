package com.hyunki.origin_weather_app.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Forecast {

    @SerializedName("main")
    private Temp temp;

    @SerializedName("weather")
    private List<Weather> weather;

    private String dt_txt;

    public Temp getTemp() {
        return temp;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public String getDate() {
        return dt_txt;
    }


}
