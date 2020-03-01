package com.hyunki.origin_weather_app.fragments;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hyunki.origin_weather_app.model.City;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class FirebaseUtil {
    FirebaseDatabase database;
    FirebaseAuth auth;
    DatabaseReference favoritesStore;
    FirebaseAuth.AuthStateListener listener;
    Set<Integer> cityIdSet = new HashSet<>();
    ArrayList<City> favoriteCities = new ArrayList<>();

    public FirebaseUtil() {
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        listener = firebaseAuth -> {
            Log.d("firebaseutil", "FirebaseUtil: listener hit");
            if(auth.getCurrentUser() != null) {
                favoritesStore =
                        database.getReference("/users/" + auth.getCurrentUser().getUid() + "/" + "favorites");
                loadFavoriteSet();
            }
        };
        auth.addAuthStateListener(listener);
    }

    public void addFavorite(City city){
        if (auth.getCurrentUser() != null){
            favoritesStore.child(city.getName()).setValue(city);
        }
    }

    public void removeFavorite(City city){
        if(auth.getCurrentUser() != null){
            favoritesStore.child(city.getName()).removeValue();
        }
    }

    public ArrayList<City> getFavorites() {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                favoriteCities.clear();
                dataSnapshot.getChildren().forEach(favorite -> {
                    City city = favorite.getValue(City.class);
                    favoriteCities.add(city);
                    Log.d("explorefirebaseutil", "onDataChange: " + city.getName());
                });
                Log.d("explorefragment", "onDataChange: " + favoriteCities.size());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        favoritesStore.addListenerForSingleValueEvent(listener);

        Log.d("firebaseutil", "getFavorites: " + favoriteCities.size());

        return favoriteCities;
    }

    private void loadFavoriteSet(){
        favoritesStore.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cityIdSet.clear();
                dataSnapshot.getChildren().forEach(favorite -> {
                    City city = favorite.getValue(City.class);
                    cityIdSet.add(city.getId());
                    Log.d("explorefirebaseutil", "onDataChange: " + city.getName());
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public boolean isFavorite(City city){

        if(cityIdSet.contains(city.getId())){
            return true;
        }else{
            return false;
        }
    }
}
