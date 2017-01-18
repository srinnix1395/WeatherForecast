package com.qtd.weatherforecast.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;

import com.qtd.weatherforecast.constant.ApiConstant;
import com.qtd.weatherforecast.constant.DatabaseConstant;
import com.qtd.weatherforecast.model.City;
import com.qtd.weatherforecast.model.CityPlus;
import com.qtd.weatherforecast.model.CurrentWeather;
import com.qtd.weatherforecast.model.WeatherDay;
import com.qtd.weatherforecast.model.WeatherHour;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Dell on 4/26/2016.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {
	private static MyDatabaseHelper instance;
	
	private MyDatabaseHelper(Context context) {
		super(context, DatabaseConstant.DATABASE_NAME, null, DatabaseConstant.DATABASE_VERSION);
	}
	
	public static synchronized MyDatabaseHelper getInstance(Context context) {
		if (instance == null) {
			instance = new MyDatabaseHelper(context);
		}
		return instance;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CITY_TABLE = "CREATE TABLE " + DatabaseConstant.TABLE_CITY +
				"(" +
				DatabaseConstant._ID_CITY + " INTEGER PRIMARY KEY , " + // Define a primary key
				DatabaseConstant.NAME + " NVARCHAR(100) ," +
				DatabaseConstant.FULLNAME + " NVARCHAR(100) ," +
				DatabaseConstant.LATITUDE + " NVARCHAR(50) ," +
				DatabaseConstant.LONGITUDE + " NVARCHAR(50))";
		
		String CREATE_CURRENT_WEATHER_TABLE = "CREATE TABLE " + DatabaseConstant.TABLE_CURRENT_WEATHER +
				"(" +
				DatabaseConstant.ID_CURRENT_WEATHER + " INTEGER PRIMARY KEY , " + // Define a primary key
				DatabaseConstant.ICON + " NVARCHAR(100) ," +
				DatabaseConstant.TEMP_C + " INTEGER ," +
				DatabaseConstant.WEATHER + " NVARCHAR(100) ," +
				DatabaseConstant.HUMIDITY + " NVARCHAR(50) ," +
				DatabaseConstant.WIND + " NVARCHAR(50) ," +
				DatabaseConstant.TIME + " NVARCHAR(100) ," +
				DatabaseConstant.UV + " INTEGER ," +
				DatabaseConstant.FEELS_LIKE + " INTEGER ," +
				DatabaseConstant.LAST_UPDATE + " INTEGER ," +
				DatabaseConstant._ID_CITY + " INTEGER)";
		
		String CREATE_HOUR_TABLE = "CREATE TABLE " + DatabaseConstant.TABLE_HOUR +
				"(" +
				DatabaseConstant.ID_HOUR + " INTEGER PRIMARY KEY , " + // Define a primary key
				DatabaseConstant.HOUR + " NVARCHAR(50) ," +
				DatabaseConstant.ICON + " NVARCHAR(100) ," +
				DatabaseConstant.TEMP_C + " INTEGER ," +
				DatabaseConstant.CHANCE_RAIN + " INTEGER ," +
				DatabaseConstant._ID_CITY + " INTEGER ," +
				DatabaseConstant.ORDER + " INTEGER)";
		
		String CREATE_DAY_TABLE = "CREATE TABLE " + DatabaseConstant.TABLE_DAY +
				"(" +
				DatabaseConstant.ID_DAY + " INTEGER PRIMARY KEY , " + // Define a primary key
				DatabaseConstant.ICON + " NVARCHAR(100) ," +
				DatabaseConstant.DAY + " NVARCHAR(100) ," +
				DatabaseConstant.WEATHER + " NVARCHAR(100) ," +
				DatabaseConstant.HIGH_TEMP + " INTEGER ," +
				DatabaseConstant.LOW_TEMP + " INTEGER ," +
				DatabaseConstant._ID_CITY + " INTEGER ," +
				DatabaseConstant.ORDER + " INTEGER)";
		
		db.execSQL(CREATE_CITY_TABLE);
		db.execSQL(CREATE_CURRENT_WEATHER_TABLE);
		db.execSQL(CREATE_HOUR_TABLE);
		db.execSQL(CREATE_DAY_TABLE);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion != newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstant.TABLE_CITY);
			db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstant.TABLE_HOUR);
			db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstant.TABLE_DAY);
			db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstant.TABLE_CURRENT_WEATHER);
			onCreate(db);
		}
	}
	
	@Override
	public void onConfigure(SQLiteDatabase db) {
		super.onConfigure(db);
		db.setForeignKeyConstraintsEnabled(true);
	}
	
	public CityPlus getCityByID(int idCity) {
		CityPlus city = new CityPlus();
		String CITY_SELECT_QUERY =
				String.format("Select %s.%s,%s,%s,%s.%s,%s.%s,%s,%s,%s.%s from %s,%s where "
								+ DatabaseConstant.TABLE_CITY + "." + DatabaseConstant._ID_CITY + "=" + DatabaseConstant.TABLE_CURRENT_WEATHER + "." + DatabaseConstant._ID_CITY + " and "
								+ DatabaseConstant.TABLE_CITY + "." + DatabaseConstant._ID_CITY + "=" + idCity,
						DatabaseConstant.TABLE_CITY, DatabaseConstant._ID_CITY,
						DatabaseConstant.NAME,
						DatabaseConstant.FULLNAME,
						DatabaseConstant.TABLE_CURRENT_WEATHER, DatabaseConstant.WEATHER,
						DatabaseConstant.TABLE_CURRENT_WEATHER, DatabaseConstant.TEMP_C,
						DatabaseConstant.LATITUDE,
						DatabaseConstant.LONGITUDE,
						DatabaseConstant.TABLE_CURRENT_WEATHER, DatabaseConstant.ICON,
						DatabaseConstant.TABLE_CITY,
						DatabaseConstant.TABLE_CURRENT_WEATHER
				);
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(CITY_SELECT_QUERY, null);
		try {
			if (cursor.moveToNext()) {
				int id = cursor.getInt(0);
				String name = cursor.getString(1);
				String fullName = cursor.getString(2);
				String weather = cursor.getString(3);
				int temp = cursor.getInt(4);
				String lat = cursor.getString(5);
				String lon = cursor.getString(6);
				String icon = cursor.getString(7);
				
				city = new CityPlus(id, name, temp, weather, lat + "," + lon, true, fullName, icon);
			}
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			db.close();
		}
		return city;
	}
	
	public ArrayList<City> getAllCities() {
		ArrayList<City> cities = new ArrayList<>();
		String CITY_SELECT_QUERY =
				String.format("Select %s.%s,%s,%s,%s.%s,%s.%s,%s,%s from %s,%s where "
								+ DatabaseConstant.TABLE_CITY + "." + DatabaseConstant._ID_CITY + "=" + DatabaseConstant.TABLE_CURRENT_WEATHER + "." + DatabaseConstant._ID_CITY,
						DatabaseConstant.TABLE_CITY, DatabaseConstant._ID_CITY,
						DatabaseConstant.NAME,
						DatabaseConstant.FULLNAME,
						DatabaseConstant.TABLE_CURRENT_WEATHER, DatabaseConstant.WEATHER,
						DatabaseConstant.TABLE_CURRENT_WEATHER, DatabaseConstant.TEMP_C,
						DatabaseConstant.LATITUDE,
						DatabaseConstant.LONGITUDE,
						DatabaseConstant.TABLE_CITY,
						DatabaseConstant.TABLE_CURRENT_WEATHER
				);
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(CITY_SELECT_QUERY, null);
		try {
			while (cursor.moveToNext()) {
				int id = cursor.getInt(0);
				String name = cursor.getString(1);
				String fullName = cursor.getString(2);
				String weather = cursor.getString(3);
				int temp = cursor.getInt(4);
				String lat = cursor.getString(5);
				String lon = cursor.getString(6);
				
				cities.add(new City(id, name, temp, weather, lat + "," + lon, true, fullName));
			}
		} catch (Exception e) {
			Log.d("Error", "Error while trying to get all cities");
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			db.close();
		}
		return cities;
	}
	
	public ArrayList<WeatherHour> getAllWeatherHours(int id) {
		ArrayList<WeatherHour> weatherHours = new ArrayList<>();
		String HOUR_SELECT_QUERY =
				String.format("Select %s,%s,%s,%s from %s where "
								+ DatabaseConstant.TABLE_HOUR + "." + DatabaseConstant._ID_CITY + "=" + id,
						DatabaseConstant.HOUR,
						DatabaseConstant.ICON,
						DatabaseConstant.TEMP_C,
						DatabaseConstant.CHANCE_RAIN,
						DatabaseConstant.TABLE_HOUR
				);
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(HOUR_SELECT_QUERY, null);
		try {
			while (cursor.moveToNext()) {
				String hour = cursor.getString(0);
				String icon = cursor.getString(1);
				int temp = cursor.getInt(2);
				String rain = cursor.getInt(3) + "%";
				
				weatherHours.add(new WeatherHour(hour, rain, temp, icon));
			}
		} catch (Exception e) {
			Log.d("Error", "Error while trying to get weather hours");
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			db.close();
		}
		return weatherHours;
	}
	
	public CurrentWeather getCurrentWeather(int id) {
		CurrentWeather currentWeather = new CurrentWeather();
		String CURRENT_WEATHER_SELECT_QUERY =
				String.format("Select %s,%s,%s,%s,%s,%s,%s,%s,%s from %s where "
								+ DatabaseConstant.TABLE_CURRENT_WEATHER + "." + DatabaseConstant._ID_CITY + "=" + id,
						DatabaseConstant.ICON,
						DatabaseConstant.TEMP_C,
						DatabaseConstant.WEATHER,
						DatabaseConstant.HUMIDITY,
						DatabaseConstant.WIND,
						DatabaseConstant.TIME,
						DatabaseConstant.UV,
						DatabaseConstant.FEELS_LIKE,
						DatabaseConstant.LAST_UPDATE,
						DatabaseConstant.TABLE_CURRENT_WEATHER
				);
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(CURRENT_WEATHER_SELECT_QUERY, null);
		try {
			if (cursor.moveToNext()) {
				currentWeather.setIcon(cursor.getString(0));
				currentWeather.setTemp(cursor.getInt(1));
				currentWeather.setWeather(cursor.getString(2));
				currentWeather.setHumidity(cursor.getString(3));
				currentWeather.setWind(cursor.getInt(4));
				currentWeather.setTime(cursor.getString(5));
				currentWeather.setUV(cursor.getInt(6));
				currentWeather.setFeelsLike(cursor.getInt(7));
				currentWeather.setLastUpdate(cursor.getLong(8));
			}
		} catch (Exception e) {
			Log.d("Error", "Error while trying to get current weather ");
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			db.close();
		}
		return currentWeather;
	}
	
	public int[] getCurrentTemp(int id) {
		int[] temp = new int[2];
		String CURRENT_TEMP_SELECT_QUERY =
				String.format("Select %s,%s from %s where "
								+ DatabaseConstant.TABLE_CURRENT_WEATHER + "." + DatabaseConstant._ID_CITY + "=" + id,
						DatabaseConstant.TEMP_C,
						DatabaseConstant.FEELS_LIKE,
						DatabaseConstant.TABLE_CURRENT_WEATHER
				);
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(CURRENT_TEMP_SELECT_QUERY, null);
		try {
			if (cursor.moveToNext()) {
				temp[0] = cursor.getInt(0);
				temp[1] = cursor.getInt(1);
			}
		} catch (Exception e) {
			Log.d("Error", "Error while trying to get current temp ");
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			db.close();
		}
		return temp;
	}
	
	public ArrayList<WeatherDay> getAllWeatherDays(int id) {
		ArrayList<WeatherDay> weatherDays = new ArrayList<>();
		String DAY_SELECT_QUERY =
				String.format("Select %s,%s,%s,%s,%s from %s where "
								+ DatabaseConstant.TABLE_DAY + "." + DatabaseConstant._ID_CITY + "=" + id,
						DatabaseConstant.DAY,
						DatabaseConstant.ICON,
						DatabaseConstant.WEATHER,
						DatabaseConstant.HIGH_TEMP,
						DatabaseConstant.LOW_TEMP,
						DatabaseConstant.TABLE_DAY
				);
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(DAY_SELECT_QUERY, null);
		try {
			while (cursor.moveToNext()) {
				String day = cursor.getString(0);
				String icon = cursor.getString(1);
				String weather = cursor.getString(2);
				int highTemp = cursor.getInt(3);
				int lowTemp = cursor.getInt(4);
				
				weatherDays.add(new WeatherDay(day, weather, highTemp, lowTemp, icon));
			}
		} catch (Exception e) {
			Log.d("Error", "Error while trying to get weather days");
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			db.close();
		}
		return weatherDays;
	}
	
	public long insertCity(String json) {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		long id = 0;
		try {
			JSONObject currentWeather = new JSONObject(json)
					.getJSONObject(ApiConstant.CURRENT_OBSERVATION)
					.getJSONObject(ApiConstant.DISPLAY_LOCATION);
			
			ContentValues contentValues = new ContentValues();
			contentValues.put(DatabaseConstant.NAME, currentWeather.getString(ApiConstant.CITY));
			contentValues.put(DatabaseConstant.FULLNAME, currentWeather.getString(ApiConstant.FULL_NAME));
			contentValues.put(DatabaseConstant.LATITUDE, currentWeather.getString(ApiConstant.LATITUDE));
			contentValues.put(DatabaseConstant.LONGITUDE, currentWeather.getString(ApiConstant.LONGITUDE));
			
			id = db.insertOrThrow(DatabaseConstant.TABLE_CITY, null, contentValues);
			Log.d("id", String.valueOf(id));
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.d("ERROR: ", "error while trying insert a city");
		} finally {
			db.endTransaction();
			db.close();
		}
		return id;
	}
	
	public void insertWeatherHour(ArrayList<WeatherHour> weatherHourArrayList, int idCity) {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {
			ContentValues contentValues = new ContentValues();
			int i = 0;
			for (WeatherHour hour : weatherHourArrayList) {
				contentValues.clear();
				contentValues.put(DatabaseConstant.HOUR, hour.getHour());
				contentValues.put(DatabaseConstant.ICON, hour.getIcon());
				contentValues.put(DatabaseConstant.TEMP_C, hour.getTemp());
				contentValues.put(DatabaseConstant.CHANCE_RAIN, Integer.parseInt(hour.getRain().replace('%', ' ').trim()));
				contentValues.put(DatabaseConstant._ID_CITY, idCity);
				contentValues.put(DatabaseConstant.ORDER, i);
				
				long result = db.insertOrThrow(DatabaseConstant.TABLE_HOUR, null, contentValues);
				Log.d("result insert hour", String.valueOf(result));
				i++;
			}
			
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.d("error", "error while trying insert weather hour");
		} finally {
			db.endTransaction();
			db.close();
		}
	}
	
	public void insertCurrentWeather(CurrentWeather currentWeather, int idCity) {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put(DatabaseConstant.ICON, currentWeather.getIcon());
			contentValues.put(DatabaseConstant.TEMP_C, currentWeather.getTemp());
			contentValues.put(DatabaseConstant.WEATHER, currentWeather.getWeather());
			contentValues.put(DatabaseConstant.HUMIDITY, currentWeather.getHumidity());
			contentValues.put(DatabaseConstant.WIND, currentWeather.getWind());
			contentValues.put(DatabaseConstant.UV, currentWeather.getUV());
			contentValues.put(DatabaseConstant.FEELS_LIKE, currentWeather.getFeelsLike());
			contentValues.put(DatabaseConstant.TIME, currentWeather.getTime());
			contentValues.put(DatabaseConstant.LAST_UPDATE, currentWeather.getLastUpdate());
			contentValues.put(DatabaseConstant._ID_CITY, idCity);
			
			long result = db.insertOrThrow(DatabaseConstant.TABLE_CURRENT_WEATHER, null, contentValues);
			Log.d("result insert cWeather", String.valueOf(result));
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.d("error", "error while trying insert current weather");
		} finally {
			db.endTransaction();
			db.close();
		}
	}
	
	
	public void insertWeatherDay(ArrayList<WeatherDay> dayArrayList, int idCity) {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {
			ContentValues contentValues = new ContentValues();
			int i = 0;
			for (WeatherDay day : dayArrayList) {
				contentValues.clear();
				contentValues.put(DatabaseConstant.DAY, day.getDay());
				contentValues.put(DatabaseConstant.WEATHER, day.getWeather());
				contentValues.put(DatabaseConstant.HIGH_TEMP, day.getHighTemp());
				contentValues.put(DatabaseConstant.LOW_TEMP, day.getLowTemp());
				contentValues.put(DatabaseConstant._ID_CITY, idCity);
				contentValues.put(DatabaseConstant.ICON, day.getIcon());
				contentValues.put(DatabaseConstant.ORDER, i);
				
				long result = db.insertOrThrow(DatabaseConstant.TABLE_DAY, null, contentValues);
				Log.d("result insert day", String.valueOf(result));
				i++;
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.d("error", "error while trying insert weather day");
		} finally {
			db.endTransaction();
			db.close();
		}
	}
	
	public void updateCurrentWeather(CurrentWeather currentWeather, int idCity) {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put(DatabaseConstant.ICON, currentWeather.getIcon());
			contentValues.put(DatabaseConstant.TEMP_C, currentWeather.getTemp());
			contentValues.put(DatabaseConstant.WEATHER, currentWeather.getWeather());
			contentValues.put(DatabaseConstant.HUMIDITY, currentWeather.getHumidity());
			contentValues.put(DatabaseConstant.WIND, currentWeather.getWind());
			contentValues.put(DatabaseConstant.UV, currentWeather.getUV());
			contentValues.put(DatabaseConstant.FEELS_LIKE, currentWeather.getFeelsLike());
			contentValues.put(DatabaseConstant.TIME, currentWeather.getTime());
			contentValues.put(DatabaseConstant.LAST_UPDATE, currentWeather.getLastUpdate());
			contentValues.put(DatabaseConstant._ID_CITY, idCity);
			
			long result = db.update(DatabaseConstant.TABLE_CURRENT_WEATHER, contentValues, DatabaseConstant._ID_CITY + "=" + idCity, null);
			Log.d("result update cWeather", String.valueOf(result));
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.d("error", "error while trying update current weather");
		} finally {
			db.endTransaction();
			db.close();
		}
	}
	
	public void updateWeatherDay(ArrayList<WeatherDay> dayArrayList, int idCity) {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {
			ContentValues contentValues = new ContentValues();
			int i = 0;
			for (WeatherDay weatherDay : dayArrayList) {
				contentValues.clear();
				contentValues.put(DatabaseConstant.DAY, weatherDay.getDay());
				contentValues.put(DatabaseConstant.WEATHER, weatherDay.getWeather());
				contentValues.put(DatabaseConstant.HIGH_TEMP, weatherDay.getHighTemp());
				contentValues.put(DatabaseConstant.LOW_TEMP, weatherDay.getLowTemp());
				contentValues.put(DatabaseConstant.ICON, weatherDay.getIcon());
				
				long result = db.update(DatabaseConstant.TABLE_DAY, contentValues, DatabaseConstant._ID_CITY + "=" + idCity + " AND " + DatabaseConstant.ORDER + "=" + i, null);
				Log.d("result update day", String.valueOf(result));
				i++;
			}
			
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.d("error", "error while trying update weather day");
		} finally {
			db.endTransaction();
			db.close();
		}
	}
	
	public void updateWeatherHour(ArrayList<WeatherHour> weatherHourArrayList, int idCity) {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {
			ContentValues contentValues = new ContentValues();
			for (int i = 0; i < weatherHourArrayList.size(); i++) {
				contentValues.clear();
				contentValues.put(DatabaseConstant.HOUR, weatherHourArrayList.get(i).getHour());
				contentValues.put(DatabaseConstant.ICON, weatherHourArrayList.get(i).getIcon());
				contentValues.put(DatabaseConstant.TEMP_C, weatherHourArrayList.get(i).getTemp());
				contentValues.put(DatabaseConstant.CHANCE_RAIN, Integer.parseInt(weatherHourArrayList.get(i).getRain().replace('%', ' ').trim()));
				
				long result = db.update(DatabaseConstant.TABLE_HOUR, contentValues, DatabaseConstant._ID_CITY + "=" + idCity + " AND " + DatabaseConstant.ORDER + "=" + i, null);
				Log.d("result update hour", String.valueOf(result));
			}
			
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.d("error", "error while trying update weather hour");
		} finally {
			db.endTransaction();
			db.close();
		}
	}
	
	public void deleteCity(int id) {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {
			db.delete(DatabaseConstant.TABLE_CURRENT_WEATHER, DatabaseConstant._ID_CITY + "=" + id, null);
			db.delete(DatabaseConstant.TABLE_DAY, DatabaseConstant._ID_CITY + "=" + id, null);
			db.delete(DatabaseConstant.TABLE_HOUR, DatabaseConstant._ID_CITY + "=" + id, null);
			db.delete(DatabaseConstant.TABLE_CITY, DatabaseConstant._ID_CITY + "=" + id, null);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.d("Error: ", "error while trying delete a city");
		} finally {
			db.endTransaction();
			db.close();
		}
	}
	
	public City getFirstCity() {
		SQLiteDatabase db = getReadableDatabase();
		String sql = String.format("Select %s,%s,%s,%s,%s from %s",
				DatabaseConstant._ID_CITY,
				DatabaseConstant.NAME,
				DatabaseConstant.FULLNAME,
				DatabaseConstant.LATITUDE,
				DatabaseConstant.LONGITUDE,
				DatabaseConstant.TABLE_CITY
		);
		Cursor cursor = db.rawQuery(sql, null);
		City city = new City();
		if (cursor.moveToNext()) {
			city.setId(cursor.getInt(0));
			city.setName(cursor.getString(1));
			city.setFullName(cursor.getString(2));
			String latitude = cursor.getString(3);
			String longitude = cursor.getString(4);
			city.setCoordinate(latitude + "," + longitude);
		}
		cursor.close();
		db.close();
		return city;
	}
	
	public void updateData(int idCity, Bundle bundle) throws JSONException {
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {
			//update current weather
			ContentValues contentValues = new ContentValues();
			
			JSONObject jsonObject = new JSONObject(bundle.getString(ApiConstant.CONDITIONS))
					.getJSONObject(ApiConstant.CURRENT_OBSERVATION);
			
			contentValues.put(DatabaseConstant.ICON, jsonObject.getString(ApiConstant.ICON_URL));
			contentValues.put(DatabaseConstant.TEMP_C, jsonObject.getInt(ApiConstant.TEMP_C));
			contentValues.put(DatabaseConstant.WEATHER, jsonObject.getString(ApiConstant.WEATHER));
			contentValues.put(DatabaseConstant.HUMIDITY, jsonObject.getString(ApiConstant.RELATIVE_HUMIDITY));
			contentValues.put(DatabaseConstant.WIND, jsonObject.getInt(ApiConstant.WIND_GUST));
			contentValues.put(DatabaseConstant.UV, jsonObject.getInt(ApiConstant.UV));
			contentValues.put(DatabaseConstant.FEELS_LIKE, jsonObject.getInt(ApiConstant.FEELS_LIKE_C));
			contentValues.put(DatabaseConstant.TIME, jsonObject.getString(ApiConstant.LOCAL_TZ_OFFSET));
			contentValues.put(DatabaseConstant.LAST_UPDATE, System.currentTimeMillis());
			
			long result = db.update(DatabaseConstant.TABLE_CURRENT_WEATHER, contentValues, DatabaseConstant._ID_CITY + "=" + idCity, null);
			Log.d("result update cWeather", String.valueOf(result));
			
			//update weather hour
			JSONArray forecast = new JSONObject(bundle.getString(ApiConstant.HOURLY))
					.getJSONArray(ApiConstant.HOURLY_FORECAST);
			
			for (int i = 0; i < 24; i++) {
				contentValues.clear();
				JSONObject hour = forecast.getJSONObject(i);
				
				contentValues.put(DatabaseConstant.HOUR, hour.getString(ApiConstant.FCTIME) + ":00");
				contentValues.put(DatabaseConstant.ICON, hour.getString(ApiConstant.ICON_URL));
				contentValues.put(DatabaseConstant.TEMP_C, hour.getJSONObject(ApiConstant.TEMP)
						.getInt(ApiConstant.METRIC));
				contentValues.put(DatabaseConstant.CHANCE_RAIN, hour.getInt(ApiConstant.POP));
				
				result = db.update(DatabaseConstant.TABLE_HOUR, contentValues, DatabaseConstant._ID_CITY + "=" + idCity + " AND " + DatabaseConstant.ORDER + "=" + i, null);
				
				Log.d("result update hour", String.valueOf(result));
			}
			
			//update weather day
			forecast = new JSONObject(bundle.getString(ApiConstant.FORECAST10DAY))
					.getJSONObject(ApiConstant.FORECAST)
					.getJSONObject(ApiConstant.SIMPLE_FORECAST)
					.getJSONArray(ApiConstant.FORECAST_DAY);
			for (int i = 1; i < 7; i++) {
				contentValues.clear();
				JSONObject object = forecast.getJSONObject(i);
				
				contentValues.put(DatabaseConstant.DAY, object.getJSONObject(ApiConstant.DATE)
						.getString(ApiConstant.WEEKDAY).substring(5));
				contentValues.put(DatabaseConstant.WEATHER, object.getString(ApiConstant.CONDITIONS));
				contentValues.put(DatabaseConstant.HIGH_TEMP, object.getJSONObject(ApiConstant.HIGH)
						.getInt(ApiConstant.CELCIUS));
				contentValues.put(DatabaseConstant.LOW_TEMP, object.getJSONObject(ApiConstant.LOW)
						.getInt(ApiConstant.CELCIUS));
				contentValues.put(DatabaseConstant.ICON, object.getString(ApiConstant.ICON_URL));
				
				result = db.update(DatabaseConstant.TABLE_DAY, contentValues, DatabaseConstant._ID_CITY + "=" + idCity + " AND " + DatabaseConstant.ORDER + "=" + (i - 1), null);
				Log.d("result update day", String.valueOf(result));
			}
			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			db.close();
		}
	}
	
	public City insertData(int idCity, Bundle bundle) throws JSONException {
		City city = new City();
				
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {
			//insert current weather
			JSONObject jsonObject = new JSONObject(bundle.getString(ApiConstant.CONDITIONS))
					.getJSONObject(ApiConstant.CURRENT_OBSERVATION);
			
			ContentValues contentValues = new ContentValues();
			contentValues.put(DatabaseConstant.ICON, jsonObject.getString(ApiConstant.ICON_URL));
			contentValues.put(DatabaseConstant.TEMP_C, jsonObject.getInt(ApiConstant.TEMP_C));
			contentValues.put(DatabaseConstant.WEATHER, jsonObject.getString(ApiConstant.WEATHER));
			contentValues.put(DatabaseConstant.HUMIDITY, jsonObject.getString(ApiConstant.RELATIVE_HUMIDITY));
			contentValues.put(DatabaseConstant.WIND, jsonObject.getInt(ApiConstant.WIND_GUST));
			contentValues.put(DatabaseConstant.UV, jsonObject.getInt(ApiConstant.UV));
			contentValues.put(DatabaseConstant.FEELS_LIKE, jsonObject.getInt(ApiConstant.FEELS_LIKE_C));
			contentValues.put(DatabaseConstant.TIME, jsonObject.getString(ApiConstant.LOCAL_TZ_OFFSET));
			contentValues.put(DatabaseConstant.LAST_UPDATE, System.currentTimeMillis());
			contentValues.put(DatabaseConstant._ID_CITY, idCity);
			
			long result = db.insertOrThrow(DatabaseConstant.TABLE_CURRENT_WEATHER, null, contentValues);
			Log.d("result insert cWeather", String.valueOf(result));
			
			JSONObject displayLocation = jsonObject.getJSONObject(ApiConstant.DISPLAY_LOCATION);
			city.setId((int) result);
			city.setName(displayLocation.getString(ApiConstant.FULL_NAME));
			city.setCoordinate(displayLocation.getString(ApiConstant.LATITUDE) + "," + displayLocation.getString(ApiConstant.LONGITUDE));
			city.setTimeZone(jsonObject.getString(ApiConstant.LOCAL_TZ_OFFSET));
			
			//insert weather hour
			JSONArray forecast = new JSONObject(bundle.getString(ApiConstant.HOURLY))
					.getJSONArray(ApiConstant.HOURLY_FORECAST);
			for (int i = 0; i < 24; i++) {
				contentValues.clear();
				JSONObject hour = forecast.getJSONObject(i);
				
				contentValues.put(DatabaseConstant.HOUR, hour.getString(ApiConstant.FCTIME) + ":00");
				contentValues.put(DatabaseConstant.ICON, hour.getString(ApiConstant.ICON_URL));
				contentValues.put(DatabaseConstant.TEMP_C, hour.getJSONObject(ApiConstant.TEMP)
						.getInt(ApiConstant.METRIC));
				contentValues.put(DatabaseConstant.CHANCE_RAIN, hour.getInt(ApiConstant.POP));
				contentValues.put(DatabaseConstant._ID_CITY, idCity);
				contentValues.put(DatabaseConstant.ORDER, i);
				
				result = db.insertOrThrow(DatabaseConstant.TABLE_HOUR, null, contentValues);
				Log.d("result insert hour", String.valueOf(result));
			}
			
			//insert weather day
			forecast = new JSONObject(bundle.getString(ApiConstant.FORECAST10DAY))
					.getJSONObject(ApiConstant.FORECAST)
					.getJSONObject(ApiConstant.SIMPLE_FORECAST)
					.getJSONArray(ApiConstant.FORECAST_DAY);
			for (int i = 1; i < 7; i++) {
				contentValues.clear();
				JSONObject object = forecast.getJSONObject(i);
				
				contentValues.put(DatabaseConstant.DAY, object.getJSONObject(ApiConstant.DATE)
						.getString(ApiConstant.WEEKDAY).substring(5));
				contentValues.put(DatabaseConstant.WEATHER, object.getString(ApiConstant.CONDITIONS));
				contentValues.put(DatabaseConstant.HIGH_TEMP, object.getJSONObject(ApiConstant.HIGH)
						.getInt(ApiConstant.CELCIUS));
				contentValues.put(DatabaseConstant.LOW_TEMP, object.getJSONObject(ApiConstant.LOW)
						.getInt(ApiConstant.CELCIUS));
				contentValues.put(DatabaseConstant._ID_CITY, idCity);
				contentValues.put(DatabaseConstant.ICON, object.getString(ApiConstant.ICON_URL));
				contentValues.put(DatabaseConstant.ORDER, i - 1);
				
				result = db.insertOrThrow(DatabaseConstant.TABLE_DAY, null, contentValues);
				Log.d("result insert day", String.valueOf(result));
			}
			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			db.close();
		}
		return city;
	}
}


