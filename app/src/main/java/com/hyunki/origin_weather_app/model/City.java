package com.hyunki.origin_weather_app.model;

public class City {

    private int id;
    private String name;
    private String country;

    public City() {
    }

    public City(String name) {
        this.name = name;
    }

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
