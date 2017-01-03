package com.qtd.weatherforecast.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by Dell on 4/25/2016.
 */
public class ServiceUtil {
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isAvailable() && connectivityManager
				.getActiveNetworkInfo().isConnected());
	}
	
	public static boolean isLocationServiceEnabled(Context context) {
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		boolean gps_enabled = false, network_enabled = false;
		
		try {
			gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
			//do nothing...
		}
		
		try {
			network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
			//do nothing...
		}
		
		return gps_enabled || network_enabled;
	}
	
	public static double[] getLocation(Context context) {
		double currentLocation[] = new double[2];
		
		LocationManager locManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		
		boolean network_enabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		
		Location location;
		
		if (network_enabled) {
			location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			
			if (location != null) {
				currentLocation[0] = location.getLongitude();
				currentLocation[1] = location.getLatitude();
			}
		}
		return currentLocation;
	}
}
