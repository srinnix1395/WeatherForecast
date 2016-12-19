package com.qtd.weatherforecast.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
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
import com.qtd.weatherforecast.utils.NetworkUtil;
import com.qtd.weatherforecast.utils.NotificationUtils;
import com.qtd.weatherforecast.utils.SharedPreUtils;
import com.qtd.weatherforecast.utils.StringUtils;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Dell on 4/25/2016.
 */
public class WeatherForecastService extends Service implements Runnable {
	private Handler handler;
	private boolean isRegistered = false;
	private NetworkBroadcastReceiver receiver;
	
	@Override
	public void onCreate() {
		super.onCreate();
		if (!isRegistered) {
			receiver = new NetworkBroadcastReceiver();
			IntentFilter filters = new IntentFilter();
			filters.addAction(AppConstant.CONNECTIVITY_CHANGED);
			registerReceiver(receiver, filters);
			isRegistered = true;
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		boolean onNotify = SharedPreUtils.getBoolean(AppConstant.STATE_NOTIFICATION, true);
		if (onNotify) {
			NotificationUtils.createNotification(WeatherForecastService.this);
		}
		handler = new Handler();
		this.run();
		return START_STICKY;
	}
	
	
	@Override
	public void run() {
		if (NetworkUtil.isNetworkAvailable(WeatherForecastService.this)
				&& SharedPreUtils.getInt(DatabaseConstant._ID, -1) != -1) {
			requestData();
		}
		handler.postDelayed(this, AppConstant.timeDelay);
	}
	
	@Override
	public void onDestroy() {
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(AppConstant.NOTIFICATION_ID);
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
		for (int i = 0; i < cities.size(); i++) {
			City temp = cities.get(i);
			final int id = temp.getId();
			final String coordinate = cities.get(i).getCoordinate();
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
							updateDatabase(bundle, id);
							if (SharedPreUtils.getBoolean(AppConstant.STATE_NOTIFICATION, true)) {
								NotificationUtils.updateNotification(WeatherForecastService.this);
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
			
			SharedPreUtils.putLong(com.qtd.weatherforecast.constant.DatabaseConstant.LAST_UPDATE, System.currentTimeMillis());
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
				if (NetworkUtil.isNetworkAvailable(WeatherForecastService.this)) {
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
