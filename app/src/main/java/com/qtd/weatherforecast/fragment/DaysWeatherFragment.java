package com.qtd.weatherforecast.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qtd.weatherforecast.AppController;
import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.activity.MainActivity;
import com.qtd.weatherforecast.adapter.WeatherDayAdapter;
import com.qtd.weatherforecast.constant.ApiConstant;
import com.qtd.weatherforecast.constant.DatabaseConstant;
import com.qtd.weatherforecast.database.MyDatabaseHelper;
import com.qtd.weatherforecast.model.WeatherDay;
import com.qtd.weatherforecast.utility.NetworkUtil;
import com.qtd.weatherforecast.utility.SharedPreUtils;
import com.qtd.weatherforecast.utility.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Dell on 4/25/2016.
 */
public class DaysWeatherFragment extends Fragment {
    @Bind(R.id.recycleView)
    RecyclerView recyclerView;

    WeatherDayAdapter adapter;
    ArrayList<WeatherDay> weatherDays;
    LinearLayoutManager layoutManager;

    MyDatabaseHelper databaseHelper;

    View view;

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
        layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        weatherDays = new ArrayList<>();
        adapter = new WeatherDayAdapter(weatherDays);
        recyclerView.setAdapter(adapter);
    }

    private void initData() {
        databaseHelper = MyDatabaseHelper.getInstance(view.getContext());
        final int id = SharedPreUtils.getInt("ID", -1);
        if (id != -1) {
            if (NetworkUtil.getInstance().isNetworkAvailable(view.getContext())) {
                String url = StringUtils.getURL("forecast10day", SharedPreUtils.getString(ApiConstant.COORDINATE, "-1"));
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        displayData(response);
                        updateDatabase(response, false, id);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error", error.toString());
                    }
                });
                AppController.getInstance().addToRequestQueue(jsonObjectRequest);
            } else {
                weatherDays.addAll(databaseHelper.getAllWeatherDays(id));
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void displayData(JSONObject response) {
        weatherDays.clear();
        adapter.notifyDataSetChanged();
        try {
            JSONObject forecast = response.getJSONObject("forecast");
            JSONObject simpleForecast = forecast.getJSONObject("simpleforecast");
            JSONArray forecastDay = simpleForecast.getJSONArray("forecastday");
            for (int i = 0; i < 6; i++) {
                JSONObject object = forecastDay.getJSONObject(i);
                JSONObject date = object.getJSONObject("date");
                String weekday = date.getString("weekday");
                weekday = weekday.substring(5);
                int highTemp = object.getJSONObject("high").getInt("celsius");
                int lowTemp = object.getJSONObject("low").getInt("celsius");
                String weather = object.getString("conditions");
                String icon = object.getString("icon");

                WeatherDay weatherDay = new WeatherDay(weekday, weather, highTemp, lowTemp, icon);
                weatherDays.add(weatherDay);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
    }

    private void updateDatabase(JSONObject response, boolean isInsert, int idCity) {
        try {
            JSONObject forecast = response.getJSONObject("forecast");
            JSONObject simpleForecast = forecast.getJSONObject("simpleforecast");
            JSONArray forecastDay = simpleForecast.getJSONArray("forecastday");
            for (int i = 0; i < 6; i++) {
                JSONObject object = forecastDay.getJSONObject(i);
                JSONObject date = object.getJSONObject("date");
                String weekday = date.getString("weekday");
                weekday = weekday.substring(5);
                int highTemp = object.getJSONObject("high").getInt("celsius");
                int lowTemp = object.getJSONObject("low").getInt("celsius");
                String weather = object.getString("conditions");
                String icon = object.getString("icon");

                WeatherDay weatherDay = new WeatherDay(weekday, weather, highTemp, lowTemp, icon);

                if (isInsert) {
                    databaseHelper.insertWeatherDay(weatherDay, idCity, i);
                } else {
                    databaseHelper.updateWeatherDay(weatherDay, idCity, i);
                }

            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            ((MainActivity) getActivity()).getTvLocation().setText(SharedPreUtils.getString(DatabaseConstant.NAME, ""));
            ((MainActivity) getActivity()).getTvTime().setText("6 ngày tiếp theo");
        }
    }

    public void updateData(String s, int idCity, boolean isInsert) {
        try {
            JSONObject object1 = new JSONObject(s);
            displayData(object1);
            if (isInsert) {
                updateDatabase(object1, true, idCity);
            } else {
                updateDatabase(object1, false, idCity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void chooseItem(int idCity) {
        weatherDays.clear();
        weatherDays.addAll(databaseHelper.getAllWeatherDays(idCity));
        adapter.notifyDataSetChanged();
    }
}
