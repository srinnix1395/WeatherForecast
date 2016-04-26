package com.qtd.weatherforecast.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.qtd.weatherforecast.fragment.CurrentWeatherFragment;
import com.qtd.weatherforecast.fragment.DaysWeatherFragment;
import com.qtd.weatherforecast.fragment.HoursWeatherFragment;
import com.qtd.weatherforecast.fragment.SearchFragment;

/**
 * Created by Dell on 4/25/2016.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 4;

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new SearchFragment();
            case 1:
                return new CurrentWeatherFragment();
            case 2:
                return new HoursWeatherFragment();
            case 3:
                return new DaysWeatherFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}
