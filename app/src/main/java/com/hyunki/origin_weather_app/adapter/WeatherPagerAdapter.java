package com.hyunki.origin_weather_app.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hyunki.origin_weather_app.fragments.FragmentA;
import com.hyunki.origin_weather_app.fragments.FragmentB;

public class WeatherPagerAdapter extends FragmentPagerAdapter {


    public WeatherPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0)
        {
            fragment = new FragmentA();
        }
        else if (position == 1)
        {
            fragment = new FragmentB();
        }

        return fragment;

    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0)
        {
            title = "My Weather";
        }
        else if (position == 1)
        {
            title = "Explore";
        }
        return title;
    }
}
