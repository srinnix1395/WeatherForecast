package com.qtd.weatherforecast.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.pageindicator.AdapterNotFoundException;
import com.example.pageindicator.IconCirclePageIndicator;
import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.adapter.MainPagerAdapter;
import com.qtd.weatherforecast.callback.FragmentCallback;
import com.qtd.weatherforecast.callback.RequestCallback;
import com.qtd.weatherforecast.callback.ViewHolderCallback;
import com.qtd.weatherforecast.constant.ApiConstant;
import com.qtd.weatherforecast.constant.AppConstant;
import com.qtd.weatherforecast.constant.DatabaseConstant;
import com.qtd.weatherforecast.custom.CustomViewPager;
import com.qtd.weatherforecast.fragment.CurrentWeatherFragment;
import com.qtd.weatherforecast.fragment.SearchFragment;
import com.qtd.weatherforecast.fragment.WeatherDayFragment;
import com.qtd.weatherforecast.fragment.WeatherHourFragment;
import com.qtd.weatherforecast.request.WeatherRequest;
import com.qtd.weatherforecast.service.WeatherForecastService;
import com.qtd.weatherforecast.utils.NetworkUtil;
import com.qtd.weatherforecast.utils.NotificationUtils;
import com.qtd.weatherforecast.utils.SharedPreUtils;
import com.qtd.weatherforecast.utils.StringUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements ViewHolderCallback, FragmentCallback {
	private static final String TAG = "MainActivity";
	@Bind(R.id.toolbar_home)
	Toolbar toolbar;
	
	@Bind(R.id.viewPager)
	CustomViewPager viewPager;
	
	@Bind(R.id.indicator)
	IconCirclePageIndicator indicator;
	
	@Bind(R.id.tv_location)
	TextView tvLocation;
	
	@Bind(R.id.tv_time)
	TextView tvTime;
	
	@Bind(R.id.imv_renew)
	ImageView imvRenew;
	
	@Bind(R.id.tv_1)
	TextView tv1;
	
	@Bind(R.id.layout_location)
	RelativeLayout layoutLocation;
	
	private MainBroadcastReceiver broadcastReceiver;
	private boolean isReceiverRegistered;
	private MainPagerAdapter adapter;
	private PopupMenu popupMenu;
	private AlertDialog alertDialog;
	private Intent intent;
	boolean isPlus;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		initComponent();
	}
	
	private void initComponent() {
		setSupportActionBar(toolbar);
		setupViewPager();
		
		broadcastReceiver = new MainBroadcastReceiver();
		
		if (intent == null) {
			intent = new Intent(MainActivity.this, WeatherForecastService.class);
			startService(intent);
		}
		
		alertDialog = new AlertDialog.Builder(MainActivity.this)
				.setMessage(getString(R.string.errorOnProcessing))
				.setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.create();
		
		if (!SharedPreUtils.getBoolean(AppConstant.HAS_CITY, false)) {
			imvRenew.setImageResource(R.drawable.ic_plus_white_24dp);
			isPlus = true;
		}
	}
	
	public void getDataFromDatabase() {
		((SearchFragment) adapter.getItem(0)).getDataFromDatabase();
		((CurrentWeatherFragment) adapter.getItem(1)).getDataFromDatabase();
		((WeatherHourFragment) adapter.getItem(2)).getDataFromDatabase();
		((WeatherDayFragment) adapter.getItem(3)).getDataFromDatabase();
		Log.d("Update", "Ok");
	}
	
	private void setupViewPager() {
		ArrayList<Fragment> fragments = new ArrayList<>();
		fragments.add(new SearchFragment());
		fragments.add(new CurrentWeatherFragment());
		fragments.add(new WeatherHourFragment());
		fragments.add(new WeatherDayFragment());
		adapter = new MainPagerAdapter(getSupportFragmentManager(), fragments);
		viewPager.setAdapter(adapter);
		try {
			indicator.setupWithViewPager(viewPager);
		} catch (AdapterNotFoundException e) {
			e.printStackTrace();
		}
		viewPager.setOffscreenPageLimit(4);
		
		
		int id = SharedPreUtils.getInt("ID", -1);
		if (id == -1) {
			viewPager.setPagingEnabled(false);
			indicator.setVisibility(View.INVISIBLE);
			viewPager.setCurrentItem(0);
		} else {
			viewPager.setCurrentItem(1);
		}
		
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
	protected void onResume() {
		super.onResume();
		registerBroadcast();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(broadcastReceiver);
		isReceiverRegistered = false;
	}
	
	@OnClick(R.id.imv_renew)
	void renewOnClick() {
		if (isPlus) {
			Intent intent = new Intent(MainActivity.this, SearchActivity.class);
			startActivityForResult(intent, AppConstant.PLACE_AUTOCOMPLETE_REQUEST_CODE);
		} else {
			if (NetworkUtil.isNetworkAvailable(MainActivity.this)) {
				Animation rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_rotation);
				imvRenew.startAnimation(rotation);
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
	
	@OnClick(R.id.imv_more)
	void imvMoreOnClick() {
		if (popupMenu == null) {
			popupMenu = new PopupMenu(MainActivity.this, findViewById(R.id.imv_more));
			popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
			popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					switch (item.getItemId()) {
						case R.id.tv_setting:
							Intent intent = new Intent(MainActivity.this, SettingActivity.class);
							startActivity(intent);
							break;
						case R.id.tv_info:
							Log.d("info", "");
							break;
					}
					return true;
				}
			});
		}
		popupMenu.show();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == AppConstant.PLACE_AUTOCOMPLETE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
			Bundle bundle = data.getExtras();
			
			String conditions = bundle.getString(ApiConstant.CONDITIONS);
			
			int idCity = ((SearchFragment) adapter.getItem(0)).updateDataAndGetID(conditions, true);
			sendDataToFragment(conditions, 1, idCity, true);
			
			String hourly = bundle.getString(ApiConstant.HOURLY);
			sendDataToFragment(hourly, 2, idCity, true);
			
			String forecast = bundle.getString(ApiConstant.FORECAST10DAY);
			sendDataToFragment(forecast, 3, idCity, true);
			
			NotificationUtils.updateNotification(this);
			
			viewPager.setPagingEnabled(true);
			indicator.setVisibility(View.VISIBLE);
		}
	}
	
	private void sendDataToFragment(String s, int id, int idCity, boolean isInsert) {
		switch (id) {
			case 1:
				((CurrentWeatherFragment) adapter.getItem(id)).updateData(s, idCity, isInsert);
				break;
			case 2:
				((WeatherHourFragment) adapter.getItem(id)).updateData(s, idCity, isInsert);
				break;
			case 3:
				((WeatherDayFragment) adapter.getItem(id)).updateData(s, idCity, isInsert);
				break;
		}
		
	}
	
	private void updateData(final String coordinate) {
		String urlConditions = StringUtils.getURL(ApiConstant.CONDITIONS, coordinate);
		String urlHourly = StringUtils.getURL(ApiConstant.HOURLY, coordinate);
		String urlForecast10day = StringUtils.getURL(ApiConstant.FORECAST10DAY, coordinate);
		
		WeatherRequest request = new WeatherRequest.Builder()
				.withUrlCurrentWeather(urlConditions)
				.withUrlHourly(urlHourly)
				.withUrlForecast10Days(urlForecast10day)
				.withCallback(new RequestCallback() {
					@Override
					public void onSuccess(Bundle bundle) {
						int idCity = ((SearchFragment) adapter.getItem(0)).updateDataAndGetID(
								bundle.getString(ApiConstant.CONDITIONS), false);
						sendDataToFragment(bundle.getString(ApiConstant.CONDITIONS), 1, idCity, false);
						sendDataToFragment(bundle.getString(ApiConstant.HOURLY), 2, idCity, false);
						sendDataToFragment(bundle.getString(ApiConstant.FORECAST10DAY), 3, idCity, false);
						stopAnimation();
						SharedPreUtils.putLong(DatabaseConstant.LAST_UPDATE, System.currentTimeMillis());
						((CurrentWeatherFragment) adapter.getItem(1)).updateTextViewRecent();
					}
					
					@Override
					public void onFail(VolleyError error) {
						alertDialog.show();
						stopAnimation();
					}
				})
				.build();
		request.request();
	}
	
	private void stopAnimation() {
		imvRenew.clearAnimation();
	}
	
	@Override
	public void deleteItemCity(int idCity) {
		((SearchFragment) adapter.getItem(0)).deleteItem(idCity);
	}
	
	@Override
	public void choseItemCity(int idCity, String name, String coordinate, String timeZone) {
		SharedPreUtils.putData(idCity, name, coordinate, timeZone);
		((SearchFragment) adapter.getItem(0)).chooseItem(idCity);
		((CurrentWeatherFragment) adapter.getItem(1)).chooseItem(idCity);
		((WeatherHourFragment) adapter.getItem(2)).chooseItem(idCity);
		((WeatherDayFragment) adapter.getItem(3)).chooseItem(idCity);
	}
	
	@Override
	public void checkCitySizeToEnableViewPagerSwipe(int idCity) {
		if (idCity == -1) {
			viewPager.setPagingEnabled(false);
			indicator.setVisibility(View.INVISIBLE);
			SharedPreUtils.putBoolean(AppConstant.HAS_CITY, false);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (popupMenu != null) {
			popupMenu.dismiss();
		}
		if (isReceiverRegistered) {
			unregisterReceiver(broadcastReceiver);
		}
	}
	
	public TextView getTv1() {
		return tv1;
	}
	
	public void setPlus(boolean plus) {
		isPlus = plus;
	}
	
	public boolean isPlus() {
		return isPlus;
	}
	
	public ImageView getImvRenew() {
		return imvRenew;
	}
	
	public TextView getTvLocation() {
		return tvLocation;
	}
	
	public TextView getTvTime() {
		return tvTime;
	}
	
	public RelativeLayout getLayoutLocation() {
		return layoutLocation;
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
						if (adapter.getItem(1).getUserVisibleHint()) {
							tvTime.setText(StringUtils.getCurrentDateTime(SharedPreUtils.getString(DatabaseConstant.TIME_ZONE, "+0700")));
						}
						((CurrentWeatherFragment) adapter.getItem(1)).updateTime();
						((CurrentWeatherFragment) adapter.getItem(1)).updateTextViewRecent();
					}
					break;
				}
				case AppConstant.STATE_UPDATE_CHANGED: {
					String state = intent.getStringExtra(AppConstant.STATE);
					switch (state) {
						case AppConstant.STATE_START: {
							if (adapter.getItem(1).getUserVisibleHint()) {
								Animation rotation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.clockwise_rotation);
								imvRenew.startAnimation(rotation);
							}
							break;
						}
						case AppConstant.STATE_END: {
							if (adapter.getItem(1).getUserVisibleHint() && imvRenew.getAnimation() != null) {
								imvRenew.getAnimation().setRepeatCount(0);
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

