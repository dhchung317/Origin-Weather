package com.hyunki.origin_weather_app.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.hyunki.origin_weather_app.fragments.ExploreFragment;
import com.hyunki.origin_weather_app.fragments.WeatherFragment;

public class WeatherPagerAdapter extends FragmentStateAdapter {

    public WeatherPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new WeatherFragment();
            case 1:
                return new ExploreFragment();
        }
        return new WeatherFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}

