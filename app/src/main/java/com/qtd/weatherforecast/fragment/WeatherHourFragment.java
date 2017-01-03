package com.qtd.weatherforecast.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.activity.MainActivity;
import com.qtd.weatherforecast.adapter.WeatherHourAdapter;
import com.qtd.weatherforecast.constant.DatabaseConstant;
import com.qtd.weatherforecast.database.MyDatabaseHelper;
import com.qtd.weatherforecast.database.ProcessJson;
import com.qtd.weatherforecast.model.WeatherHour;
import com.qtd.weatherforecast.utils.SharedPreUtils;

import org.json.JSONException;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Dell on 4/25/2016.
 */
public class WeatherHourFragment extends Fragment {
    @Bind(R.id.recycleView)
    RecyclerView recyclerView;
//    @Bind(R.id.chartView)
//    ChartView chartView;

    private WeatherHourAdapter adapter;
    private ArrayList<WeatherHour> weatherHours;
    private MyDatabaseHelper databaseHelper;
    private MainActivity activity;
	
	public static WeatherHourFragment newInstance() {
		return new WeatherHourFragment();
	}
	
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hours_weather, container, false);
        ButterKnife.bind(this, view);
        initComponent();
        initData();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    private void initComponent() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        weatherHours = new ArrayList<>();
        adapter = new WeatherHourAdapter(weatherHours);
        recyclerView.setAdapter(adapter);

//        chartView.setTemperatureArr(new int[24]);
        SnapHelper snapHelperTop = new GravitySnapHelper(Gravity.TOP);
        snapHelperTop.attachToRecyclerView(recyclerView);
    }

    private void initData() {
        databaseHelper = MyDatabaseHelper.getInstance(getContext());
        final int id = SharedPreUtils.getInt(DatabaseConstant._ID, -1);
        if (id != -1) {
            weatherHours.addAll(databaseHelper.getAllWeatherHours(id));
            adapter.notifyDataSetChanged();
        }
    }

    public void displayData(ArrayList<WeatherHour> weatherHourArrayList) {
        weatherHours.clear();

        weatherHours.addAll(weatherHourArrayList);
        adapter.notifyDataSetChanged();
    }

    private void updateDatabase(ArrayList<WeatherHour> arrHour, boolean isInsert, int idCity) {
        for (int i = 0; i < arrHour.size(); i++) {
            if (isInsert) {
                databaseHelper.insertWeatherHour(arrHour.get(i), idCity, i);
            } else {
                databaseHelper.updateWeatherHour(arrHour.get(i), idCity, i);
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            activity.tvLocation.setText(SharedPreUtils.getString(DatabaseConstant.NAME, ""));
            activity.tvTime.setText(R.string.twentyFourHoursToGo);
        }
    }

    public void updateData(String s, int idCity, boolean isInsert) {
        try {
			ArrayList<WeatherHour> allWeatherHours = ProcessJson.getAllWeatherHours(s);
			displayData(allWeatherHours);
            updateDatabase(allWeatherHours, isInsert, idCity);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void chooseItem(int idCity) {
        weatherHours.clear();
        weatherHours.addAll(databaseHelper.getAllWeatherHours(idCity));
        adapter.notifyDataSetChanged();
    }
	
    public void getDataFromDatabase() {
        int id = SharedPreUtils.getInt(DatabaseConstant._ID, -1);
        if (id != -1) {
            weatherHours.clear();

            weatherHours.addAll(databaseHelper.getAllWeatherHours(id));
            adapter.notifyDataSetChanged();
        }
    }
	
	public void updateDegree() {
		adapter.notifyDataSetChanged();
	}
}
