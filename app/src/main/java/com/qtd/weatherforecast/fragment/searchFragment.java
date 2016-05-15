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

import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.activity.MainActivity;
import com.qtd.weatherforecast.adapter.CityAdapter;
import com.qtd.weatherforecast.constant.ApiConstant;
import com.qtd.weatherforecast.constant.DatabaseConstant;
import com.qtd.weatherforecast.database.MyDatabaseHelper;
import com.qtd.weatherforecast.model.City;
import com.qtd.weatherforecast.utils.SharedPreUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Dell on 4/25/2016.
 */
public class SearchFragment extends Fragment{
    @Bind(R.id.recycleView_search_fragment)
    RecyclerView recyclerView;

    CityAdapter adapter;
    ArrayList<City> cities;
    LinearLayoutManager layoutManager;
    MyDatabaseHelper databaseHelper;
    View view;
    UpdateChosingCityCallback callback;

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
                callback = (UpdateChosingCityCallback) context;
            }
        } catch (Exception e) {
            Log.d("Err casting searchFrg", "cast context to activity");
        }
    }

    private void initComponent() {
        layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        cities = new ArrayList<>();
        adapter = new CityAdapter(cities);
        recyclerView.setAdapter(adapter);
    }

    public void initData() {
        databaseHelper = MyDatabaseHelper.getInstance(view.getContext());
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
            ((MainActivity)getActivity()).getImvRenew().setImageResource(R.drawable.ic_plus_white_24dp);
            ((MainActivity) getActivity()).setPlus(true);
            ((MainActivity) getActivity()).getTv1().setVisibility(View.VISIBLE);
            ((MainActivity) getActivity()).getTvLocation().setVisibility(View.INVISIBLE);
            ((MainActivity) getActivity()).getTvTime().setVisibility(View.INVISIBLE);

        }
    }

    public int updateDataAndGetID(String s, boolean isInsert) {
        long id = SharedPreUtils.getInt("ID", -1);
        try {
            JSONObject object = new JSONObject(s);
            JSONObject currentObservation = object.getJSONObject("current_observation");
            JSONObject displayLocation = currentObservation.getJSONObject("display_location");
            String name = displayLocation.getString("city");
            String fullName = displayLocation.getString("full");
            int tempc = currentObservation.getInt("temp_c");
            String weather = currentObservation.getString("weather");
            String coordinate = displayLocation.getString("latitude") + "," + displayLocation.getString("longitude");

            if (isInsert) {
                City city = new City(0, name, tempc, weather, coordinate, true, fullName);
                id = databaseHelper.insertCity(city);
                SharedPreUtils.putInt("ID", (int)id);
                SharedPreUtils.putString(DatabaseConstant.NAME, name);
                SharedPreUtils.putString(ApiConstant.COORDINATE, coordinate);
                city.setId((int) id);
                cities.add(city);
                setCheckedCities((int)id);
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
        for (int i = cities.size()-1; i >= 0; i--) {
            if (cities.get(i).getId() == idCity) {
                cities.remove(i);
                break;
            }
        }
        databaseHelper.deleteCity(idCity);
        int idChoosen = SharedPreUtils.getInt("ID", -1);
        if (idChoosen == idCity) {
            City city = databaseHelper.getFirstCity();
            setCheckedCities(city.getId());
            SharedPreUtils.putInt("ID", city.getId());
            SharedPreUtils.putString(DatabaseConstant.NAME, city.getName());
            SharedPreUtils.putString(ApiConstant.COORDINATE, city.getCoordinate());
            callback.checkCitySizeToEnableViewPagerSwipe(city.getId());
        }
        adapter.notifyDataSetChanged();
    }

    public void chooseItem(int idCity) {
        setCheckedCities(idCity);
        adapter.notifyDataSetChanged();
    }

    public void getDataFromDatabase() {
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
    public interface UpdateChosingCityCallback {
        void checkCitySizeToEnableViewPagerSwipe(int idCity);
    }
}
