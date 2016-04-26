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
import com.qtd.weatherforecast.adapter.WeatherHourAdapter;
import com.qtd.weatherforecast.model.WeatherHour;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Dell on 4/25/2016.
 */
public class HoursWeatherFragment extends Fragment {
    @Bind(R.id.recycleView)
    RecyclerView recyclerView;

    WeatherHourAdapter adapter;
    ArrayList<WeatherHour> weatherHours;
    LinearLayoutManager layoutManager;

    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_hours_weather, container, false);
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
        weatherHours = new ArrayList<>();
        adapter = new WeatherHourAdapter(weatherHours);
        recyclerView.setAdapter(adapter);
    }
}
