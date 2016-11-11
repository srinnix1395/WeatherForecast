package com.qtd.weatherforecast.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qtd.weatherforecast.AppController;
import com.qtd.weatherforecast.constant.ApiConstant;
import com.qtd.weatherforecast.constant.AppConstant;
import com.qtd.weatherforecast.database.MyDatabaseHelper;
import com.qtd.weatherforecast.database.ProcessJson;
import com.qtd.weatherforecast.model.City;
import com.qtd.weatherforecast.model.CurrentWeather;
import com.qtd.weatherforecast.model.WeatherDay;
import com.qtd.weatherforecast.model.WeatherHour;
import com.qtd.weatherforecast.utils.NetworkUtil;
import com.qtd.weatherforecast.utils.NotificationUtils;
import com.qtd.weatherforecast.utils.SharedPreUtils;
import com.qtd.weatherforecast.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        if (NetworkUtil.isNetworkAvailable(WeatherForecastService.this)) {
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
            String urlConditions = StringUtils.getURL(com.qtd.weatherforecast.constant.ApiConstant.CONDITIONS, coordinate);

            final JSONArray array = new JSONArray();

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, urlConditions, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    array.put(response);
                    requestHourly(array, id, coordinate);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error", error.toString());
                    broadcastUpdateState(AppConstant.STATE_UPDATE_CHANGED, AppConstant.STATE_END);
                }
            });
            AppController.getInstance().addToRequestQueue(objectRequest);
        }
    }

    private void requestHourly(final JSONArray a, final int idCity, final String coordinate) {
        String urlHourly = StringUtils.getURL(ApiConstant.HOURLY, coordinate);
        JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, urlHourly, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("hourly", response.toString());
                a.put(response);
                requestForecast10day(a, idCity, coordinate);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
                broadcastUpdateState(AppConstant.STATE_UPDATE_CHANGED, AppConstant.STATE_END);
            }
        });
        AppController.getInstance().addToRequestQueue(request1);
    }

    private void requestForecast10day(final JSONArray array, final int idCity, String coordinate) {
        String urlForecast10day = StringUtils.getURL(com.qtd.weatherforecast.constant.ApiConstant.FORECAST10DAY, coordinate);
        JsonObjectRequest request2 = new JsonObjectRequest(Request.Method.GET, urlForecast10day, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("forecast10day", response.toString());
                array.put(response);
                updateDatabase(array, idCity);
                if (SharedPreUtils.getBoolean(AppConstant.STATE_NOTIFICATION, true)) {
                    NotificationUtils.updateNotification(WeatherForecastService.this);
                }
                broadcastDatabaseState();
                broadcastUpdateState(AppConstant.STATE_UPDATE_CHANGED, AppConstant.STATE_END);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
                broadcastUpdateState(AppConstant.STATE_UPDATE_CHANGED, AppConstant.STATE_END);
            }
        });
        AppController.getInstance().addToRequestQueue(request2);
    }

    private void updateDatabase(JSONArray a, int idCity) {
        try {
            MyDatabaseHelper databaseHelper = MyDatabaseHelper.getInstance(this);
            CurrentWeather currentWeather = ProcessJson.getCurrentWeather(a.getJSONObject(0));
            ArrayList<WeatherHour> arrHour = ProcessJson.getAllWeatherHours(a.getJSONObject(1));
            ArrayList<WeatherDay> arrDay = ProcessJson.getAllWeatherDays(a.getJSONObject(2));

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
