package com.qtd.weatherforecast;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.qtd.weatherforecast.utility.SharedPreUtils;

/**
 * Created by Dell on 4/24/2016.
 */
public class AppController extends Application {
    private static SharedPreUtils sharedPreferences;
    public static final String TAG = AppController.class.getSimpleName();
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;

    private static AppController instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        sharedPreferences = new SharedPreUtils(getApplicationContext());
    }

    public static synchronized AppController getInstance() {
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return requestQueue;
    }

    public static SharedPreUtils getSharedPreferences() {
        return sharedPreferences;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (imageLoader == null) {
            imageLoader = new ImageLoader(requestQueue, new LruBitmapCache());
        }
        return imageLoader;
    }

    public void addToRequestQueue(Request request, String tag) {
        request.setTag(TAG);
        getRequestQueue().add(request);
    }

    public void addToRequestQueue(Request request) {
        request.setTag(TAG);
        getRequestQueue().add(request);
    }

    public void cancelPendingRequest(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }
}
