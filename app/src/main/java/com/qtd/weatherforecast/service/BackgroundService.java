package com.qtd.weatherforecast.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qtd.weatherforecast.AppController;
import com.qtd.weatherforecast.constant.ApiConstant;

import org.json.JSONObject;

/**
 * Created by Dell on 4/25/2016.
 */
public class BackgroundService extends Service implements Runnable{
    public static final String TAG = "BroadcastService";
    public static final String BROADCAST_ACTION = "com.qtd.weatherforecast";

    private final Handler handler = new Handler();
    private Intent intent;
    private int timeDelay = 3600000;

    public int getTimeDelay() {
        return timeDelay;
    }

    public void setTimeDelay(int timeDelay) {
        this.timeDelay = timeDelay;
    }

    public BackgroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.removeCallbacks(this);
        handler.postDelayed(this, 1000);
        return START_STICKY;
    }


    @Override
    public void run() {
        String url = ApiConstant.URL + ApiConstant.API_KEY + "/" + ApiConstant.FEATURE_FORECAST + "/lang:" + ApiConstant.VIETNAMESE + "/q/VietNam/HaNoi.json";
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                intent.putExtra("response", response.toString());
                sendBroadcast(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", error.toString());
            }
        });
        AppController.getInstance().addToRequestQueue(objectRequest);
        handler.postDelayed(this, timeDelay);

    }
}
