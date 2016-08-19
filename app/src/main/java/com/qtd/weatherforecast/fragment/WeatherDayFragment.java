package com.qtd.weatherforecast.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.activity.MainActivity;
import com.qtd.weatherforecast.adapter.WeatherDayAdapter;
import com.qtd.weatherforecast.constant.DatabaseConstant;
import com.qtd.weatherforecast.database.MyDatabaseHelper;
import com.qtd.weatherforecast.database.ProcessJson;
import com.qtd.weatherforecast.model.WeatherDay;
import com.qtd.weatherforecast.utils.SharedPreUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Dell on 4/25/2016.
 */
public class WeatherDayFragment extends Fragment {
    @Bind(R.id.recycleView)
    RecyclerView recyclerView;

    private WeatherDayAdapter adapter;
    private ArrayList<WeatherDay> weatherDays;
    private MyDatabaseHelper databaseHelper;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_days_weather, container, false);
        ButterKnife.bind(this, view);
        initComponent();
        initData();
        return view;
    }

    private void initComponent() {
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setHasFixedSize(true);

        weatherDays = new ArrayList<>();
        adapter = new WeatherDayAdapter(weatherDays);
        recyclerView.setAdapter(adapter);
    }

    private void initData() {
        databaseHelper = MyDatabaseHelper.getInstance(view.getContext());
        final int id = SharedPreUtils.getInt(DatabaseConstant._ID, -1);
        if (id != -1) {
            weatherDays.addAll(databaseHelper.getAllWeatherDays(id));
            adapter.notifyDataSetChanged();
        }
    }

    private void displayData(JSONObject response) {
        weatherDays.clear();
        adapter.notifyDataSetChanged();

        weatherDays.addAll(ProcessJson.getAllWeatherDays(response));
        adapter.notifyDataSetChanged();
    }

    private void updateDatabase(JSONObject response, boolean isInsert, int idCity) {
        ArrayList<WeatherDay> arrDay = ProcessJson.getAllWeatherDays(response);
        for (int i = 0; i < arrDay.size(); i++) {
            if (isInsert) {
                databaseHelper.insertWeatherDay(arrDay.get(i), idCity, i);
            } else {
                databaseHelper.updateWeatherDay(arrDay.get(i), idCity, i);
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            ((MainActivity) getActivity()).getTvLocation().setText(SharedPreUtils.getString(DatabaseConstant.NAME, ""));
            ((MainActivity) getActivity()).getTvTime().setText(R.string.sixDaysToGo);
        }
    }

    public void updateData(String s, int idCity, boolean isInsert) {
        try {
            JSONObject object = new JSONObject(s);
            displayData(object);
            updateDatabase(object, isInsert, idCity);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void chooseItem(int idCity) {
        weatherDays.clear();
        weatherDays.addAll(databaseHelper.getAllWeatherDays(idCity));
        adapter.notifyDataSetChanged();
    }

    public void getDataFromDatabase() {
        int id = SharedPreUtils.getInt(DatabaseConstant._ID, -1);
        if (id != -1) {
            weatherDays.clear();
            adapter.notifyDataSetChanged();

            weatherDays.addAll(databaseHelper.getAllWeatherDays(id));
            adapter.notifyDataSetChanged();
        }
    }
}
