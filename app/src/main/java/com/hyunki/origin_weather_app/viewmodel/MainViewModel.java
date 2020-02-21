package com.hyunki.origin_weather_app.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hyunki.origin_weather_app.model.City;
import com.hyunki.origin_weather_app.model.json.JSONUtil;
import com.hyunki.origin_weather_app.repository.RepositoryImpl;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends ViewModel {
    public static final String TAG = "mainviewmodel";
    final RepositoryImpl repository = new RepositoryImpl();
    private CompositeDisposable disposable = new CompositeDisposable();

    private MutableLiveData<State> forecastlivedata = new MutableLiveData();

    private MutableLiveData<State> citylivedata = new MutableLiveData();
//    private MutableLiveData<State> jsonstringlivedata = new MutableLiveData<>();

    public void loadForecasts(String location) {
        forecastlivedata.setValue(State.Loading.INSTANCE);

        disposable.add(
                repository.getForecasts(location)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnError(throwable -> {
                            Log.e(TAG, "getForecasts: ", throwable);
                            forecastlivedata.setValue(State.Error.INSTANCE);
                        })
                        .subscribe(forecasts -> {
                            forecastlivedata.setValue(new State.Success(forecasts));
                        })
        );
    }

    public void loadCities(Context context, String filename) {
        citylivedata.setValue(State.Loading.INSTANCE);
        disposable.add(
                Observable.defer(
                        (Callable<ObservableSource<City[]>>) () -> Observable.just(repository.getCities(context, filename)))
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMapIterable(cities -> Arrays.asList(cities))
//                        .take(10000)
                        .toList()

                        .doOnError(throwable -> {
                            Log.e(TAG, "loadCities: ", throwable);
                        }).subscribe(cities -> citylivedata.setValue(new State.Success(cities)))
        );
    }

//    public void loadJSONString(Context context, String filename) {
//        jsonstringlivedata.setValue(State.Loading.INSTANCE);
//
//        disposable.add(
//                Observable.defer(
//                        (Callable<ObservableSource<String>>) () -> Observable.just(
//                                JSONUtil.parseJSONString(context, filename)))
//                        .subscribeOn(Schedulers.computation())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(jsonString -> jsonstringlivedata.setValue(new State.Success(jsonString)))
//        );
//    }

    public MutableLiveData<State> getForecastLivedata() {
        return forecastlivedata;
    }

    public MutableLiveData<State> getCitylivedata() {
        return citylivedata;
    }

//    public MutableLiveData<State> getJSONStringLivedata() {
//        return jsonstringlivedata;
//    }

}
