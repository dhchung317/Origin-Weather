package com.hyunki.origin_weather_app.model.util;

public class TempUtil {
    public static int getFahrenheitFromKelvin(double k){
        double f = (k - 273.15) * 9/5 + 32;
        return (int) Math.round(f);
    }
}