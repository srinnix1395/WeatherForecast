package com.qtd.weatherforecast.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
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
import com.qtd.weatherforecast.callback.SearchCallback;
import com.qtd.weatherforecast.callback.ViewHolderCallback;
import com.qtd.weatherforecast.callback.WeatherRequestCallback;
import com.qtd.weatherforecast.constant.ApiConstant;
import com.qtd.weatherforecast.constant.AppConstant;
import com.qtd.weatherforecast.constant.DatabaseConstant;
import com.qtd.weatherforecast.custom.CustomViewPager;
import com.qtd.weatherforecast.fragment.CurrentWeatherFragment;
import com.qtd.weatherforecast.fragment.GuideFragment;
import com.qtd.weatherforecast.fragment.LocationFragment;
import com.qtd.weatherforecast.fragment.SearchFragment;
import com.qtd.weatherforecast.fragment.WeatherDayFragment;
import com.qtd.weatherforecast.fragment.WeatherHourFragment;
import com.qtd.weatherforecast.model.City;
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

public class MainActivity extends AppCompatActivity implements ViewHolderCallback
		, FragmentCallback, SearchCallback {

	private static final String FRAGMENT_SEARCH = "FRAGMENT_SEARCH";

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

	@Bind(R.id.imvBackground)
	ImageView imvBackground;

	public ImageView imvUpdate;

	private MainBroadcastReceiver broadcastReceiver;
	private boolean isReceiverRegistered;
	private Intent intent;
	boolean isPlus;

	private LocationFragment locationFragment;
	private CurrentWeatherFragment currentWeatherFragment;
	private WeatherHourFragment weatherHourFragment;
	private WeatherDayFragment weatherDayFragment;
	private Animation rotation;
	private String background;

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

		background = SharedPreUtils.getBackground();
		setImageBackground();
		setupViewPager();
		initAnimation();

		broadcastReceiver = new MainBroadcastReceiver();

		if (intent == null) {
			intent = new Intent(MainActivity.this, WeatherForecastService.class);
			startService(intent);
		}
	}

	private void setImageBackground() {
		Picasso.with(this)
				.load("file:///android_asset/" + background)
				.resize(1024, 1024)
				.into(imvBackground);
	}

	private void initAnimation() {
		rotation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.clockwise_rotation);
	}

	public void getDataFromDatabase() {
		locationFragment.getDataFromDatabase();
		currentWeatherFragment.getDataFromDatabase();
		weatherHourFragment.getDataFromDatabase();
		weatherDayFragment.getDataFromDatabase();
		Log.d("Update", "Ok");
	}

	private void setupViewPager() {
		locationFragment = LocationFragment.newInstance();
		currentWeatherFragment = CurrentWeatherFragment.newInstance();
		weatherHourFragment = WeatherHourFragment.newInstance();
		weatherDayFragment = WeatherDayFragment.newInstance();

		ArrayList<Fragment> fragments = new ArrayList<>();
		fragments.add(locationFragment);
		fragments.add(currentWeatherFragment);
		fragments.add(weatherHourFragment);
		fragments.add(weatherDayFragment);
		MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(), fragments);

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
			imvUpdate.setImageResource(R.drawable.ic_plus);
			isPlus = true;
		} else {
			imvUpdate.setImageResource(R.drawable.ic_refresh);
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
			filter.addAction(Intent.ACTION_SCREEN_ON);
			registerReceiver(broadcastReceiver, filter);
			isReceiverRegistered = true;
		}
	}

	@Override
	protected void onPause() {
		unregisterReceiver(broadcastReceiver);
		isReceiverRegistered = false;
		super.onPause();
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
			FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
			fragmentTransaction.add(R.id.layoutMain, new SearchFragment(), FRAGMENT_SEARCH);
			fragmentTransaction.commit();
		} else {
			if (ServiceUtil.isNetworkAvailable(MainActivity.this)) {
				imvUpdate.startAnimation(rotation);
				currentWeatherFragment.updating();
				updateData(SharedPreUtils.getString(ApiConstant.COORDINATE, "-1"));
			} else {
				UiHelper.showDialogNoConnection(this);
			}
		}
	}

	@Override
	public void onSearchFinish(Bundle bundle) {
		int result = bundle.getInt(ApiConstant.RESULTS);
		if (result == WeatherRequest.RESULT_OK) {
			City city = bundle.getParcelable(ApiConstant.CITY);
			if (city != null && city.getId() != -1) {
				locationFragment.insertCity(city);
				currentWeatherFragment.getDataFromDatabase();
				weatherHourFragment.getDataFromDatabase();
				weatherDayFragment.getDataFromDatabase();

				NotificationUtils.createOrUpdateNotification(this);

				viewPager.setPagingEnabled(true);
				indicator.setVisibility(View.VISIBLE);

				FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
				fragmentTransaction.remove(getSupportFragmentManager().findFragmentByTag(FRAGMENT_SEARCH));
				if (!SharedPreUtils.isOpenGuide()) {
					fragmentTransaction.add(R.id.layoutMain, new GuideFragment());
					SharedPreUtils.setIsOpenGuide();
				}
				fragmentTransaction.commit();
			} else {
				UiHelper.showDialogFail(this);
			}
		} else {
			UiHelper.showDialogFail(this);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == AppConstant.REQUEST_CODE_SETTING) {
			String backgroundNew = SharedPreUtils.getBackground();
			if (!background.equals(backgroundNew)) {
				background = backgroundNew;
				setImageBackground();
			}
			if (resultCode == RESULT_OK) {
				locationFragment.updateDegree();
				currentWeatherFragment.updateDegree();
				weatherHourFragment.updateDegree();
				weatherDayFragment.updateDegree();
			}
		}
	}

	private void updateData(String coordinate) {
		if (coordinate.equals("-1")) {
			imvUpdate.clearAnimation();
			currentWeatherFragment.updateTextViewRecent();
		} else {
			String urlConditions = StringUtils.getURL(ApiConstant.CONDITIONS, coordinate);
			String urlHourly = StringUtils.getURL(ApiConstant.HOURLY, coordinate);
			String urlForecast10day = StringUtils.getURL(ApiConstant.FORECAST10DAY, coordinate);

			WeatherRequest request = new WeatherRequest.Builder(this, SharedPreUtils.getInt(AppConstant._ID, -1))
					.withUrlCurrentWeather(urlConditions)
					.withUrlHourly(urlHourly)
					.withUrlForecast10Days(urlForecast10day)
					.withCallback(new WeatherRequestCallback() {
						@Override
						public void onSuccess(Bundle bundle) {
							int result = bundle.getInt(ApiConstant.RESULTS);
							if (result == WeatherRequest.RESULT_OK) {
								locationFragment.getDataFromDatabase();
								currentWeatherFragment.getDataFromDatabase();
								weatherDayFragment.getDataFromDatabase();
								weatherHourFragment.getDataFromDatabase();

								SharedPreUtils.putLong(DatabaseConstant.LAST_UPDATE, System.currentTimeMillis());
								NotificationUtils.createOrUpdateNotification(MainActivity.this);
							} else {
								UiHelper.showDialogFail(MainActivity.this);
							}
							if (imvUpdate != null) {
								imvUpdate.clearAnimation();
							}
							currentWeatherFragment.updateTextViewRecent();
						}

						@Override
						public void onFail(String error) {
							UiHelper.showDialogFail(MainActivity.this);
							if (imvUpdate != null) {
								imvUpdate.clearAnimation();
							}
							currentWeatherFragment.updateTextViewRecent();

						}
					})
					.build();
			request.request();
		}
	}

	@Override
	public void deleteItemCity(int idCity) {
		locationFragment.deleteItem(idCity);
	}

	@Override
	public void choseItemCity(int idCity, String name, String coordinate, String timeZone) {
		SharedPreUtils.putData(idCity, name, coordinate, timeZone);
		locationFragment.chooseItem(idCity);
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

	@Override
	public void onBackPressed() {
		Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_SEARCH);
		if (fragment != null) {
			getSupportFragmentManager().beginTransaction().remove(fragment).commit();
			UiHelper.closeSoftKeyboard(this);
			return;
		}

		finish();
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
				case AppConstant.STATE_UPDATE_CHANGED: {
					String state = intent.getStringExtra(AppConstant.STATE);
					switch (state) {
						case AppConstant.STATE_START: {
							if (currentWeatherFragment.getUserVisibleHint()) {
								if (imvUpdate != null) {
									imvUpdate.startAnimation(rotation);
								}
								currentWeatherFragment.updating();
							}
							break;
						}
						case AppConstant.STATE_END: {
							if (currentWeatherFragment.getUserVisibleHint()) {
								if (imvUpdate != null && imvUpdate.getAnimation() != null) {
									imvUpdate.getAnimation().setRepeatCount(0);
								}
								currentWeatherFragment.updateTextViewRecent();
							}
							break;
						}
					}
					break;
				}
				case Intent.ACTION_SCREEN_ON:
				case Intent.ACTION_TIME_TICK: {
					if (SharedPreUtils.getBoolean(AppConstant.HAS_CITY, false)) {
						if (currentWeatherFragment.getUserVisibleHint()) {
							tvTime.setText(StringUtils.getCurrentDateTime(SharedPreUtils.getString(DatabaseConstant.TIME_ZONE, "+0700")));
						}
						currentWeatherFragment.updateTime();
						currentWeatherFragment.updateTextViewRecent();
					}
					break;
				}
			}
		}
	}
}

