package com.qtd.weatherforecast.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;

import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.activity.MainActivity;
import com.qtd.weatherforecast.adapter.CityAdapter;
import com.qtd.weatherforecast.callback.FragmentCallback;
import com.qtd.weatherforecast.constant.ApiConstant;
import com.qtd.weatherforecast.constant.AppConstant;
import com.qtd.weatherforecast.constant.DatabaseConstant;
import com.qtd.weatherforecast.database.MyDatabaseHelper;
import com.qtd.weatherforecast.model.City;
import com.qtd.weatherforecast.model.CurrentWeather;
import com.qtd.weatherforecast.utils.NotificationUtils;
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

    private CityAdapter adapter;
    private ArrayList<City> cities;
    private FragmentCallback callback;
    private MainActivity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
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
                callback = (FragmentCallback) context;
            }
            activity = (MainActivity) context;
        } catch (Exception e) {
            Log.d("Err casting searchFrg", "cast context to activity");
        }
    }

    private void initComponent() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        cities = new ArrayList<>();
        adapter = new CityAdapter(cities);
        recyclerView.setAdapter(adapter);
    }

    public void initData() {
        MyDatabaseHelper databaseHelper = MyDatabaseHelper.getInstance(getContext());

        int id = SharedPreUtils.getInt(DatabaseConstant._ID, -1);
        Log.d("id", String.valueOf(id));
        if (id != -1) {
            cities.addAll(databaseHelper.getAllCities());
            for (City city : cities) {
                if (city.getId() != id) {
                    city.setChosen(false);
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (SharedPreUtils.getBoolean(AppConstant.HAS_CITY, false) && isVisibleToUser) {
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
                    activity.getImvRenew().setImageResource(R.drawable.ic_plus_white_24dp);
                    Animation rotation2 = AnimationUtils.loadAnimation(getActivity(), R.anim.clockwise_rotation_finite2);
                    Animation fadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
                    AnimationSet set2 = new AnimationSet(false);
                    set2.addAnimation(fadeIn);
                    set2.addAnimation(rotation2);
                    activity.getImvRenew().startAnimation(set2);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            activity.getImvRenew().startAnimation(set1);

            Animation animationDown = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_down);
            animationDown.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    activity.getTv1().setVisibility(View.VISIBLE);
                    activity.getTvLocation().setVisibility(View.INVISIBLE);
                    activity.getTvTime().setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            activity.getLayoutLocation().startAnimation(animationDown);

            activity.setPlus(true);
        }
    }

    public int updateDataAndGetID(String s, boolean isInsert) {
        MyDatabaseHelper databaseHelper = MyDatabaseHelper.getInstance(getContext());
        long id = SharedPreUtils.getInt(DatabaseConstant._ID, -1);
        try {
            JSONObject object = new JSONObject(s);
            JSONObject currentObservation = object.getJSONObject(ApiConstant.CURRENT_OBSERVATION);
            String timeUpdate = currentObservation.getString(ApiConstant.LOCAL_TZ_OFFSET);
            JSONObject displayLocation = currentObservation.getJSONObject(ApiConstant.DISPLAY_LOCATION);
            String name = displayLocation.getString(ApiConstant.CITY);
            String fullName = displayLocation.getString(ApiConstant.FULL);
            int tempc = currentObservation.getInt(ApiConstant.TEMP_C);
            String weather = currentObservation.getString(ApiConstant.WEATHER);
            String coordinate = displayLocation.getString(ApiConstant.LATITUDE) + "," + displayLocation.getString(ApiConstant.LONGITUDE);

            if (isInsert) {
                City city = new City(0, name, tempc, weather, coordinate, true, fullName);
                id = databaseHelper.insertCity(city);
                SharedPreUtils.putBoolean(AppConstant.HAS_CITY, true);
                SharedPreUtils.putData((int) id, name, coordinate, timeUpdate);
                city.setId((int) id);
                cities.add(city);
                setCheckedCities((int) id);
            } else {
                for (City city : cities) {
                    if (city.getId() == id) {
                        city.setTemp(tempc);
                        city.setWeather(weather);
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
        for (City city : cities) {
            if (city.getId() != idChecked) {
                city.setChosen(false);
            } else {
                city.setChosen(true);
            }
        }
    }

    public void deleteItem(int idCity) {
        MyDatabaseHelper databaseHelper = MyDatabaseHelper.getInstance(getContext());

        for (City city : cities) {
            if (city.getId() == idCity) {
                cities.remove(city);
                break;
            }
        }

        databaseHelper.deleteCity(idCity);
        int idChosen = SharedPreUtils.getInt(DatabaseConstant._ID, -1);
        if (idChosen == idCity) {
            City city = databaseHelper.getFirstCity();
            CurrentWeather currentWeather = databaseHelper.getCurrentWeather(city.getId());
            setCheckedCities(city.getId());
            SharedPreUtils.putData(city.getId(), city.getName(), city.getCoordinate(), currentWeather.getTime());
            if (city.getId() == -1) {
                NotificationUtils.clearNotification(getContext());
            }
            callback.checkCitySizeToEnableViewPagerSwipe(city.getId());
            activity.getDataFromDatabase();
        }
        adapter.notifyDataSetChanged();
        NotificationUtils.updateNotification(getContext());
    }

    public void chooseItem(int idCity) {
        setCheckedCities(idCity);
        adapter.notifyDataSetChanged();
        NotificationUtils.updateNotification(getContext());
    }

    public void getDataFromDatabase() {
        MyDatabaseHelper databaseHelper = MyDatabaseHelper.getInstance(getContext());
        int id = SharedPreUtils.getInt(DatabaseConstant._ID, -1);
        if (id != -1) {
            cities.clear();
            adapter.notifyDataSetChanged();

            cities.addAll(databaseHelper.getAllCities());

            for (City city : cities) {
                if (city.getId() != id) {
                    city.setChosen(false);
                }
            }
            adapter.notifyDataSetChanged();
        }
    }
}