package com.qtd.weatherforecast.viewholder;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.callback.ViewHolderCallback;
import com.qtd.weatherforecast.database.MyDatabaseHelper;
import com.qtd.weatherforecast.model.City;
import com.qtd.weatherforecast.utils.SharedPreUtils;
import com.qtd.weatherforecast.utils.StringUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Dell on 4/26/2016.
 */
public class CityViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.tv_city)
    TextView tvCity;

    @Bind(R.id.tv_weather)
    TextView tvWeather;

    @Bind(R.id.radio_chosen)
    RadioButton radioButton;

    @Bind(R.id.cardView_item)
    CardView cardView;

    private int id = 0;
    private String name = "";
    private String coordinate = "";
    private String timeZone = "";
    private ViewHolderCallback callback;

    public CityViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        try {
            callback = (ViewHolderCallback) itemView.getContext();
        } catch (Exception e) {
            Log.d("error city view holder", e.toString());
        }
    }

    public void setupViewHolder(City city) {
        id = city.getId();
        name = city.getName();
        coordinate = city.getCoordinate();
        timeZone = MyDatabaseHelper.getInstance(itemView.getContext()).getCurrentWeather(id).getTime();
        tvCity.setText(city.getName());

        String temp = StringUtils.getTemp(city.getTemp());
        tvWeather.setText(temp + "°, " + city.getWeather());

        radioButton.setChecked(city.isChosen());
        cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), city.isChosen() ? android.R.color.white : R.color.colorWhiteFade));
    }

    @OnClick(R.id.imv_clear)
    void imvClearOnClick() {
        callback.deleteItemCity(id);
    }

    @OnClick({R.id.cardView_item, R.id.radio_chosen})
    void cardViewOnClick() {
        int idChosen = SharedPreUtils.getInt("ID", -1);
        if (idChosen != id) {
            callback.choseItemCity(id, name, coordinate, timeZone);
        }
    }
}
