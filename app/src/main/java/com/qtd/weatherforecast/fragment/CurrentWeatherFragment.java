package com.qtd.weatherforecast.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.activity.MainActivity;
import com.qtd.weatherforecast.constant.DatabaseConstant;
import com.qtd.weatherforecast.database.MyDatabaseHelper;
import com.qtd.weatherforecast.database.ProcessJson;
import com.qtd.weatherforecast.model.CurrentWeather;
import com.qtd.weatherforecast.utils.ImageUtils;
import com.qtd.weatherforecast.utils.SharedPreUtils;
import com.qtd.weatherforecast.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

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
            tvTemp.setText(currentWeather.getTemp() + "°");
            tvHumid.setText(currentWeather.getHumidity());
            tvWeather.setText(currentWeather.getWeather());
            tvWind.setText(String.valueOf(currentWeather.getWind()) + " km/h");
            tvUV.setText(String.valueOf(currentWeather.getUV()));
            tvFeel.setText(String.valueOf(currentWeather.getFeelslike()) + "°");
            time = StringUtils.getCurrentDateTime(currentWeather.getTime());
            ((MainActivity) getActivity()).getTvTime().setText(time);
            ((MainActivity) getActivity()).getTvTime().setVisibility(View.VISIBLE);
            tvUpdate.setText("Cập nhật " + StringUtils.getTimeAgo());
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
            time = StringUtils.getCurrentDateTime(currentObservation.getString("local_tz_offset"));
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
            if (((MainActivity) getActivity()).isPlus()) {
                Animation rotation1 = AnimationUtils.loadAnimation(view.getContext(), R.anim.clockwise_rotation_finite1);
                Animation fadeOut = AnimationUtils.loadAnimation(view.getContext(), R.anim.fade_out);
                AnimationSet set1 = new AnimationSet(false);
                set1.addAnimation(rotation1);
                set1.addAnimation(fadeOut);
                set1.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ((MainActivity) getActivity()).getImvRenew().setImageResource(R.drawable.ic_autorenew_white_24dp);
                        Animation rotation2 = AnimationUtils.loadAnimation(view.getContext(), R.anim.clockwise_rotation_finite2);
                        Animation fadeIn = AnimationUtils.loadAnimation(view.getContext(), R.anim.fade_in);
                        AnimationSet set2 = new AnimationSet(false);
                        set2.addAnimation(fadeIn);
                        set2.addAnimation(rotation2);
                        ((MainActivity) getActivity()).getImvRenew().startAnimation(set2);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                ((MainActivity) getActivity()).getImvRenew().startAnimation(set1);

                Animation animationUp = AnimationUtils.loadAnimation(view.getContext(), R.anim.translate_up);
                animationUp.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ((MainActivity) getActivity()).getTvTime().setText(time);
                        ((MainActivity) getActivity()).getTvTime().setVisibility(View.VISIBLE);
                        ((MainActivity) getActivity()).getTv1().setVisibility(View.INVISIBLE);
                        ((MainActivity) getActivity()).getTvLocation().setVisibility(View.VISIBLE);
                        ((MainActivity) getActivity()).getTvLocation().setText(SharedPreUtils.getString(DatabaseConstant.NAME, "-1"));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                ((MainActivity) getActivity()).getTv1().startAnimation(animationUp);
            } else {
                ((MainActivity) getActivity()).getTvTime().setText(time);
                ((MainActivity) getActivity()).getTvTime().setVisibility(View.VISIBLE);
                ((MainActivity) getActivity()).getTv1().setVisibility(View.INVISIBLE);
                ((MainActivity) getActivity()).getTvLocation().setVisibility(View.VISIBLE);
                ((MainActivity) getActivity()).getTvLocation().setText(SharedPreUtils.getString(DatabaseConstant.NAME, "-1"));
            }

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
        time = StringUtils.getCurrentDateTime(currentWeather.getTime());
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
            time = StringUtils.getCurrentDateTime(currentWeather.getTime());
            tvUV.setText(String.valueOf(currentWeather.getUV()));
        }
    }

    public void updateTextViewRecent() {
        String timeAgo = StringUtils.getTimeAgo();
        tvUpdate.setText("Cập nhật " + timeAgo);
    }

    public void updateTime() {
        time = StringUtils.getCurrentDateTime(SharedPreUtils.getString(DatabaseConstant.TIME_ZONE, "+0700"));
    }
}
