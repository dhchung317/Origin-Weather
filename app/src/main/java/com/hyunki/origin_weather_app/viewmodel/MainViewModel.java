package com.hyunki.origin_weather_app.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hyunki.origin_weather_app.model.Forecast;
import com.hyunki.origin_weather_app.repository.RepositoryImpl;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends ViewModel {
    public static final String TAG = "mainviewmodel";
    final RepositoryImpl repository = new RepositoryImpl();
    private CompositeDisposable disposable = new CompositeDisposable();

    private MutableLiveData<State> livedata = new MutableLiveData();

    public void getForecasts(String location) {
        livedata.setValue(State.Loading.INSTANCE);

        disposable.add(
                repository.getForecasts(location)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnError(throwable -> {
                            Log.e(TAG, "getForecasts: ",throwable);
                            livedata.setValue(State.Error.INSTANCE);
                        })
                .subscribe(forecasts -> {
                    livedata.setValue(new State.Success(forecasts));
                })
        );
    }

    public MutableLiveData<State> getLivedata(){
        return livedata;
    }

}
