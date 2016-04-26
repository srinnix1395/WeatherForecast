package com.qtd.weatherforecast.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.model.City;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Dell on 4/26/2016.
 */
public class CityViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.radio_city)
    RadioButton radioButton;

    @Bind(R.id.tv_city)
    TextView tvCity;

    @Bind(R.id.tv_weather)
    TextView tvWeather;

    View view;
    public CityViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        ButterKnife.bind(this, view);
    }

    public void setupViewHolder(City city) {
        tvCity.setText(city.getName());
        tvWeather.setText(String.valueOf(city.getTemp()) + "°, " + tvWeather );
    }

    @OnClick(R.id.imv_clear)
    void imvClearOnClick() {

    }
}
