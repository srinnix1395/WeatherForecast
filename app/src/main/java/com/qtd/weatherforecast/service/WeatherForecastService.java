package com.qtd.weatherforecast.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qtd.weatherforecast.AppController;
import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.activity.MainActivity;
import com.qtd.weatherforecast.constant.ApiConstant;
import com.qtd.weatherforecast.constant.DatabaseConstant;
import com.qtd.weatherforecast.database.MyDatabaseHelper;
import com.qtd.weatherforecast.database.ProcessJson;
import com.qtd.weatherforecast.model.City;
import com.qtd.weatherforecast.model.CityPlus;
import com.qtd.weatherforecast.model.CurrentWeather;
import com.qtd.weatherforecast.model.WeatherDay;
import com.qtd.weatherforecast.model.WeatherHour;
import com.qtd.weatherforecast.utils.ImageUtils;
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
    public static final String ACTION_DATABASE_CHANGED = "com.qtd.weatherforecast.activity.MainActivity";
    public static final int NOTIFICATION_ID = 1012;
    public static final String STATE_UPDATE_CHANGED = "state_update_changed";
    public static final String STATE_START = "state_start";
    public static final String STATE_END = "state_end";
    public static final String STATE = "state";
    public static final String STATE_NOTIFICATION = "state_noti";

    private int timeDelay = 180000;
    private Handler handler;
    private boolean isRegistered = false;
    private NetworkBroadcastReceiver receiver;

    @Override
    public void onCreate() {
        super.onCreate();
        if (!isRegistered) {
            receiver = new NetworkBroadcastReceiver();
            IntentFilter filters = new IntentFilter();
            filters.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            registerReceiver(receiver, filters);
            isRegistered = true;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void createNotification() {
        int id = SharedPreUtils.getInt("ID", -1);
        if (id != -1) {
            Intent intent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            MyDatabaseHelper databaseHelper = MyDatabaseHelper.getInstance(this);
            CityPlus city = databaseHelper.getCityByID(id);
            databaseHelper.close();

            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification);
            remoteViews.setImageViewResource(R.id.imv_icon, ImageUtils.getImageResourceNotification(city.getIcon()));
            remoteViews.setTextViewText(R.id.tv_location, city.getFullName());
            remoteViews.setTextViewText(R.id.tv_weather, city.getWeather());
            remoteViews.setTextViewText(R.id.tv_temp, String.valueOf(city.getTemp()) + "°");
            NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(ImageUtils.getImageResourceCurrentWeather(city.getIcon()))
                    .setContent(remoteViews)
                    .setOngoing(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(city.getTemp() + "°");
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, notiBuilder.build());
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean onNotify = SharedPreUtils.getBoolean(STATE_NOTIFICATION, true);
        if (onNotify) {
            NotificationUtils.createNotification(WeatherForecastService.this);
        }
        handler = new Handler();
        this.run();
        return START_STICKY;
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
                broadcastUpdateState(STATE_UPDATE_CHANGED, STATE_END);
            }
        });
        AppController.getInstance().addToRequestQueue(request1);
    }

    private void requestForecast10day(final JSONArray a, final int idCity, String coordinate) {
        String urlForecast10day = StringUtils.getURL(ApiConstant.FORECAST10DAY, coordinate);
        JsonObjectRequest request2 = new JsonObjectRequest(Request.Method.GET, urlForecast10day, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("forecast10day", response.toString());
                a.put(response);
                updateDatabase(a, idCity);
                if (SharedPreUtils.getBoolean(STATE_NOTIFICATION, true)) {
                    NotificationUtils.updateNotification(WeatherForecastService.this);
                }
                broadcastDatabaseState();
                broadcastUpdateState(STATE_UPDATE_CHANGED, STATE_END);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
                broadcastUpdateState(STATE_UPDATE_CHANGED, STATE_END);
            }
        });
        AppController.getInstance().addToRequestQueue(request2);
    }



    private void updateNoti() {
        int id = SharedPreUtils.getInt("ID", -1);
        if (id != -1) {
            MyDatabaseHelper databaseHelper = MyDatabaseHelper.getInstance(this);
            CityPlus cityPlus = databaseHelper.getCityByID(id);
            databaseHelper.close();
            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification);
            remoteViews.setImageViewResource(R.id.imv_icon, ImageUtils.getImageResourceNotification(cityPlus.getIcon()));
            remoteViews.setTextViewText(R.id.tv_weather, cityPlus.getWeather());
            remoteViews.setTextViewText(R.id.tv_temp, String.valueOf(cityPlus.getTemp()) + "°");
            remoteViews.setTextViewText(R.id.tv_location, cityPlus.getFullName());
            Intent intent = new Intent(WeatherForecastService.this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(WeatherForecastService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(WeatherForecastService.this)
                    .setSmallIcon(ImageUtils.getImageResourceCurrentWeather(cityPlus.getIcon()))
                    .setContent(remoteViews)
                    .setOngoing(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(cityPlus.getTemp() + "°");
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, notiBuilder.build());
        }
    }

    private void updateDatabase(JSONArray a, int idCity) {
        try {
            MyDatabaseHelper databaseHelper = MyDatabaseHelper.getInstance(this);
            CurrentWeather currentWeather = ProcessJson.getCurrentWeather(a.getJSONObject(0));
            databaseHelper.updateCurrentWeather(currentWeather, idCity);
            ArrayList<WeatherHour> arrHour = ProcessJson.getAllWeatherHours(a.getJSONObject(1));
            for (int i = 0; i < arrHour.size(); i++) {
                databaseHelper.updateWeatherHour(arrHour.get(i), idCity, i);
            }
            ArrayList<WeatherDay> arrDay = ProcessJson.getAllWeatherDays(a.getJSONObject(2));
            for (int i = 0; i < arrDay.size(); i++) {
                databaseHelper.updateWeatherDay(arrDay.get(i), idCity, i);
            }
            databaseHelper.close();
            SharedPreUtils.putLong(DatabaseConstant.LAST_UPDATE, System.currentTimeMillis());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
        if (isRegistered) {
            unregisterReceiver(receiver);
        }
    }

    @Override
    public void run() {
        if (NetworkUtil.getInstance().isNetworkAvailable(WeatherForecastService.this)) {
            requestData();
        }
        handler.postDelayed(this, timeDelay);
    }

    private void requestData() {
        MyDatabaseHelper databaseHelper = MyDatabaseHelper.getInstance(this);
        final ArrayList<City> cities = databaseHelper.getAllCities();
        databaseHelper.close();
        broadcastUpdateState(STATE_UPDATE_CHANGED, STATE_START);
        for (int i = 0; i < cities.size(); i++) {
            City temp = cities.get(i);
            final int id = temp.getId();
            final String coordinate = cities.get(i).getCoordinate();
            String urlConditions = StringUtils.getURL(ApiConstant.CONDITIONS, coordinate);

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
                    broadcastUpdateState(STATE_UPDATE_CHANGED, STATE_END);
                }
            });
            AppController.getInstance().addToRequestQueue(objectRequest);
        }
    }

    private void broadcastUpdateState(String action, String state) {
        Intent intent = new Intent(action);
        intent.putExtra(STATE, state);
        sendBroadcast(intent);
    }

    private void broadcastDatabaseState() {
        Intent intent = new Intent(ACTION_DATABASE_CHANGED);
        sendBroadcast(intent);
    }

    class NetworkBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                if (isOnline(WeatherForecastService.this)) {
                    long time = SharedPreUtils.getLong(DatabaseConstant.LAST_UPDATE, 0);
                    long now = System.currentTimeMillis();
                    if (now - time > 60000) {
                        requestData();
                    }
                }
            }
        }

        public boolean isOnline(Context context) {

            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            //should check null because in airplane mode it will be null
            return (netInfo != null && netInfo.isConnected());

        }
    }

}
