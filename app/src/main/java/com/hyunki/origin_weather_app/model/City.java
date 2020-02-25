package com.hyunki.origin_weather_app.model;

import com.google.gson.annotations.SerializedName;

public class City {

    int id;
    String name;
    String country;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public void setName(String name) {
        this.name = name;
    }
}
