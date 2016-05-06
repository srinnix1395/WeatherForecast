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
import com.qtd.weatherforecast.adapter.WeatherHourAdapter;
import com.qtd.weatherforecast.constant.ApiConstant;
import com.qtd.weatherforecast.constant.DatabaseConstant;
import com.qtd.weatherforecast.database.MyDatabaseHelper;
import com.qtd.weatherforecast.database.ProcessJson;
import com.qtd.weatherforecast.model.WeatherHour;
import com.qtd.weatherforecast.utility.NetworkUtil;
import com.qtd.weatherforecast.utility.SharedPreUtils;
import com.qtd.weatherforecast.utility.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Dell on 4/25/2016.
 */
public class HoursWeatherFragment extends Fragment{
    @Bind(R.id.recycleView)
    RecyclerView recyclerView;

    WeatherHourAdapter adapter;
    ArrayList<WeatherHour> weatherHours;
    LinearLayoutManager layoutManager;

    MyDatabaseHelper databaseHelper;

    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_hours_weather, container, false);
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
        weatherHours = new ArrayList<>();
        adapter = new WeatherHourAdapter(weatherHours);
        recyclerView.setAdapter(adapter);
    }

    private void initData() {
        databaseHelper = MyDatabaseHelper.getInstance(view.getContext());
        final int id = SharedPreUtils.getInt("ID", -1);
        if (id != -1) {
            if (NetworkUtil.getInstance().isNetworkAvailable(view.getContext())) {
                String url = StringUtils.getURL("hourly", SharedPreUtils.getString(ApiConstant.COORDINATE, "-1"));
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
                weatherHours.addAll(databaseHelper.getAllWeatherHours(id));
                adapter.notifyDataSetChanged();
            }
        }


    }

    public void displayData(JSONObject response) {
        weatherHours.clear();
        adapter.notifyDataSetChanged();

        weatherHours.addAll(ProcessJson.getAllWeatherHours(response));

//        try {
//            JSONArray currentObservation = response.getJSONArray("hourly_forecast");
//            for (int i = 0; i < 24; i++) {
//                JSONObject hour = currentObservation.getJSONObject(i);
//                JSONObject fctime = hour.getJSONObject("FCTTIME");
//                JSONObject temp = hour.getJSONObject("temp");
//                String icon = hour.getString("icon");
//
//                WeatherHour weatherHour = new WeatherHour(fctime.getString("hour") + ":00", hour.getString("pop") + "%", temp.getInt("metric"), icon);
//                weatherHours.add(weatherHour);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        adapter.notifyDataSetChanged();
    }

    private void updateDatabase(JSONObject response,boolean isInsert, int idCity) {
        ArrayList<WeatherHour> arrHour = ProcessJson.getAllWeatherHours(response);
        for (int i = 0; i < arrHour.size(); i++) {
            if (isInsert) {
                databaseHelper.insertWeatherHour(arrHour.get(i), idCity, i);
            } else {
                databaseHelper.updateWeatherHour(arrHour.get(i), idCity, i);
            }
        }

//        try {
//            JSONArray currentObservation = response.getJSONArray("hourly_forecast");
//            for (int i = 0; i < 24; i++) {
//                JSONObject hour = currentObservation.getJSONObject(i);
//                JSONObject fctime = hour.getJSONObject("FCTTIME");
//                JSONObject temp = hour.getJSONObject("temp");
//                String icon = hour.getString("icon");
//
//                WeatherHour weatherHour = new WeatherHour(fctime.getString("hour") + ":00", hour.getString("pop") + "%", temp.getInt("metric"), icon);
//                if (isInsert) {
//                    databaseHelper.insertWeatherHour(weatherHour, idCity, i);
//                } else {
//                    databaseHelper.updateWeatherHour(weatherHour, idCity, i);
//                }
//
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            ((MainActivity) getActivity()).getTvLocation().setText(SharedPreUtils.getString(DatabaseConstant.NAME, ""));
            ((MainActivity)getActivity()).getTvTime().setText("24 giờ tiếp theo");
        }
    }

    public void updateData(String s, int idCity, boolean isInsert) {
        try {
            JSONObject object = new JSONObject(s);
            displayData(object);
            if (isInsert) {
                updateDatabase(object,true, idCity);
            } else {
                updateDatabase(object,false, idCity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void chooseItem(int idCity) {
        weatherHours.clear();
        weatherHours.addAll(databaseHelper.getAllWeatherHours(idCity));
        adapter.notifyDataSetChanged();
    }
}
