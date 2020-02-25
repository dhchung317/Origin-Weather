package com.hyunki.origin_weather_app.model;

import com.google.gson.annotations.SerializedName;

public class Temp {
    @SerializedName("temp")
    private double temp;

    @SerializedName("temp_min")
    private double temp_min;

    @SerializedName("temp_max")
    private double temp_max;

    public double getTempKelvin() {
        return temp;
    }

    public double getTemp_min() {
        return temp_min;
    }

    public double getTemp_max() {
        return temp_max;
    }
}
