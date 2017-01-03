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

    private View view;

    public WeatherDayViewHolder(View itemView) {
        super(itemView);
        this.view = itemView;
        ButterKnife.bind(this, view);
    }

    public void setupViewHolder(WeatherDay weatherDay, boolean isLast) {
        tvDay.setText(weatherDay.getDay());
        tvWeather.setText(weatherDay.getWeather());
        int icon = UiHelper.getImageResource(weatherDay.getIcon());
        imvIcon.setImageResource(icon);

        tvHighTemp.setText(StringUtils.getTemp(weatherDay.getHighTemp()));
        tvLowTemp.setText(StringUtils.getTemp(weatherDay.getLowTemp()));

        if (isLast) {
            imvLine.setImageResource(R.drawable.background_gach_trong);
        } else {
            imvLine.setImageResource(R.drawable.background_gach_gray);
        }
    }
}
