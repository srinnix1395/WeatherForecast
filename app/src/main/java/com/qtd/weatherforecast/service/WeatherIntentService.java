package com.qtd.weatherforecast.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import com.qtd.weatherforecast.database.MyDatabaseHelper;
import com.qtd.weatherforecast.database.ProcessJson;
import com.qtd.weatherforecast.model.City;
import com.qtd.weatherforecast.model.CityPlus;
import com.qtd.weatherforecast.model.CurrentWeather;
import com.qtd.weatherforecast.model.WeatherDay;
import com.qtd.weatherforecast.model.WeatherHour;
import com.qtd.weatherforecast.utility.ImageUtils;
import com.qtd.weatherforecast.utility.NetworkUtil;
import com.qtd.weatherforecast.utility.SharedPreUtils;
import com.qtd.weatherforecast.utility.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Dell on 5/7/2016.
 */
public class WeatherIntentService extends IntentService {
    private static final String TAG = "intentService";
    private static final int NOTIFICATION_ID = 0;
    private static final String BROADCAST_ACTION = "com.qtd.weatherforecast";

    private Intent intent;
    private int timeDelay = 60000;

    private String urlConditions;
    private String urlForecast10day;
    private String urlHourly;
    MyDatabaseHelper databaseHelper;
    NotificationCompat.Builder notiBuilder;

    public WeatherIntentService() {
        super("MyIntentService");
        databaseHelper = MyDatabaseHelper.getInstance(this);
        intent = new Intent(BROADCAST_ACTION);
        createNotification();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        downloadData();
    }


    private void createNotification() {
        int id = SharedPreUtils.getInt("ID", -1);
        if (id != -1) {
            Intent intent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            CityPlus city = databaseHelper.getCityByID(id);

            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification);
            remoteViews.setImageViewResource(R.id.imv_icon, ImageUtils.getImageResourceNotification(city.getIcon()));
            remoteViews.setTextViewText(R.id.tv_location, city.getName());
            remoteViews.setTextViewText(R.id.tv_weather, city.getWeather());
            remoteViews.setTextViewText(R.id.tv_temp, String.valueOf(city.getTemp()) + "°");
            notiBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(ImageUtils.getImageResourceCurrentWeather(city.getIcon()))
                    .setContent(remoteViews)
                    .setOngoing(true)
                    .setContentIntent(pendingIntent);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, notiBuilder.build());
        }
    }

    private void downloadData() {
        if (NetworkUtil.getInstance().isNetworkAvailable(this)) {
            final ArrayList<City> cities = databaseHelper.getAllCities();
            for (int i = 0; i < cities.size(); i++) {
                final City temp = cities.get(i);
                urlConditions = StringUtils.getURL(ApiConstant.CONDITIONS, cities.get(i).getCoordinate());
                urlHourly = StringUtils.getURL(ApiConstant.HOURLY, cities.get(i).getCoordinate());
                urlForecast10day = StringUtils.getURL(ApiConstant.FORECAST10DAY, cities.get(i).getCoordinate());
                final JSONArray array = new JSONArray();

                JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, urlConditions, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        array.put(response);
                        requestHourly(array, temp.getId());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error", error.toString());
                    }
                });
                AppController.getInstance().addToRequestQueue(objectRequest);
            }
        }
    }

    private void requestHourly(final JSONArray a, final int idCity) {
        JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, urlHourly, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("hourly", response.toString());
                a.put(response);
                requestForecast10day(a, idCity);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
            }
        });
        AppController.getInstance().addToRequestQueue(request1);
    }

    private void requestForecast10day(final JSONArray a, final int idCity) {
        JsonObjectRequest request2 = new JsonObjectRequest(Request.Method.GET, urlForecast10day, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("forecast10day", response.toString());
                a.put(response);
                updateDatabase(a, idCity);
                updateNoti();
                broadcastToActivity();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
            }
        });
        AppController.getInstance().addToRequestQueue(request2);
    }

    private void broadcastToActivity() {
        intent.putExtra("Update", true);
        sendBroadcast(intent);
    }

    private void updateNoti() {
        int id = SharedPreUtils.getInt("ID", - 1);
        if (id != -1) {
            CityPlus cityPlus = databaseHelper.getCityByID(id);
            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification);
            remoteViews.setImageViewResource(R.id.imv_icon, ImageUtils.getImageResourceNotification(cityPlus.getIcon()));
            remoteViews.setTextViewText(R.id.tv_weather, cityPlus.getWeather());
            remoteViews.setTextViewText(R.id.tv_temp, String.valueOf(cityPlus.getTemp()) + "°");
            Intent intent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            notiBuilder.setSmallIcon(ImageUtils.getImageResourceCurrentWeather(cityPlus.getIcon()));
            notiBuilder.setContent(remoteViews);
            notiBuilder.setOngoing(true);
            notiBuilder.setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, notiBuilder.build());
        }

    }

    private void updateDatabase(JSONArray a, int idCity) {
        try {
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
