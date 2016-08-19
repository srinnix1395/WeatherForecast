package com.qtd.weatherforecast.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.viewholder.WeatherHourViewHolder;
import com.qtd.weatherforecast.model.WeatherHour;

import java.util.ArrayList;

/**
 * Created by Dell on 4/26/2016.
 */
public class WeatherHourAdapter extends RecyclerView.Adapter<WeatherHourViewHolder> {
    private ArrayList<WeatherHour> weatherHours;

    public WeatherHourAdapter(ArrayList<WeatherHour> weatherHours) {
        this.weatherHours = weatherHours;
    }

    @Override
    public WeatherHourViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather_hour, parent, false);
        return new WeatherHourViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WeatherHourViewHolder holder, int position) {
        holder.setupViewHolder(weatherHours.get(position));
    }

    @Override
    public int getItemCount() {
        return weatherHours.size();
    }
}
