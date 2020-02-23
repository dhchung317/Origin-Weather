package com.hyunki.origin_weather_app.model.util;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyunki.origin_weather_app.model.City;

import java.io.IOException;


public class JSONUtil {
    public static final String TAG = "jsonutil";

    public static City[] cityJSONtoJACKSON(Context context, String filename){
        ObjectMapper mapper = new ObjectMapper();
        City[] cities = null;
        try {
            Log.d(TAG, "cityJSONtoJACKSON: trying");
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            cities = mapper.readValue(context.getAssets().open(filename), City[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cities;
    }
}
