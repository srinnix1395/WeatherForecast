package com.qtd.weatherforecast.utility;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by Dell on 4/25/2016.
 */
public class NetworkUtil {

    private static NetworkUtil instance;

    public static synchronized NetworkUtil getInstance() {
        if (instance == null) {
            instance = new NetworkUtil();
        }
        return instance;
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isAvailable() && connectivityManager
                .getActiveNetworkInfo().isConnected());
    }
}
