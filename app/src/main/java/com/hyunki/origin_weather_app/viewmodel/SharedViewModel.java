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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.hyunki.origin_weather_app.repository.RepositoryImpl;

import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SharedViewModel extends AndroidViewModel {
    private static final String TAG = "mainviewmodel";

    private FusedLocationProviderClient fusedLocationClient
            = LocationServices.getFusedLocationProviderClient(getApplication().getApplicationContext());

    private final RepositoryImpl repository = new RepositoryImpl();

    private CompositeDisposable disposable = new CompositeDisposable();

    private MutableLiveData<State> forecastLiveData = new MutableLiveData<>();
    private MutableLiveData<State> exploredForecastLiveData = new MutableLiveData<>();
    private MutableLiveData<State> cityLiveData = new MutableLiveData<>();
    private MutableLiveData<State> singleCityLiveData = new MutableLiveData<>();
    private MutableLiveData<State> defaultLocation = new MutableLiveData<>();

    public SharedViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadForecasts(String location) {
        forecastLiveData.setValue(State.Loading.INSTANCE);

        disposable.add(
                repository.getForecasts(location)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(forecasts -> {
                                    forecastLiveData.setValue(new State.Success.OnForecastsLoaded(forecasts));
                                },
                                throwable -> {
                                    Log.e(TAG, "getForecasts: ", throwable);
                                    forecastLiveData.setValue(State.Error.INSTANCE);
                                })
        );
    }

    public void loadForecastsById(String id) {
        forecastLiveData.setValue(State.Loading.INSTANCE);

        disposable.add(
                repository.getForecastsById(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(forecasts -> {
                                    exploredForecastLiveData.setValue(new State.Success.OnForecastsByIdLoaded(forecasts));
                                },
                                throwable -> {
                                    Log.e(TAG, "getForecasts: ", throwable);
                                    forecastLiveData.setValue(State.Error.INSTANCE);
                                })
        );
    }

    public void loadSingleCityById(String id) {
        singleCityLiveData.setValue(State.Loading.INSTANCE);
        disposable.add(
                repository.getCityById(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(city -> singleCityLiveData.setValue(new State.Success.OnCityByIdLoaded(city)),
                                throwable -> {
                                    Log.e(TAG, "getsinglecity: ", throwable);
                                    singleCityLiveData.setValue(State.Error.INSTANCE);
                                })
        );
    }

    public void loadCities(Context context, String filename) {
        cityLiveData.setValue(State.Loading.INSTANCE);
        disposable.add(
                Observable.just(repository.getCities(context, filename))
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
//                        .flatMapIterable(cities -> Arrays.asList(cities))
////                        .take(10000)
//                        .toList()
                        .subscribe(cities -> cityLiveData.setValue(new State.Success.OnCitiesLoaded(cities)),
                                throwable -> {
                                    Log.e(TAG, "loadCities: ", throwable);
                                    cityLiveData.setValue(State.Error.INSTANCE);
                                })
        );
    }

    public void loadLastLocation() {
        Log.d(TAG, "loadLastLocation: ran");

        defaultLocation.setValue(State.Loading.INSTANCE);
        fusedLocationClient.getLastLocation().addOnCompleteListener(
                task -> {
                    Location location = task.getResult();
//                    Log.d(TAG, "loadLastLocation: " + location.getLatitude());
                    if (location != null) {
                        Log.d(TAG, "getLastLocation: location succesful " + getLocationString(location));
                        defaultLocation.setValue(
                                new State.Success.OnDefaultLocationLoaded(
                                        getLocationString(location)));
                    }
                });
    }

    @SuppressLint("MissingPermission")
    public void requestNewLocationData() {

        LocationRequest locationRequest = new LocationRequest();
        locationRequest
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(0)
                .setFastestInterval(0)
                .setNumUpdates(1);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplication());
        fusedLocationClient.requestLocationUpdates(
                locationRequest, locationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Log.d(TAG, "onLocationResult: ran");
            String location = getLocationString(locationResult.getLocations().get(0));
            Log.d(TAG, "onLocationResult: " + location);
            defaultLocation.setValue(
                    new State.Success.OnDefaultLocationLoaded(location));
        }
    };


    public LiveData<State> getForecastLiveData() {
        return forecastLiveData;
    }

    public LiveData<State> getCityLiveData() {
        return cityLiveData;
    }

    public LiveData<State> getDefaultLocation() {
        return defaultLocation;
    }

    public LiveData<State> getExploredForecastLiveData() { return exploredForecastLiveData; }

    public LiveData<State> getSingleCityLiveData() {
        return singleCityLiveData;
    }

    private String getLocationString(Location location) {
        Geocoder gcd = new Geocoder(getApplication(), Locale.getDefault());
        Address address;
        String locationString = "";
        try {
            address = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0);
            String locality = address.getSubLocality();
            String state = address.getAdminArea();
            locationString = String.format("%s, %s", locality, state);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return locationString;
    }

}
