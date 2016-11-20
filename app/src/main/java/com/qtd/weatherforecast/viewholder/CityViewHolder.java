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

    View view;
    int id = 0;
    String name = "";
    String coordinate = "";
    String timeZone = "";
    ViewHolderCallback callback;

    public CityViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        ButterKnife.bind(this, view);
        try {
            callback = (ViewHolderCallback) view.getContext();
        } catch (Exception e) {
            Log.d("error city view holder", e.toString());
        }
    }

    public void setupViewHolder(City city) {
        id = city.getId();
        name = city.getName();
        coordinate = city.getCoordinate();
        timeZone = MyDatabaseHelper.getInstance(view.getContext()).getCurrentWeather(id).getTime();
        tvCity.setText(city.getName());
        tvWeather.setText(String.valueOf(city.getTemp()) + "°, " + city.getWeather());

        radioButton.setChecked(city.isChosen());
        cardView.setCardBackgroundColor(ContextCompat.getColor(view.getContext(), city.isChosen() ? android.R.color.white : R.color.colorWhiteFade));

//        if (city.isChosen()) {
//            cardView.setCardBackgroundColor(ContextCompat.getColor(view.getContext(), android.R.color.white));
//        } else {
//            cardView.setCardBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.colorWhiteFade));
//        }

    }

    @OnClick(R.id.imv_clear)
    void imvClearOnClick() {
        Log.d("city", "clear");
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