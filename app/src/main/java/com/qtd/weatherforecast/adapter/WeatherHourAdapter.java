package com.qtd.weatherforecast.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.adapter.viewholder.WeatherHourViewHolder;
import com.qtd.weatherforecast.model.WeatherHour;

import java.util.ArrayList;

/**
 * Created by Dell on 4/26/2016.
 */
public class WeatherHourAdapter extends RecyclerView.Adapter<WeatherHourViewHolder> {
    ArrayList<WeatherHour> weatherHours;

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
        WeatherHour weatherHour = weatherHours.get(position);
        holder.setupViewHolder(weatherHour);
    }

    @Override
    public int getItemCount() {
        return weatherHours.size();
    }
}
