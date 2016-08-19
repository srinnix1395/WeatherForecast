package com.qtd.weatherforecast.utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by Dell on 4/25/2016.
 */
public class NetworkUtil {
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isAvailable() && connectivityManager
                .getActiveNetworkInfo().isConnected());
    }
}
