package com.qtd.weatherforecast.fragment;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.RemoteViews;

import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.activity.MainActivity;
import com.qtd.weatherforecast.adapter.CityAdapter;
import com.qtd.weatherforecast.database.MyDatabaseHelper;
import com.qtd.weatherforecast.model.City;
import com.qtd.weatherforecast.model.CityPlus;
import com.qtd.weatherforecast.model.CurrentWeather;
import com.qtd.weatherforecast.service.WeatherForecastService;
import com.qtd.weatherforecast.utils.ImageUtils;
import com.qtd.weatherforecast.utils.SharedPreUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Dell on 4/25/2016.
 */
public class SearchFragment extends Fragment {
    @Bind(R.id.recycleView_search_fragment)
    RecyclerView recyclerView;

    CityAdapter adapter;
    ArrayList<City> cities;
    View view;
    UpdateChoosingCityCallback callback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);
        initComponent();
        initData();
        return view;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            if (context instanceof MainActivity) {
                callback = (UpdateChoosingCityCallback) context;
            }
        } catch (Exception e) {
            Log.d("Err casting searchFrg", "cast context to activity");
        }
    }

    private void initComponent() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        cities = new ArrayList<>();
        adapter = new CityAdapter(cities);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    public void initData() {
        MyDatabaseHelper databaseHelper = MyDatabaseHelper.getInstance(view.getContext());
        int id = SharedPreUtils.getInt("ID", -1);
        Log.d("id", String.valueOf(id));
        if (id != -1) {
            cities.addAll(databaseHelper.getAllCities());
            for (int i = 0; i < cities.size(); i++) {
                if (cities.get(i).getId() != id) {
                    cities.get(i).setChosen(false);
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

            Animation rotation1 = AnimationUtils.loadAnimation(getActivity(), R.anim.clockwise_rotation_finite1);
            Animation fadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
            AnimationSet set1 = new AnimationSet(false);
            set1.addAnimation(rotation1);
            set1.addAnimation(fadeOut);
            set1.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ((MainActivity) getActivity()).getImvRenew().setImageResource(R.drawable.ic_plus_white_24dp);
                    Animation rotation2 = AnimationUtils.loadAnimation(getActivity(), R.anim.clockwise_rotation_finite2);
                    Animation fadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
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

            Animation animationDown = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_down);
            animationDown.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ((MainActivity) getActivity()).getTv1().setVisibility(View.VISIBLE);
                    ((MainActivity) getActivity()).getTvLocation().setVisibility(View.INVISIBLE);
                    ((MainActivity) getActivity()).getTvTime().setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            ((MainActivity) getActivity()).getLayoutLocation().startAnimation(animationDown);

            ((MainActivity) getActivity()).setPlus(true);


        }
    }

    public int updateDataAndGetID(String s, boolean isInsert) {
        MyDatabaseHelper databaseHelper = MyDatabaseHelper.getInstance(view.getContext());
        long id = SharedPreUtils.getInt("ID", -1);
        try {
            JSONObject object = new JSONObject(s);
            JSONObject currentObservation = object.getJSONObject("current_observation");
            String timeUpdate = currentObservation.getString("local_tz_offset");
            JSONObject displayLocation = currentObservation.getJSONObject("display_location");
            String name = displayLocation.getString("city");
            String fullName = displayLocation.getString("full");
            int tempc = currentObservation.getInt("temp_c");
            String weather = currentObservation.getString("weather");
            String coordinate = displayLocation.getString("latitude") + "," + displayLocation.getString("longitude");

            if (isInsert) {
                City city = new City(0, name, tempc, weather, coordinate, true, fullName);
                id = databaseHelper.insertCity(city);
                SharedPreUtils.putData((int) id, name, coordinate, timeUpdate);
                city.setId((int) id);
                cities.add(city);
                setCheckedCities((int) id);
            } else {
                for (int i = 0; i < cities.size(); i++) {
                    if (cities.get(i).getId() == id) {
                        cities.get(i).setTemp(tempc);
                        cities.get(i).setWeather(weather);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
        return (int) id;
    }

    private void setCheckedCities(int idChecked) {
        for (int i = 0; i < cities.size(); i++) {
            if (cities.get(i).getId() != idChecked) {
                cities.get(i).setChosen(false);
            } else {
                cities.get(i).setChosen(true);
            }
        }
    }

    public void deleteItem(int idCity) {
        MyDatabaseHelper databaseHelper = MyDatabaseHelper.getInstance(view.getContext());
        for (int i = cities.size() - 1; i >= 0; i--) {
            if (cities.get(i).getId() == idCity) {
                cities.remove(i);
                break;
            }
        }
        databaseHelper.deleteCity(idCity);
        int idChosen = SharedPreUtils.getInt("ID", -1);
        if (idChosen == idCity) {
            City city = databaseHelper.getFirstCity();
            CurrentWeather currentWeather = databaseHelper.getCurrentWeather(city.getId());
            setCheckedCities(city.getId());
            SharedPreUtils.putData(city.getId(), city.getName(), city.getCoordinate(), currentWeather.getTime());
            if (city.getId() == -1) {
                NotificationManager notificationManager = (NotificationManager) view.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(WeatherForecastService.NOTIFICATION_ID);
            }
            callback.checkCitySizeToEnableViewPagerSwipe(city.getId());
        }
        adapter.notifyDataSetChanged();
        updateNoti();
    }

    public void chooseItem(int idCity) {
        setCheckedCities(idCity);
        adapter.notifyDataSetChanged();
        updateNoti();
    }


    public void updateNoti() {
        MyDatabaseHelper databaseHelper = MyDatabaseHelper.getInstance(view.getContext());
        int id = SharedPreUtils.getInt("ID", -1);
        if (id != -1) {
            CityPlus cityPlus = databaseHelper.getCityByID(id);
            RemoteViews remoteViews = new RemoteViews(view.getContext().getPackageName(), R.layout.notification);
            remoteViews.setImageViewResource(R.id.imv_icon, ImageUtils.getImageResourceNotification(cityPlus.getIcon()));
            remoteViews.setTextViewText(R.id.tv_weather, cityPlus.getWeather());
            remoteViews.setTextViewText(R.id.tv_temp, String.valueOf(cityPlus.getTemp()) + "°");
            remoteViews.setTextViewText(R.id.tv_location, cityPlus.getFullName());
            Intent intent = new Intent(view.getContext(), MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(view.getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(view.getContext())
                    .setSmallIcon(ImageUtils.getImageResourceCurrentWeather(cityPlus.getIcon()))
                    .setContent(remoteViews)
                    .setOngoing(true)
                    .setContentIntent(pendingIntent);
            NotificationManager notificationManager = (NotificationManager) view.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(WeatherForecastService.NOTIFICATION_ID, notiBuilder.build());
        }
    }

    public void getDataFromDatabase() {
        MyDatabaseHelper databaseHelper = MyDatabaseHelper.getInstance(view.getContext());
        int id = SharedPreUtils.getInt("ID", -1);
        if (id != -1) {
            cities.clear();
            adapter.notifyDataSetChanged();

            cities.addAll(databaseHelper.getAllCities());
            for (int i = 0; i < cities.size(); i++) {
                if (cities.get(i).getId() != id) {
                    cities.get(i).setChosen(false);
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    public interface UpdateChoosingCityCallback {
        void checkCitySizeToEnableViewPagerSwipe(int idCity);
    }
}
