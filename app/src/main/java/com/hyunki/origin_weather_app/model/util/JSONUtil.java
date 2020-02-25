package com.hyunki.origin_weather_app.model.util;

import android.content.Context;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyunki.origin_weather_app.model.City;

import java.io.IOException;
import java.util.ArrayList;


public class JSONUtil {

    public static ArrayList<City> cityJSONtoJACKSON(Context context, String filename) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<City> cities = null;
        try {
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            cities = mapper.readValue(context.getAssets().open(filename),
                    mapper.getTypeFactory().constructCollectionType(ArrayList.class, City.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cities;
    }
}
