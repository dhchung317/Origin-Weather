package com.hyunki.origin_weather_app.model.json;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.hyunki.origin_weather_app.model.City;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.Observable;


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
