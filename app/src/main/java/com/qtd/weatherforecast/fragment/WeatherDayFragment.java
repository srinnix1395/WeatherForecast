package com.qtd.weatherforecast.fragment;

import android.content.Context;
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
import com.qtd.weatherforecast.model.WeatherDay;
import com.qtd.weatherforecast.utils.SharedPreUtils;

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
    private MainActivity activity;
	
	public static WeatherDayFragment newInstance() {
		return new WeatherDayFragment();
	}
	
	@Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_days_weather, container, false);
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

        weatherDays = new ArrayList<>();
        adapter = new WeatherDayAdapter(weatherDays);
        recyclerView.setAdapter(adapter);
    }

    private void initData() {
        databaseHelper = MyDatabaseHelper.getInstance(getContext());
        int id = SharedPreUtils.getInt(DatabaseConstant._ID, -1);
        if (id != -1) {
            weatherDays.addAll(databaseHelper.getAllWeatherDays(id));
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            activity.tvLocation.setText(SharedPreUtils.getString(DatabaseConstant.NAME, ""));
            activity.tvTime.setText(R.string.sixDaysToGo);
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

            weatherDays.addAll(databaseHelper.getAllWeatherDays(id));
            adapter.notifyDataSetChanged();
        }
    }
	
	public void updateDegree() {
		adapter.notifyDataSetChanged();
	}
}
