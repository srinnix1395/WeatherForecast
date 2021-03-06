package com.qtd.weatherforecast.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.model.WeatherHour;
import com.qtd.weatherforecast.utils.StringUtils;
import com.qtd.weatherforecast.utils.UiHelper;

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
	
	public WeatherHourViewHolder(View itemView) {
		super(itemView);
		ButterKnife.bind(this, itemView);
	}
	
	public void setupViewHolder(WeatherHour weatherHour) {
		tvHour.setText(weatherHour.getHour());
		if (!weatherHour.getRain().equals("0%")) {
			tvRain.setText(weatherHour.getRain());
		} else {
			tvRain.setText("");
		}
		tvTemp.setText(String.format("%s°", StringUtils.getTemp(weatherHour.getTemp())));
		imvIcon.setImageResource(UiHelper.getImageResource(weatherHour.getIcon()));
	}
}
