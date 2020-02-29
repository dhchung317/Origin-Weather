package com.hyunki.origin_weather_app.fragments;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hyunki.origin_weather_app.model.City;

public class FirebaseUtil {
    FirebaseDatabase database;
    FirebaseAuth auth;
    DatabaseReference favoritesStore;
    FirebaseAuth.AuthStateListener listener;

    public FirebaseUtil() {
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        listener = firebaseAuth -> {
            if(auth.getCurrentUser() != null) {
                favoritesStore =
                        database.getReference("/users/" + auth.getCurrentUser().getUid() + "/" + "favorites");
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
}
