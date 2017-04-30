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
import com.qtd.weatherforecast.constant.AppConstant;
import com.qtd.weatherforecast.constant.DatabaseConstant;
import com.qtd.weatherforecast.database.MyDatabaseHelper;
import com.qtd.weatherforecast.model.City;
import com.qtd.weatherforecast.model.CurrentWeather;
import com.qtd.weatherforecast.utils.NotificationUtils;
import com.qtd.weatherforecast.utils.SharedPreUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Dell on 4/25/2016.
 */
public class LocationFragment extends Fragment {
	@Bind(R.id.recycleView_search_fragment)
	RecyclerView recyclerView;
	
	private CityAdapter adapter;
	private ArrayList<City> cities;
	private FragmentCallback callback;
	private MainActivity activity;
	
	private AnimationSet animationHide;
	private Animation animationDown;
	
	public static LocationFragment newInstance() {
		return new LocationFragment();
	}
	
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_location, container, false);
		ButterKnife.bind(this, view);
		initComponent();
		getDataFromDatabase();
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
		} catch (ClassCastException e) {
			Log.d("Err casting searchFrg", "cast context to activity");
		}
	}
	
	private void initComponent() {
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		recyclerView.setHasFixedSize(true);
		
		cities = new ArrayList<>();
		adapter = new CityAdapter(cities);
		recyclerView.setAdapter(adapter);
		
		initAnimation();
	}
	
	private void initAnimation() {
		Animation rotation2 = AnimationUtils.loadAnimation(getActivity(), R.anim.clockwise_rotation_finite2);
		Animation fadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
		final AnimationSet animationShow = new AnimationSet(false);
		animationShow.addAnimation(fadeIn);
		animationShow.addAnimation(rotation2);
		
		Animation rotation1 = AnimationUtils.loadAnimation(getContext(), R.anim.clockwise_rotation_finite1);
		Animation fadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
		animationHide = new AnimationSet(false);
		animationHide.addAnimation(rotation1);
		animationHide.addAnimation(fadeOut);
		animationHide.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				activity.imvUpdate.setImageResource(R.drawable.ic_plus);
				activity.imvUpdate.startAnimation(animationShow);
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
		});
		
		animationDown = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_down);
		animationDown.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				activity.tv1.setVisibility(View.VISIBLE);
				activity.tvLocation.setVisibility(View.INVISIBLE);
				activity.tvTime.setVisibility(View.INVISIBLE);
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
		});
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (getContext() == null) {
			return;
		}
		if (SharedPreUtils.getBoolean(AppConstant.HAS_CITY, false) && isVisibleToUser) {
			if (activity.imvUpdate != null) {
				activity.imvUpdate.startAnimation(animationHide);
			}
            activity.layoutLocation.startAnimation(animationDown);

            activity.setPlus(true);
		}
	}
	
	public void insertCity(City city) {
		SharedPreUtils.putBoolean(AppConstant.HAS_CITY, true);
		SharedPreUtils.putData(city.getId(), city.getName(), city.getCoordinate(), city.getTimeZone());
		cities.add(city);
		setCheckedCities(city.getId());
		adapter.notifyDataSetChanged();
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
		NotificationUtils.createOrUpdateNotification(getContext());
	}
	
	public void chooseItem(int idCity) {
		setCheckedCities(idCity);
		adapter.notifyDataSetChanged();
		NotificationUtils.createOrUpdateNotification(getContext());
	}
	
	public void getDataFromDatabase() {
		MyDatabaseHelper databaseHelper = MyDatabaseHelper.getInstance(getContext());
		int id = SharedPreUtils.getInt(DatabaseConstant._ID, -1);
		if (id != -1) {
			cities.clear();
			cities.addAll(databaseHelper.getAllCities());
			
			for (City city : cities) {
				if (city.getId() != id) {
					city.setChosen(false);
				}
			}
			adapter.notifyDataSetChanged();
		}
	}
	
	public void updateDegree() {
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}
}
