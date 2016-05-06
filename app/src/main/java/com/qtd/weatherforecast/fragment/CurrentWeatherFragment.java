package com.qtd.weatherforecast.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.activity.MainActivity;
import com.qtd.weatherforecast.constant.DatabaseConstant;
import com.qtd.weatherforecast.database.MyDatabaseHelper;
import com.qtd.weatherforecast.database.ProcessJson;
import com.qtd.weatherforecast.model.CurrentWeather;
import com.qtd.weatherforecast.utility.ImageUtils;
import com.qtd.weatherforecast.utility.SharedPreUtils;
import com.qtd.weatherforecast.utility.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    @Bind(R.id.tv_feelslike)
    TextView tvFeel;

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
            CurrentWeather currentWeather = databaseHelper.getCurrentWeather(id);
            imvIcon.setImageResource(ImageUtils.getImageResourceCurrentWeather(currentWeather.getIcon()));
            imvIcon.setImageResource(R.drawable.sun_500);
            tvTemp.setText(currentWeather.getTemp() + "°");
            tvHumid.setText(currentWeather.getHumidity());
            tvWeather.setText(currentWeather.getWeather());
            tvWind.setText(String.valueOf(currentWeather.getWind()) + " km/h");
            tvUV.setText(String.valueOf(currentWeather.getUV()));
            tvFeel.setText(String.valueOf(currentWeather.getFeelslike()) + "°");
        }
    }


    private void displayData(JSONObject s) {
        try {
            JSONObject currentObservation = s.getJSONObject("current_observation");
            String icon = currentObservation.getString("icon_url");
            imvIcon.setImageResource(ImageUtils.getImageResourceCurrentWeather(icon));
            tvTemp.setText(String.valueOf(currentObservation.getInt("temp_c")) + "°");
            tvHumid.setText(currentObservation.getString("relative_humidity"));
            tvWeather.setText(currentObservation.getString("weather"));
            tvWind.setText(String.valueOf(currentObservation.getString("wind_gust_kph")) + " km/h");
            String day = currentObservation.getString("observation_time_rfc822");
            tvUV.setText(String.valueOf(currentObservation.getInt("UV")));
            tvFeel.setText(String.valueOf(currentObservation.getInt("feelslike_c")) + "°");
            //time = StringUtils.getWeekday(day.substring(0, 3)) + ", " + day.substring(17, 22);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateDatabase(JSONObject response, boolean isInsert, int idCity) {
        CurrentWeather currentWeather = ProcessJson.getCurrentWeather(response);
        if (isInsert) {
            databaseHelper.insertCurrentWeather(currentWeather, idCity);
        } else {
            databaseHelper.updateCurrentWeather(currentWeather, idCity);
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
        imvIcon.setImageResource(ImageUtils.getImageResourceCurrentWeather(currentWeather.getIcon()));
        tvTemp.setText(String.valueOf(currentWeather.getTemp()) + "°");
        tvHumid.setText(currentWeather.getHumidity());
        tvWeather.setText(currentWeather.getWeather());
        tvWind.setText(String.valueOf(currentWeather.getWind()) + " km/h");
        String day = StringUtils.getWeekday(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
        String hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" + Calendar.getInstance().get(Calendar.MINUTE);
        time = day + "," + hour;
        tvUV.setText(String.valueOf(currentWeather.getUV()));
    }

    @Bind(R.id.layout_UV)
    RelativeLayout layoutUV;
    @Bind(R.id.layout_humid)
    RelativeLayout layoutHumid;

    @OnClick(R.id.layout_humid)
    void layoutHumidOnClick() {
        if (layoutHumid.getVisibility() == View.VISIBLE) {
            layoutHumid.setVisibility(View.INVISIBLE);
            layoutUV.setVisibility(View.VISIBLE);
        }
    }


    @OnClick(R.id.layout_UV)
    void layoutUVOnClick() {
        if (layoutUV.getVisibility() == View.VISIBLE) {
            layoutUV.setVisibility(View.INVISIBLE);
            layoutHumid.setVisibility(View.VISIBLE);
        }
    }

    public void getDataFromDatabase() {
        int id = SharedPreUtils.getInt("ID", -1);
        if (id != -1) {
            CurrentWeather currentWeather = databaseHelper.getCurrentWeather(id);
            imvIcon.setImageResource(ImageUtils.getImageResourceCurrentWeather(currentWeather.getIcon()));
            tvTemp.setText(String.valueOf(currentWeather.getTemp()) + "°");
            tvHumid.setText(currentWeather.getHumidity());
            tvWeather.setText(currentWeather.getWeather());
            tvWind.setText(String.valueOf(currentWeather.getWind()) + " km/h");
            String day = StringUtils.getWeekday(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
            String hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" + Calendar.getInstance().get(Calendar.MINUTE);
            time = day + "," + hour;
            tvUV.setText(String.valueOf(currentWeather.getUV()));
        }
    }
}
