package com.qtd.weatherforecast.adapter.viewholder;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.database.MyDatabaseHelper;
import com.qtd.weatherforecast.model.City;

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
    DeleteItemCallback callback;

    public CityViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        ButterKnife.bind(this, view);
//        initComponent();
        try {
            callback = (DeleteItemCallback) view.getContext();
        } catch (Exception e) {
            Log.d("error city view holder", e.toString());
        }
    }

    private void initComponent() {
        tvCity = (TextView) view.findViewById(R.id.tv_city);
        tvWeather = (TextView) view.findViewById(R.id.tv_weather);
        radioButton = (RadioButton) view.findViewById(R.id.radio_chosen);
        cardView = (CardView) view.findViewById(R.id.cardView_item);
    }

    public void setupViewHolder(City city) {
        id = city.getId();
        name = city.getName();
        coordinate = city.getCoordinate();
        timeZone = new MyDatabaseHelper(view.getContext()).getCurrentWeather(id).getTime();
        tvCity.setText(city.getName());
        tvWeather.setText(String.valueOf(city.getTemp()) + "°, " + city.getWeather());
        if (city.isChosen()) {
            radioButton.setChecked(true);
            cardView.setCardBackgroundColor(ContextCompat.getColor(view.getContext(), android.R.color.white));
        } else {
            radioButton.setChecked(false);
            cardView.setCardBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.colorWhiteFade));
        }
    }

    @OnClick(R.id.imv_clear)
    void imvClearOnClick() {
        Log.d("city", "clear");
        callback.deleteItemCity(id);
    }

    @OnClick(R.id.cardView_item)
    void cardViewOnClick() {
        Log.d("city", "choose");
        callback.choseItemCity(id, name, coordinate, timeZone);
    }

    @OnClick(R.id.radio_chosen)
    void radioChosenOnClick() {
        callback.choseItemCity(id, name, coordinate,timeZone);
    }

    public interface DeleteItemCallback {
        void deleteItemCity(int idCity);
        void choseItemCity(int idCity, String name, String coordinate, String timeZone);
    }
}
