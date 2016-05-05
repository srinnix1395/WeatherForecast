package com.qtd.weatherforecast.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Dell on 4/25/2016.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 4;

    ArrayList<Fragment> fragments;

    public MainPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return fragments.get(0);
            case 1:
                return fragments.get(1);
            case 2:
                return fragments.get(2);
            case 3:
                return fragments.get(3);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}
