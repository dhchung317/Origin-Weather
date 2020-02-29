package com.hyunki.origin_weather_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.hyunki.origin_weather_app.adapter.FavoritesAdapter;
import com.hyunki.origin_weather_app.adapter.WeatherPagerAdapter;
import com.hyunki.origin_weather_app.controller.CityClickListener;
import com.hyunki.origin_weather_app.controller.FavoritesClickListener;
import com.hyunki.origin_weather_app.fragments.FirebaseUtil;
import com.hyunki.origin_weather_app.model.City;
import com.hyunki.origin_weather_app.viewmodel.SharedViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import static com.hyunki.origin_weather_app.fragments.WeatherFragment.PERMISSION_ID;

public class MainActivity extends AppCompatActivity implements CityClickListener, FavoritesClickListener {

    private FirebaseUtil firebaseUtil;

    private FirebaseAuth auth;

    private SharedViewModel viewModel;

    private ActionBarDrawerToggle actionBarDrawerToggle;

    private ListView favoriteCitiesListView;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        firebaseUtil = new FirebaseUtil();

        favoriteCitiesListView = findViewById(R.id.favorite_cities_list_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        invalidateOptionsMenu();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView;

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        drawer.addDrawerListener(actionBarDrawerToggle);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        actionBarDrawerToggle.syncState();

        navigationView = findViewById(R.id.nav_view);

        viewModel = ViewModelProviders.of(this).get(SharedViewModel.class);

        ViewPager2 viewPager = findViewById(R.id.viewpager);
        viewPager.setUserInputEnabled(false);
        WeatherPagerAdapter viewPagerAdapter = new WeatherPagerAdapter(getSupportFragmentManager(), this.getLifecycle());
        viewPager.setAdapter(viewPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText(R.string.tab_weather_label);
                    } else if (position == 1) {
                        tab.setText(R.string.tab_explore_label);
                    }
                }).attach();

        //Favorites Dummy Array
        ArrayList<City> dummyArray = new ArrayList<>();

        City city = new City();
        city.setName("Brooklyn");
        City city2 = new City();
        city2.setName("Seoul");
        City city3 = new City();
        city3.setName("London");



        dummyArray.add(city);
        dummyArray.add(city2);
        dummyArray.add(city3);
        //Favorites Dummy Array

        //TODO- factor out dummylist and create method to check firebase for favorite entries under the signed in user
        // - if signed in, and if there are entries, retrieve the entry list, and set the adapter with the list
        // - use context passed in the adapter constructor to write methods in the adapter that will call
        // an onClick listener that will display the selected city's forecast as a page on the exploreFragment
        FavoritesAdapter adapter = new FavoritesAdapter(this, dummyArray);
        favoriteCitiesListView.setAdapter(adapter);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_favorites) {
                toggleFavoritesList();
            }
            if (item.isChecked()) {
                item.setChecked(false);
            } else {
                item.setChecked(true);
            }
            return true;
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewModel.requestNewLocationData();
            }
        }
    }

    @Override
    public void updateFragmentWithCityInfo(City city) {
        viewModel.loadSingleCityById(String.valueOf(city.getId()));
        viewModel.loadForecastsById(String.valueOf(city.getId()));
        viewModel.setCurrentExploredCity(city);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (auth.getCurrentUser() != null) {
            if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
                return true;
            }
        } else {
            showSnackBar(findViewById(R.id.coordinatorLayout), getString(R.string.sign_in_for_favorites_prompt));
        }

        switch (item.getTitle().toString()) {
            case "Sign In": {
                showLoginActivity();
            }
            case "Sign Out": {
                revokeAccess();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.login_switch);

        if (auth.getCurrentUser() != null) {
            item.setTitle(getString(R.string.menu_item_sign_out));
        } else {
            item.setTitle(getString(R.string.menu_item_sign_in));
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void toggleFavoritesList() {
        if (favoriteCitiesListView.getVisibility() == View.GONE) {
            favoriteCitiesListView.setVisibility(View.VISIBLE);
        } else {
            favoriteCitiesListView.setVisibility(View.GONE);
        }
    }

    public void showSnackBar(View v, String message) {
        Snackbar.make(v, message,
                Snackbar.LENGTH_SHORT)
                .show();
    }

    private void showLoginActivity() {
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
    }

    private void revokeAccess() {
        auth.signOut();
    }

    @Override
    public void showForecastForSelectedFavorite(City city) {
        //TODO- factor out snackbar message and instead go to explore fragment and render forecast with city object data
        Toast.makeText(this, String.format("%s %s", getString(R.string.dummylist_snackbar_alert), city.getName()), Toast.LENGTH_SHORT).show();
    }


}