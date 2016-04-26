package com.qtd.weatherforecast.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.model.WeatherDay;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Dell on 4/26/2016.
 */
public class WeatherDayViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.tv_time)
    TextView tvDay;

    @Bind(R.id.tv_weather)
    TextView tvWeather;

    @Bind(R.id.tv_high_temp)
    TextView tvHighTemp;

    @Bind(R.id.tv_low_temp)
    TextView tvLowTemp;

    View view;

    public WeatherDayViewHolder(View itemView) {
        super(itemView);
        this.view = itemView;
        ButterKnife.bind(this, view);
    }

    public void setupViewHolder(WeatherDay weatherDay) {
        tvDay.setText(weatherDay.getDay());
        tvWeather.setText(weatherDay.getWeather());
        tvWeather.setText(weatherDay.getWeather());
        tvHighTemp.setText(weatherDay.getHighTemp() + "°");
        tvLowTemp.setText(weatherDay.getLowTemp() + "°");
    }
}
