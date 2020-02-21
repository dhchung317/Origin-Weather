package com.hyunki.origin_weather_app.model.json;

import android.content.Context;

import com.google.gson.Gson;
import com.hyunki.origin_weather_app.model.City;

import java.io.IOException;
import java.io.InputStream;

import io.reactivex.Observable;


public class JSONUtil {


    public static String inputStreamToString(InputStream inputStream) {
        try {
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes, 0, bytes.length);
            String json = new String(bytes);
            return json;
        } catch (IOException e) {
            return null;
        }
    }

    public static String parseJSONString(Context context, String filename) {
        String returnedJSON = null;
        try {
            returnedJSON = inputStreamToString(context.getAssets().open(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnedJSON;
    }

    public static City[] cityStringListToGson(String JSON){
        City[] cities = new Gson().fromJson(JSON, City[].class);
        return cities;
    }


}
