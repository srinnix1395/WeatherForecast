package com.qtd.weatherforecast.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;

import com.android.volley.VolleyError;
import com.qtd.weatherforecast.callback.RequestCallback;
import com.qtd.weatherforecast.constant.ApiConstant;
import com.qtd.weatherforecast.constant.AppConstant;
import com.qtd.weatherforecast.constant.DatabaseConstant;
import com.qtd.weatherforecast.database.MyDatabaseHelper;
import com.qtd.weatherforecast.database.ProcessJson;
import com.qtd.weatherforecast.model.City;
import com.qtd.weatherforecast.model.CurrentWeather;
import com.qtd.weatherforecast.model.WeatherDay;
import com.qtd.weatherforecast.model.WeatherHour;
import com.qtd.weatherforecast.request.WeatherRequest;
import com.qtd.weatherforecast.utils.NotificationUtils;
import com.qtd.weatherforecast.utils.ServiceUtil;
import com.qtd.weatherforecast.utils.SharedPreUtils;
import com.qtd.weatherforecast.utils.StringUtils;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Dell on 4/25/2016.
 */
public class WeatherForecastService extends Service {
	private boolean isRegistered = false;
	private NetworkBroadcastReceiver receiver;
	
	@Override
	public void onCreate() {
		super.onCreate();
		if (!isRegistered) {
			registerBroadcast();
		}
	}
	
	private void registerBroadcast() {
		receiver = new NetworkBroadcastReceiver();
		IntentFilter filters = new IntentFilter();
		filters.addAction(AppConstant.CONNECTIVITY_CHANGED);
		registerReceiver(receiver, filters);
		isRegistered = true;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		boolean onNotify = SharedPreUtils.getBoolean(AppConstant.STATE_NOTIFICATION, true);
		if (onNotify) {
			NotificationUtils.createOrUpdateNotification(WeatherForecastService.this);
		}
//		Thread thread = new Thread(runnable);
//		thread.run();
		return START_STICKY;
	}
	
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			while (true) {
				if (ServiceUtil.isNetworkAvailable(WeatherForecastService.this)
						&& SharedPreUtils.getInt(DatabaseConstant._ID, -1) != -1) {
					requestData();
				}
				try {
					Thread.sleep(AppConstant.timeDelay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};
	
	@Override
	public void onDestroy() {
		NotificationUtils.clearNotification(this);
		if (isRegistered) {
			unregisterReceiver(receiver);
		}
		super.onDestroy();
	}
	
	private void requestData() {
		MyDatabaseHelper databaseHelper = MyDatabaseHelper.getInstance(this);
		final ArrayList<City> cities = databaseHelper.getAllCities();
		databaseHelper.close();
		broadcastUpdateState(AppConstant.STATE_UPDATE_CHANGED, AppConstant.STATE_START);
		
		String urlConditions;
		String urlHourly;
		String urlForecast10day;
		String coordinate;
		
		for (int i = 0; i < cities.size(); i++) {
			final int id = cities.get(i).getId();
			
			coordinate = cities.get(i).getCoordinate();
			urlConditions = StringUtils.getURL(ApiConstant.CONDITIONS, coordinate);
			urlHourly = StringUtils.getURL(ApiConstant.HOURLY, coordinate);
			urlForecast10day = StringUtils.getURL(ApiConstant.FORECAST10DAY, coordinate);
			
			WeatherRequest request = new WeatherRequest.Builder()
					.withUrlCurrentWeather(urlConditions)
					.withUrlHourly(urlHourly)
					.withUrlForecast10Days(urlForecast10day)
					.withCallback(new RequestCallback() {
						@Override
						public void onSuccess(Bundle bundle) {
							updateDatabase(bundle, id);
							if (SharedPreUtils.getInt(AppConstant._ID, -1) == id
									&& SharedPreUtils.getBoolean(AppConstant.STATE_NOTIFICATION, true)) {
								NotificationUtils.createOrUpdateNotification(WeatherForecastService.this);
							}
							broadcastDatabaseState();
							broadcastUpdateState(AppConstant.STATE_UPDATE_CHANGED, AppConstant.STATE_END);
						}
						
						@Override
						public void onFail(VolleyError error) {
							broadcastUpdateState(AppConstant.STATE_UPDATE_CHANGED, AppConstant.STATE_END);
						}
					})
					.build();
			request.request();
		}
	}
	
	private void updateDatabase(Bundle bundle, int idCity) {
		try {
			MyDatabaseHelper databaseHelper = MyDatabaseHelper.getInstance(this);
			CurrentWeather currentWeather = ProcessJson.getCurrentWeather(bundle.getString(ApiConstant.CONDITIONS));
			ArrayList<WeatherHour> arrHour = ProcessJson.getAllWeatherHours(bundle.getString(ApiConstant.HOURLY));
			ArrayList<WeatherDay> arrDay = ProcessJson.getAllWeatherDays(bundle.getString(ApiConstant.FORECAST10DAY));
			
			databaseHelper.updateAllData(currentWeather, idCity, arrHour, arrDay);
			
			SharedPreUtils.putLong(DatabaseConstant.LAST_UPDATE, System.currentTimeMillis());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void broadcastUpdateState(String action, String state) {
		Intent intent = new Intent(action);
		intent.putExtra(AppConstant.STATE, state);
		sendBroadcast(intent);
	}
	
	private void broadcastDatabaseState() {
		Intent intent = new Intent(AppConstant.ACTION_DATABASE_CHANGED);
		sendBroadcast(intent);
	}
	
	class NetworkBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(AppConstant.CONNECTIVITY_CHANGED)) {
				if (ServiceUtil.isNetworkAvailable(WeatherForecastService.this)) {
					long time = SharedPreUtils.getLong(com.qtd.weatherforecast.constant.DatabaseConstant.LAST_UPDATE, 0);
					long now = System.currentTimeMillis();
					if (now - time > 60000) {
						requestData();
					}
				}
			}
		}
	}
}
