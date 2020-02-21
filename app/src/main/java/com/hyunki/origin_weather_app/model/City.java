package com.hyunki.origin_weather_app.model;

import com.google.gson.annotations.SerializedName;

public class City {
    @SerializedName("id")
    int countryCode;

    @SerializedName("name")
    String city;

    @SerializedName("country")
    String country;

    public int getCountryCode() {
        return countryCode;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }
}
