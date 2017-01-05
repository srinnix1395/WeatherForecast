package com.qtd.weatherforecast.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.pageindicator.AdapterNotFoundException;
import com.example.pageindicator.IconCirclePageIndicator;
import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.adapter.MainPagerAdapter;
import com.qtd.weatherforecast.callback.FragmentCallback;
import com.qtd.weatherforecast.callback.ViewHolderCallback;
import com.qtd.weatherforecast.callback.WeatherRequestCallback;
import com.qtd.weatherforecast.constant.ApiConstant;
import com.qtd.weatherforecast.constant.AppConstant;
import com.qtd.weatherforecast.constant.DatabaseConstant;
import com.qtd.weatherforecast.custom.CustomViewPager;
import com.qtd.weatherforecast.fragment.CurrentWeatherFragment;
import com.qtd.weatherforecast.fragment.FragmentGuide;
import com.qtd.weatherforecast.fragment.SearchFragment;
import com.qtd.weatherforecast.fragment.WeatherDayFragment;
import com.qtd.weatherforecast.fragment.WeatherHourFragment;
import com.qtd.weatherforecast.request.WeatherRequest;
import com.qtd.weatherforecast.service.WeatherForecastService;
import com.qtd.weatherforecast.utils.NotificationUtils;
import com.qtd.weatherforecast.utils.ServiceUtil;
import com.qtd.weatherforecast.utils.SharedPreUtils;
import com.qtd.weatherforecast.utils.StringUtils;
import com.qtd.weatherforecast.utils.UiHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ViewHolderCallback, FragmentCallback {
	private static final String TAG = "MainActivity";
	@Bind(R.id.toolbar_home)
	Toolbar toolbar;
	
	@Bind(R.id.viewPager)
	CustomViewPager viewPager;
	
	@Bind(R.id.indicator)
	IconCirclePageIndicator indicator;
	
	@Bind(R.id.tv_location)
	public TextView tvLocation;
	
	@Bind(R.id.tv_time)
	public TextView tvTime;
	
	@Bind(R.id.tv_1)
	public TextView tv1;
	
	@Bind(R.id.layout_location)
	public RelativeLayout layoutLocation;
	
	public ImageView imvUpdate;
	
	private MainBroadcastReceiver broadcastReceiver;
	private boolean isReceiverRegistered;
	private MainPagerAdapter adapter;
	private Intent intent;
	boolean isPlus;
	
	private SearchFragment searchFragment;
	private CurrentWeatherFragment currentWeatherFragment;
	private WeatherHourFragment weatherHourFragment;
	private WeatherDayFragment weatherDayFragment;
	private Animation rotation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		initComponent();
	}
	
	private void initComponent() {
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		
		setupViewPager();
		initAnimation();
		
		broadcastReceiver = new MainBroadcastReceiver();
		
		if (intent == null) {
			intent = new Intent(MainActivity.this, WeatherForecastService.class);
			startService(intent);
		}
	}
	
	private void initAnimation() {
		rotation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.clockwise_rotation);
	}
	
	public void getDataFromDatabase() {
		searchFragment.getDataFromDatabase();
		currentWeatherFragment.getDataFromDatabase();
		weatherHourFragment.getDataFromDatabase();
		weatherDayFragment.getDataFromDatabase();
		Log.d("Update", "Ok");
	}
	
	private void setupViewPager() {
		searchFragment = SearchFragment.newInstance();
		currentWeatherFragment = CurrentWeatherFragment.newInstance();
		weatherHourFragment = WeatherHourFragment.newInstance();
		weatherDayFragment = WeatherDayFragment.newInstance();
		
		ArrayList<Fragment> fragments = new ArrayList<>();
		fragments.add(searchFragment);
		fragments.add(currentWeatherFragment);
		fragments.add(weatherHourFragment);
		fragments.add(weatherDayFragment);
		adapter = new MainPagerAdapter(getSupportFragmentManager(), fragments);
		
		viewPager.setAdapter(adapter);
		viewPager.setOffscreenPageLimit(4);
		
		boolean hasCity = SharedPreUtils.getBoolean(AppConstant.HAS_CITY, false);
		if (!hasCity) {
			viewPager.setPagingEnabled(false);
			indicator.setVisibility(View.INVISIBLE);
			try {
				indicator.setupWithViewPager(viewPager);
			} catch (AdapterNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			try {
				indicator.setupWithViewPager(viewPager, 1);
			} catch (AdapterNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem miUpdate = menu.findItem(R.id.miUpdate);
		imvUpdate = (ImageView) MenuItemCompat.getActionView(miUpdate);
		imvUpdate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				miUpdateOnClick();
			}
		});
		
		if (!SharedPreUtils.getBoolean(AppConstant.HAS_CITY, false)) {
			Picasso.with(this)
					.load(R.drawable.ic_plus)
					.into(imvUpdate);
			isPlus = true;
		} else {
			Picasso.with(this)
					.load(R.drawable.ic_refresh)
					.into(imvUpdate);
			isPlus = false;
		}
		return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.miSetting:
				Intent intent = new Intent(MainActivity.this, SettingActivity.class);
				startActivityForResult(intent, AppConstant.REQUEST_CODE_SETTING);
				break;
			case R.id.miInfo:
				Log.d("info", "");
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		registerBroadcast();
	}
	
	private void registerBroadcast() {
		if (!isReceiverRegistered) {
			IntentFilter filter = new IntentFilter();
			filter.addAction(AppConstant.ACTION_DATABASE_CHANGED);
			filter.addAction(AppConstant.STATE_UPDATE_CHANGED);
			filter.addAction(Intent.ACTION_TIME_TICK);
			registerReceiver(broadcastReceiver, filter);
			isReceiverRegistered = true;
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(broadcastReceiver);
		isReceiverRegistered = false;
	}
	
	@Override
	protected void onDestroy() {
		if (isReceiverRegistered) {
			unregisterReceiver(broadcastReceiver);
		}
		super.onDestroy();
	}
	
	void miUpdateOnClick() {
		if (isPlus) {
			Intent intent = new Intent(MainActivity.this, SearchActivity.class);
			startActivityForResult(intent, AppConstant.PLACE_AUTOCOMPLETE_REQUEST_CODE);
		} else {
			if (ServiceUtil.isNetworkAvailable(MainActivity.this)) {
				imvUpdate.startAnimation(rotation);
				updateData(SharedPreUtils.getString(ApiConstant.COORDINATE, "-1"));
			} else {
				new AlertDialog.Builder(MainActivity.this)
						.setMessage(getString(R.string.noInternetConnection))
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						})
						.create().show();
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == AppConstant.PLACE_AUTOCOMPLETE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
			Bundle bundle = data.getExtras();
			
			String conditions = bundle.getString(ApiConstant.CONDITIONS);
			
			int idCity = searchFragment.updateDataAndGetID(conditions, true);
			sendDataToFragment(conditions, 1, idCity, true);
			
			String hourly = bundle.getString(ApiConstant.HOURLY);
			sendDataToFragment(hourly, 2, idCity, true);
			
			String forecast = bundle.getString(ApiConstant.FORECAST10DAY);
			sendDataToFragment(forecast, 3, idCity, true);
			
			NotificationUtils.createOrUpdateNotification(this);
			
			viewPager.setPagingEnabled(true);
			indicator.setVisibility(View.VISIBLE);
			
			new Handler().post(new Runnable() {
				@Override
				public void run() {
					if (!SharedPreUtils.isOpenGuide()) {
						FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
						fragmentTransaction.add(R.id.layoutMain, new FragmentGuide());
						fragmentTransaction.commit();
						SharedPreUtils.setIsOpenGuide();
					}
				}
			});
			return;
		}
		
		if (requestCode == AppConstant.REQUEST_CODE_SETTING && resultCode == RESULT_OK) {
			searchFragment.updateDegree();
			currentWeatherFragment.updateDegree();
			weatherHourFragment.updateDegree();
			weatherDayFragment.updateDegree();
		}
	}
	
	private void sendDataToFragment(String s, int id, int idCity, boolean isInsert) {
		switch (id) {
			case 1:
				currentWeatherFragment.updateData(s, idCity, isInsert);
				break;
			case 2:
				weatherHourFragment.updateData(s, idCity, isInsert);
				break;
			case 3:
				weatherDayFragment.updateData(s, idCity, isInsert);
				break;
		}
		
	}
	
	private void updateData(String coordinate) {
		String urlConditions = StringUtils.getURL(ApiConstant.CONDITIONS, coordinate);
		String urlHourly = StringUtils.getURL(ApiConstant.HOURLY, coordinate);
		String urlForecast10day = StringUtils.getURL(ApiConstant.FORECAST10DAY, coordinate);
		
		WeatherRequest request = new WeatherRequest.Builder(this, SharedPreUtils.getInt(AppConstant._ID, -1))
				.withUrlCurrentWeather(urlConditions)
				.withUrlHourly(urlHourly)
				.withUrlForecast10Days(urlForecast10day)
				.withCallback(new WeatherRequestCallback() {
					@Override
					public void onSuccess(Integer result) {
						if (result == WeatherRequest.RESULT_OK) {
							searchFragment.getDataFromDatabase();
							currentWeatherFragment.getDataFromDatabase();
							weatherDayFragment.getDataFromDatabase();
							weatherHourFragment.getDataFromDatabase();
							
							SharedPreUtils.putLong(DatabaseConstant.LAST_UPDATE, System.currentTimeMillis());
							currentWeatherFragment.updateTextViewRecent();
						} else {
							UiHelper.showDialogFail(MainActivity.this);
						}
						if (imvUpdate != null) {
							imvUpdate.clearAnimation();
						}
					}
					
					@Override
					public void onFail(String error) {
						UiHelper.showDialogFail(MainActivity.this);
						if (imvUpdate != null) {
							imvUpdate.clearAnimation();
						}
					}
				})
				.build();
		request.request();
	}
	
	@Override
	public void deleteItemCity(int idCity) {
		searchFragment.deleteItem(idCity);
	}
	
	@Override
	public void choseItemCity(int idCity, String name, String coordinate, String timeZone) {
		SharedPreUtils.putData(idCity, name, coordinate, timeZone);
		searchFragment.chooseItem(idCity);
		currentWeatherFragment.chooseItem(idCity);
		weatherHourFragment.chooseItem(idCity);
		weatherDayFragment.chooseItem(idCity);
	}
	
	@Override
	public void checkCitySizeToEnableViewPagerSwipe(int idCity) {
		if (idCity == -1) {
			viewPager.setPagingEnabled(false);
			indicator.setVisibility(View.INVISIBLE);
			SharedPreUtils.putBoolean(AppConstant.HAS_CITY, false);
		}
	}
	
	public void setPlus(boolean plus) {
		isPlus = plus;
	}
	
	public boolean isPlus() {
		return isPlus;
	}
	
	class MainBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (intent.getAction()) {
				case AppConstant.ACTION_DATABASE_CHANGED: {
					getDataFromDatabase();
					break;
				}
				case Intent.ACTION_TIME_TICK: {
					if (SharedPreUtils.getInt(DatabaseConstant._ID, -1) != -1) {
						if (currentWeatherFragment.getUserVisibleHint()) {
							tvTime.setText(StringUtils.getCurrentDateTime(SharedPreUtils.getString(DatabaseConstant.TIME_ZONE, "+0700")));
						}
						currentWeatherFragment.updateTime();
						currentWeatherFragment.updateTextViewRecent();
					}
					break;
				}
				case AppConstant.STATE_UPDATE_CHANGED: {
					String state = intent.getStringExtra(AppConstant.STATE);
					switch (state) {
						case AppConstant.STATE_START: {
							if (currentWeatherFragment.getUserVisibleHint() && imvUpdate != null) {
								imvUpdate.startAnimation(rotation);
							}
							break;
						}
						case AppConstant.STATE_END: {
							if (currentWeatherFragment.getUserVisibleHint() && imvUpdate != null && imvUpdate.getAnimation() != null) {
								imvUpdate.getAnimation().setRepeatCount(0);
							}
							break;
						}
					}
					break;
				}
			}
		}
	}
}

