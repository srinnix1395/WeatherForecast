package com.qtd.weatherforecast.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.qtd.weatherforecast.R;
import com.qtd.weatherforecast.constant.ApiConstant;
import com.qtd.weatherforecast.constant.AppConstant;
import com.qtd.weatherforecast.constant.DatabaseConstant;

import static com.qtd.weatherforecast.AppController.getSharedPreferences;

public class SharedPreUtils {
	
	private SharedPreferences preferences;
	
	/**
	 * @param context your current context.
	 */
	public SharedPreUtils(Context context) {
		this.preferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_APPEND);
	}
	
	private void putStringOrReplace(String key, String value) {
		if (value == null) {
			preferences.edit().remove(key).apply();
		} else {
			preferences.edit().putString(key, value).apply();
		}
	}
	
	private String getStringFromSharedPre(String key, String defValue) {
		if (preferences.contains(key)) {
			return preferences.getString(key, defValue);
		}
		return defValue;
	}
	
	private void putBooleanToShare(String key, boolean value) {
		preferences.edit().putBoolean(key, value).apply();
	}
	
	private Boolean getBooleanFromShare(String key, boolean defValue) {
		if (preferences.contains(key)) {
			return preferences.getBoolean(key, defValue);
		} else {
			return defValue;
		}
	}
	
	private void putIntToShare(String key, int value) {
		preferences.edit().putInt(key, value).apply();
	}
	
	private int getIntFromShare(String key, int defValue) {
		if (preferences.contains(key)) {
			return preferences.getInt(key, defValue);
		} else {
			return defValue;
		}
	}
	
	private void putLongToShare(String key, long value) {
		preferences.edit().putLong(key, value).apply();
	}
	
	private long getLongFromShare(String key, long defValue) {
		if (preferences.contains(key)) {
			return preferences.getLong(key, defValue);
		} else {
			return defValue;
		}
	}
	
	private void putFloatToShare(String key, float value) {
		preferences.edit().putFloat(key, value).apply();
	}
	
	private float getFloatFromShare(String key, float defValue) {
		if (preferences.contains(key)) {
			return preferences.getFloat(key, defValue);
		} else {
			return defValue;
		}
	}
	
	private boolean containKEY(String key) {
		return preferences.contains(key);
	}
	
	public static void putString(String key, String value) {
		getSharedPreferences().putStringOrReplace(key, value);
	}
	
	public static String getString(String key, String defValue) {
		return getSharedPreferences().getStringFromSharedPre(key, defValue);
	}
	
	public static void putInt(String key, int value) {
		getSharedPreferences().putIntToShare(key, value);
	}
	
	public static int getInt(String key, int defValue) {
		return getSharedPreferences().getIntFromShare(key, defValue);
	}
	
	public static void putLong(String key, long value) {
		getSharedPreferences().putLongToShare(key, value);
	}
	
	public static long getLong(String key, int defValue) {
		return getSharedPreferences().getLongFromShare(key, defValue);
	}
	
	public static void putBoolean(String key, boolean value) {
		getSharedPreferences().putBooleanToShare(key, value);
	}
	
	public static boolean getBoolean(String key, boolean defValue) {
		return getSharedPreferences().getBooleanFromShare(key, defValue);
	}
	
	public static void clearData() {
		getSharedPreferences().clearAllData();
	}
	
	private void clearAllData() {
		preferences.edit().clear().apply();
	}
	
	public static void putData(int ID, String name, String coordinate, String timeUpdate) {
		getSharedPreferences().putIntToShare("ID", ID);
		getSharedPreferences().putStringOrReplace(DatabaseConstant.NAME, name);
		getSharedPreferences().putStringOrReplace(ApiConstant.COORDINATE, coordinate);
		getSharedPreferences().putStringOrReplace(DatabaseConstant.TIME_ZONE, timeUpdate);
	}
	
	public static boolean isOpenGuide() {
		return getSharedPreferences().preferences.getBoolean(AppConstant.IS_OPEN_GUIDE, false);
	}
	
	public static void setIsOpenGuide() {
		getSharedPreferences().putBooleanToShare(AppConstant.IS_OPEN_GUIDE, true);
	}
	
	public static String getBackground() {
		return getSharedPreferences().preferences.getString(AppConstant.BACKGROUND, AppConstant.DEFAULT_BACKGROUND);
	}
	
	public static void setBackground(String background) {
		getSharedPreferences().putStringOrReplace(AppConstant.BACKGROUND, background);
	}
}
