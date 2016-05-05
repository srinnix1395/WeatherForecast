package com.qtd.weatherforecast.fragment;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qtd.weatherforecast.AppController;
import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.activity.MainActivity;
import com.qtd.weatherforecast.constant.ApiConstant;
import com.qtd.weatherforecast.constant.DatabaseConstant;
import com.qtd.weatherforecast.database.MyDatabaseHelper;
import com.qtd.weatherforecast.model.CurrentWeather;
import com.qtd.weatherforecast.utility.ImageUtils;
import com.qtd.weatherforecast.utility.NetworkUtil;
import com.qtd.weatherforecast.utility.SharedPreUtils;
import com.qtd.weatherforecast.utility.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Dell on 4/25/2016.
 */
public class CurrentWeatherFragment extends Fragment {
    @Bind(R.id.tv_temp)
    TextView tvTemp;

    @Bind(R.id.tv_weather)
    TextView tvWeather;

    @Bind(R.id.tv_humid)
    TextView tvHumid;

    @Bind(R.id.tv_wind)
    TextView tvWind;

    @Bind(R.id.imv_icon)
    ImageView imvIcon;

    @Bind(R.id.tv_update)
    TextView tvUpdate;

    @Bind(R.id.tv_uv)
    TextView tvUV;

    View view;
    String time = "";

    MyDatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_current_weather, container, false);
        ButterKnife.bind(this, view);
        initComponent();
        return view;
    }

    private void initComponent() {
        databaseHelper = MyDatabaseHelper.getInstance(view.getContext());
        final int id = SharedPreUtils.getInt("ID", -1);

        if (id != -1) {
            if (NetworkUtil.getInstance().isNetworkAvailable(view.getContext())) {
                String url = StringUtils.getURL("conditions", SharedPreUtils.getString(ApiConstant.COORDINATE, "-1"));
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
                tvUpdate.setText("Cập nhật lần cuối: vừa xong");
            } else {
                CurrentWeather currentWeather = databaseHelper.getCurrentWeather(id);
//                imvIcon.setImageResource(ImageUtils.getImageResource(currentWeather.getIcon()));
                imvIcon.setImageResource(R.drawable.sun_500);
                tvTemp.setText(currentWeather.getTemp() + "°");
                tvHumid.setText(currentWeather.getHumidity());
                tvWeather.setText(currentWeather.getWeather());
                tvWind.setText(String.valueOf(currentWeather.getWind()) + " km/h");
//                tvUV.setText(String.valueOf(currentWeather.getUV()));
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void displayData(JSONObject s) {
        try {
            JSONObject currentObservation = s.getJSONObject("current_observation");
            String icon = currentObservation.getString("icon");

            imvIcon.setImageResource(ImageUtils.getImageResource(icon));
            tvTemp.setText(currentObservation.getString("temp_c") + "°");
            tvHumid.setText(currentObservation.getString("relative_humidity"));
            tvWeather.setText(currentObservation.getString("weather"));
            tvWind.setText(String.valueOf(currentObservation.getString("wind_gust_kph")) + " km/h");
            String day = currentObservation.getString("observation_time_rfc822");
//            tvUV.setText(currentObservation.getInt("UV"));
            //time = StringUtils.getWeekday(day.substring(0, 3)) + ", " + day.substring(17, 22);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateDatabase(JSONObject response, boolean isInsert, int idCity) {
        try {
            JSONObject currentObservation = response.getJSONObject("current_observation");
            String day = currentObservation.getString("observation_time_rfc822");
            String timeUpdate = StringUtils.getWeekday(day.substring(0, 3)) + ", " + day.substring(17, 22);
            int wind = currentObservation.getInt("wind_gust_kph");
            String humid = currentObservation.getString("relative_humidity");
            String weather = currentObservation.getString("weather");
            int tempc = currentObservation.getInt("temp_c");
            int uv = currentObservation.getInt("UV");
            int feelslike = currentObservation.getInt("feelslike_c");
            String icon = currentObservation.getString("icon");

            CurrentWeather currentWeather = new CurrentWeather(icon, tempc, weather, humid, wind, uv, feelslike, timeUpdate);

            if (isInsert) {
                databaseHelper.insertCurrentWeather(currentWeather, idCity);
            } else {
                databaseHelper.updateCurrentWeather(currentWeather, idCity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            ((MainActivity) getActivity()).getTvTime().setText(time);
            ((MainActivity) getActivity()).getTvLocation().setText(SharedPreUtils.getString(DatabaseConstant.NAME, ""));
            ((MainActivity) getActivity()).getImvRenew().setImageResource(R.drawable.ic_autorenew_white_24dp);
            ((MainActivity) getActivity()).setPlus(false);
        }
    }

    public void updateData(String s, int idCity, boolean isInsert) {
        try {
            JSONObject object = new JSONObject(s);
            displayData(object);
            if (isInsert) {
                updateDatabase(object, true, idCity);
            } else {
                updateDatabase(object, false, idCity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void chooseItem(int idCity) {
        CurrentWeather currentWeather = databaseHelper.getCurrentWeather(idCity);
        imvIcon.setImageResource(ImageUtils.getImageResource(currentWeather.getIcon()));
        tvTemp.setText(String.valueOf(currentWeather.getTemp()) + "°");
        tvHumid.setText(currentWeather.getHumidity());
        tvWeather.setText(currentWeather.getWeather());
        tvWind.setText(String.valueOf(currentWeather.getWind()) + " km/h");
        String day = StringUtils.getWeekday(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
        String hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" + Calendar.getInstance().get(Calendar.MINUTE);
        time = day + "," + hour;
//        tvUV.setText(currentWeather.getUV());
    }
}
