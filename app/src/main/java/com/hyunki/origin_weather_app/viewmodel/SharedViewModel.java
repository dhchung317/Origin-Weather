package com.hyunki.origin_weather_app.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.hyunki.origin_weather_app.model.City;
import com.hyunki.origin_weather_app.repository.RepositoryImpl;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SharedViewModel extends AndroidViewModel {
    public static final String TAG = "mainviewmodel";

    private FusedLocationProviderClient fusedLocationClient
            = LocationServices.getFusedLocationProviderClient(getApplication().getApplicationContext());

    final RepositoryImpl repository = new RepositoryImpl();

    private CompositeDisposable disposable = new CompositeDisposable();

    private MutableLiveData<State> forecastlivedata = new MutableLiveData();

    private MutableLiveData<State> citylivedata = new MutableLiveData();

    private MutableLiveData<String> defaultLocation = new MutableLiveData<>();

    public SharedViewModel(@NonNull Application application) {
        super(application);
    }

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
//                        .flatMapIterable(cities -> Arrays.asList(cities))
////                        .take(10000)
//                        .toList()
                        .doOnError(throwable -> {
                            Log.e(TAG, "loadCities: ", throwable);
                        }).subscribe(cities -> citylivedata.setValue(new State.Success(cities)))
        );
    }

        public void loadLastLocation(){
            Log.d(TAG, "loadLastLocation: ran");

        fusedLocationClient.getLastLocation().addOnCompleteListener(
                task -> {
                    Location location = task.getResult();
//                    Log.d(TAG, "loadLastLocation: " + location.getLatitude());
                    if (location != null) {
                        Log.d(TAG, "getLastLocation: location succesful");
                        defaultLocation.setValue(getLocationString(location));
                    }
                });
    }


    public MutableLiveData<State> getForecastLivedata() {
        return forecastlivedata;
    }

    public MutableLiveData<State> getCitylivedata() {
        return citylivedata;
    }

    public MutableLiveData<String> getDefaultLocation() {
        return defaultLocation;
    }

    private String getLocationString(Location location) {
        Geocoder gcd = new Geocoder(getApplication(),
                Locale.getDefault());
        Address address;
        String locationString = "";
        try {
            address = gcd.getFromLocation(location.getLatitude(),location.getLongitude(),1).get(0);
            String locality = address.getSubLocality();
            String state = address.getAdminArea();
            locationString = String.format("%s,%s", locality, state);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return locationString;
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(0)
                .setFastestInterval(0)
                .setNumUpdates(1);

        fusedLocationClient.requestLocationUpdates(
                locationRequest, locationCallback,
                Looper.myLooper()
        );
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
//            if(swipeRefreshLayout.isRefreshing()) {
//                swipeRefreshLayout.setRefreshing(false);
//            }
            defaultLocation.setValue(
                    getLocationString(locationResult.getLastLocation()));
        }
    };

}
