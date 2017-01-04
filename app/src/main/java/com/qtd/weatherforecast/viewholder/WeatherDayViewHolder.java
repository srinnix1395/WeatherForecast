package com.qtd.weatherforecast.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.model.WeatherDay;
import com.qtd.weatherforecast.utils.UiHelper;
import com.qtd.weatherforecast.utils.StringUtils;

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

    @Bind(R.id.imv_icon)
    ImageView imvIcon;

    @Bind(R.id.imv_line)
    ImageView imvLine;

    public WeatherDayViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setupViewHolder(WeatherDay weatherDay, boolean isLast) {
        tvDay.setText(weatherDay.getDay());
        tvWeather.setText(weatherDay.getWeather());
        imvIcon.setImageResource(UiHelper.getImageResource(weatherDay.getIcon()));

        tvHighTemp.setText(StringUtils.getTemp(weatherDay.getHighTemp()));
        tvLowTemp.setText(StringUtils.getTemp(weatherDay.getLowTemp()));

        if (isLast) {
            imvLine.setVisibility(View.INVISIBLE);
        } else {
            imvLine.setVisibility(View.VISIBLE);
        }
    }
}
