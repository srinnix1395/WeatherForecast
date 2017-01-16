package com.qtd.weatherforecast.fragment;

import android.content.Context;
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
import com.qtd.weatherforecast.constant.AppConstant;
import com.qtd.weatherforecast.constant.DatabaseConstant;
import com.qtd.weatherforecast.database.MyDatabaseHelper;
import com.qtd.weatherforecast.database.ProcessJson;
import com.qtd.weatherforecast.model.CurrentWeather;
import com.qtd.weatherforecast.utils.SharedPreUtils;
import com.qtd.weatherforecast.utils.StringUtils;
import com.qtd.weatherforecast.utils.UiHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.qtd.weatherforecast.constant.AppConstant.HAS_CITY;

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
	
	@Bind(R.id.layout_UV)
	RelativeLayout layoutUV;
	
	@Bind(R.id.layout_humid)
	RelativeLayout layoutHumid;
	
	private String time = "";
	private MainActivity activity;
	private MyDatabaseHelper databaseHelper;
	private AnimationSet animationHide;
	private Animation animationUp;
	
	public static CurrentWeatherFragment newInstance() {
		return new CurrentWeatherFragment();
	}
	
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_current_weather, container, false);
		ButterKnife.bind(this, view);
		initComponent();
		return view;
	}
	
	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		activity = (MainActivity) context;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (SharedPreUtils.getBoolean(HAS_CITY, false)) {
			activity.tvTime.setText(time);
			activity.tvTime.setVisibility(View.VISIBLE);
			activity.tv1.setVisibility(View.INVISIBLE);
			activity.tvLocation.setVisibility(View.VISIBLE);
			activity.tvLocation.setText(SharedPreUtils.getString(DatabaseConstant.NAME, "-1"));
		}
	}
	
	private void initComponent() {
		databaseHelper = MyDatabaseHelper.getInstance(getContext());
		final int id = SharedPreUtils.getInt(DatabaseConstant._ID, -1);
		
		if (SharedPreUtils.getBoolean(HAS_CITY, false)) {
			CurrentWeather currentWeather = databaseHelper.getCurrentWeather(id);
			
			displayData(currentWeather);
			
			activity.tvTime.setText(time);
			activity.tvTime.setVisibility(View.VISIBLE);
		}
		
		initAnimation();
	}
	
	private void initAnimation() {
		Animation rotation2 = AnimationUtils.loadAnimation(getContext(), R.anim.clockwise_rotation_finite2);
		Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
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
				Picasso.with(activity)
						.load(R.drawable.ic_refresh)
						.into(activity.imvUpdate);
				activity.imvUpdate.startAnimation(animationShow);
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
		});
		
		animationUp = AnimationUtils.loadAnimation(getContext(), R.anim.translate_up);
		animationUp.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				activity.tvTime.setText(time);
				activity.tvTime.setVisibility(View.VISIBLE);
				activity.tv1.setVisibility(View.INVISIBLE);
				activity.tvLocation.setVisibility(View.VISIBLE);
				activity.tvLocation.setText(SharedPreUtils.getString(DatabaseConstant.NAME, "-1"));
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
		});
	}
	
	public void updateData(String s, int idCity, boolean isInsert) {
		try {
			CurrentWeather currentWeather = ProcessJson.getCurrentWeather(s);
			displayData(currentWeather);
			SharedPreUtils.putLong(DatabaseConstant.LAST_UPDATE, System.currentTimeMillis());
			updateDatabase(currentWeather, isInsert, idCity);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void displayData(CurrentWeather weather) {
		imvIcon.setImageResource(UiHelper.getImageResourceCurrentWeather(weather.getIcon()));
		tvTemp.setText(String.format("%s°", StringUtils.getTemp(weather.getTemp())));
		tvHumid.setText(weather.getHumidity());
		tvWeather.setText(weather.getWeather());
		tvWind.setText(String.format("%s km/h", String.valueOf(weather.getWind())));
		tvUV.setText(String.valueOf(weather.getUV()));
		tvFeel.setText(String.format("%s°", StringUtils.getTemp(weather.getFeelsLike())));
		time = StringUtils.getCurrentDateTime(weather.getTime());
		updateTextViewRecent();
	}
	
	private void updateDatabase(CurrentWeather currentWeather, boolean isInsert, int idCity) {
		if (isInsert) {
			databaseHelper.insertCurrentWeather(currentWeather, idCity);
		} else {
			databaseHelper.updateCurrentWeather(currentWeather, idCity);
		}
	}
	
	public void getDataFromDatabase() {
		int id = SharedPreUtils.getInt(DatabaseConstant._ID, -1);
		if (id != -1) {
			CurrentWeather currentWeather = databaseHelper.getCurrentWeather(id);
			
			displayData(currentWeather);
		}
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (activity != null && isVisibleToUser) {
			changeIcon();
		}
	}
	
	private void changeIcon() {
		if (activity.isPlus()) {
			activity.imvUpdate.startAnimation(animationHide);
			activity.tv1.startAnimation(animationUp);
		} else {
			activity.tvTime.setText(time);
			activity.tvTime.setVisibility(View.VISIBLE);
			activity.tv1.setVisibility(View.INVISIBLE);
			activity.tvLocation.setVisibility(View.VISIBLE);
			activity.tvLocation.setText(SharedPreUtils.getString(DatabaseConstant.NAME, "-1"));
		}
		activity.setPlus(false);
	}
	
	public void chooseItem(int idCity) {
		CurrentWeather currentWeather = databaseHelper.getCurrentWeather(idCity);
		
		displayData(currentWeather);
		SharedPreUtils.putLong(DatabaseConstant.LAST_UPDATE, currentWeather.getLastUpdate());
	}
	
	@OnClick({R.id.layout_humid, R.id.layout_UV})
	void layoutInfoOnClick(View v) {
		switch (v.getId()) {
			case R.id.layout_humid: {
				if (layoutHumid.getVisibility() == View.VISIBLE) {
					layoutHumid.setVisibility(View.INVISIBLE);
					layoutUV.setVisibility(View.VISIBLE);
				}
				break;
			}
			case R.id.layout_UV: {
				if (layoutUV.getVisibility() == View.VISIBLE) {
					layoutUV.setVisibility(View.INVISIBLE);
					layoutHumid.setVisibility(View.VISIBLE);
				}
				break;
			}
		}
	}
	
	public void updateTextViewRecent() {
		String timeAgo = StringUtils.getTimeAgo();
		tvUpdate.setText(String.format("Cập nhật %s", timeAgo));
	}
	
	public void updateTime() {
		time = StringUtils.getCurrentDateTime(SharedPreUtils.getString(DatabaseConstant.TIME_ZONE, "+0700"));
	}
	
	public void updateDegree() {
		int id = SharedPreUtils.getInt(AppConstant._ID, -1);
		if (id != -1) {
			int[] currentTemp = MyDatabaseHelper.getInstance(getContext()).getCurrentTemp(id);
			
			tvTemp.setText(StringUtils.getTemp(currentTemp[0]));
			tvFeel.setText(StringUtils.getTemp(currentTemp[1]));
		}
	}
	
	public void updating() {
		tvUpdate.setText(R.string.updating);
	}
	
	public void endUpdate() {
		if (tvUpdate.getText().equals(getString(R.string.updating))) {
			updateTextViewRecent();
		}
	}
}
