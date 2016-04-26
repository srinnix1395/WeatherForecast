package com.qtd.weatherforecast.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.adapter.WeatherDayAdapter;
import com.qtd.weatherforecast.model.WeatherDay;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Dell on 4/25/2016.
 */
public class DaysWeatherFragment extends Fragment {
    @Bind(R.id.recycleView)
    RecyclerView recyclerView;

    WeatherDayAdapter adapter;
    ArrayList<WeatherDay> weatherDays;
    LinearLayoutManager layoutManager;

    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_days_weather, container, false);
        ButterKnife.bind(this, view);
        initComponent();
        initData();
        return view;
    }

    private void initData() {

    }

    private void initComponent() {
        layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        weatherDays = new ArrayList<>();
        adapter = new WeatherDayAdapter(weatherDays);
        recyclerView.setAdapter(adapter);
    }
}
