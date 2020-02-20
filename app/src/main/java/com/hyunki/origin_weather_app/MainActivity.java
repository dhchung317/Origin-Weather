package com.hyunki.origin_weather_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.hyunki.origin_weather_app.model.Forecast;
import com.hyunki.origin_weather_app.model.WeatherResponse;
import com.hyunki.origin_weather_app.network.RetrofitFactory;
import com.hyunki.origin_weather_app.network.WeatherService;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "main--";

    public static final String API_KEY = "872ec5abb676430b71b8708ce8e2e1ba";

//icons - http://openweathermap.org/img/wn/ {10d} @2x.png




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RetrofitFactory.getInstance()
                .create(WeatherService.class)
                .getResponse("london",API_KEY)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d(TAG, "accept: " + throwable);
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<WeatherResponse>() {
                    @Override
                    public void accept(WeatherResponse weatherResponse) throws Exception {
                        Log.d(TAG, "accept: " + weatherResponse.getList().size());
                        Log.d(TAG, "accept: " + weatherResponse.getList().get(0).getWeather().get(0).getDescription());
                        Log.d(TAG, "accept: " + weatherResponse.getList().get(0).getWeather().get(0).getIcon());
                        Log.d(TAG, "accept: " + weatherResponse.getList().get(0).getWeather().get(0).getMain());
                        Log.d(TAG, "accept: " + weatherResponse.getList().get(0).getTemp().getTemp());
                        Log.d(TAG, "accept: " + weatherResponse.getList().get(0).getDate());
                        Log.d(TAG, "accept: " + weatherResponse.getList().get(1).getDate());
                        Log.d(TAG, "accept: " + weatherResponse.getList().get(2).getDate());

                    }
                });
    }

}

//        - Displays users location and local weather (in Fahrenheit) upon opening app
//        - Present detailed weather conditions (rain, sleet, sunny, etc.) with strong attention to design
//        - Allows users to search for weather in other cities
//        - Allow users to be able to register and login using Firebase api
//        - Styling is key!
//        Bonus:
//        - Be able to save certain cities you’re interested in (or visit frequently),
//        and have that data present from a navigation perspective.
//        - Present a few future days of generic (less-defined) weather for a city
