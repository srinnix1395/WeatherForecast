package com.qtd.weatherforecast.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.adapter.viewholder.WeatherDayViewHolder;
import com.qtd.weatherforecast.model.WeatherDay;

import java.util.ArrayList;

/**
 * Created by Dell on 4/26/2016.
 */
public class WeatherDayAdapter extends RecyclerView.Adapter<WeatherDayViewHolder> {
    ArrayList<WeatherDay> weatherDays;

    public WeatherDayAdapter(ArrayList<WeatherDay> weatherDays) {
        this.weatherDays = weatherDays;
    }

    @Override
    public WeatherDayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather_day, parent, false);
        return new WeatherDayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WeatherDayViewHolder holder, int position) {
        WeatherDay weatherDay = weatherDays.get(position);
        holder.setupViewHolder(weatherDay, position);
    }

    @Override
    public int getItemCount() {
        return weatherDays.size();
    }
}
