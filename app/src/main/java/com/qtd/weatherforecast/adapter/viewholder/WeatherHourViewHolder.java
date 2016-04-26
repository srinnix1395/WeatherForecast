package com.qtd.weatherforecast.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.model.WeatherHour;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Dell on 4/26/2016.
 */
public class WeatherHourViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.tv_hour)
    TextView tvHour;

    @Bind(R.id.tv_rain)
    TextView tvRain;

    @Bind(R.id.tv_temp)
    TextView tvTemp;

    @Bind(R.id.imv_icon)
    ImageView imvIcon;

    View view;

    public WeatherHourViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        ButterKnife.bind(this, view);
    }

    public void setupViewHolder(WeatherHour weatherHour) {
        tvHour.setText(weatherHour.getHour());
        tvRain.setText(weatherHour.getRain());
        tvTemp.setText(String.valueOf(weatherHour.getTemp()) + "°");
    }
}
