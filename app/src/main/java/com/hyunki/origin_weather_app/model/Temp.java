package com.hyunki.origin_weather_app.model;

import com.google.gson.annotations.SerializedName;

public class Temp {
    @SerializedName("temp")
    double temp;

    @SerializedName("temp_min")
    double temp_min;

    @SerializedName("temp_max")
    double temp_max;

    public double getTemp() {
        return temp;
    }

    public double getTemp_min() {
        return temp_min;
    }

    public double getTemp_max() {
        return temp_max;
    }
}
