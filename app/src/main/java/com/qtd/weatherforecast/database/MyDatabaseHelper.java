package com.qtd.weatherforecast.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.qtd.weatherforecast.constant.DatabaseConstant;
import com.qtd.weatherforecast.model.City;
import com.qtd.weatherforecast.model.CityPlus;
import com.qtd.weatherforecast.model.CurrentWeather;
import com.qtd.weatherforecast.model.WeatherDay;
import com.qtd.weatherforecast.model.WeatherHour;

import java.util.ArrayList;

/**
 * Created by Dell on 4/26/2016.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {
    //Database info
    private static final String DATABASE_NAME = "dbWeatherForecast";
    private static final int DATABASE_VERSION = 1;

    //Table names
    private static final String TABLE_HOUR = "tbHour";
    private static final String TABLE_DAY = "tbDay";
    private static final String TABLE_CITY = "tbCity";
    private static final String TABLE_CURRENT_WEATHER = "tbCurrentWeather";

    private static MyDatabaseHelper instance;

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static MyDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MyDatabaseHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CITY_TABLE = "CREATE TABLE " + TABLE_CITY +
                "(" +
                DatabaseConstant.ID_CITY + " INTEGER PRIMARY KEY , " + // Define a primary key
                DatabaseConstant.NAME + " NVARCHAR(100) ," +
                DatabaseConstant.FULLNAME + " NVARCHAR(100) ," +
                DatabaseConstant.LATITUDE + " NVARCHAR(50) ," +
                DatabaseConstant.LONGITUDE + " NVARCHAR(50))";

        String CREATE_CURRENT_WEATHER_TABLE = "CREATE TABLE " + TABLE_CURRENT_WEATHER +
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
                DatabaseConstant.ID_CITY + " INTEGER)";

        String CREATE_HOUR_TABLE = "CREATE TABLE " + TABLE_HOUR +
                "(" +
                DatabaseConstant.ID_HOUR + " INTEGER PRIMARY KEY , " + // Define a primary key
                DatabaseConstant.HOUR + " NVARCHAR(50) ," +
                DatabaseConstant.ICON + " NVARCHAR(100) ," +
                DatabaseConstant.TEMP_C + " INTEGER ," +
                DatabaseConstant.CHANCE_RAIN + " INTEGER ," +
                DatabaseConstant.ID_CITY + " INTEGER ," +
                DatabaseConstant.ORDER + " INTEGER)";

        String CREATE_DAY_TABLE = "CREATE TABLE " + TABLE_DAY +
                "(" +
                DatabaseConstant.ID_DAY + " INTEGER PRIMARY KEY , " + // Define a primary key
                DatabaseConstant.ICON + " NVARCHAR(100) ," +
                DatabaseConstant.DAY + " NVARCHAR(100) ," +
                DatabaseConstant.WEATHER + " NVARCHAR(100) ," +
                DatabaseConstant.HIGH_TEMP + " INTEGER ," +
                DatabaseConstant.LOW_TEMP + " INTEGER ," +
                DatabaseConstant.ID_CITY + " INTEGER ," +
                DatabaseConstant.ORDER + " INTEGER)";

        db.execSQL(CREATE_CITY_TABLE);
        db.execSQL(CREATE_CURRENT_WEATHER_TABLE);
        db.execSQL(CREATE_HOUR_TABLE);
        db.execSQL(CREATE_DAY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITY);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_HOUR);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DAY);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CURRENT_WEATHER);
            onCreate(db);
        }
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    public synchronized CityPlus getCityByID(int idCity) {
        CityPlus city = new CityPlus();
        String CITY_SELECT_QUERY =
                String.format("Select %s.%s,%s,%s,%s.%s,%s.%s,%s,%s,%s.%s from %s,%s where "
                                + TABLE_CITY + "." + DatabaseConstant.ID_CITY + "=" + TABLE_CURRENT_WEATHER + "." + DatabaseConstant.ID_CITY + " and "
                                + TABLE_CITY + "." + DatabaseConstant.ID_CITY + "=" + idCity,
                        TABLE_CITY, DatabaseConstant.ID_CITY,
                        DatabaseConstant.NAME,
                        DatabaseConstant.FULLNAME,
                        TABLE_CURRENT_WEATHER, DatabaseConstant.WEATHER,
                        TABLE_CURRENT_WEATHER, DatabaseConstant.TEMP_C,
                        DatabaseConstant.LATITUDE,
                        DatabaseConstant.LONGITUDE,
                        TABLE_CURRENT_WEATHER, DatabaseConstant.ICON,
                        TABLE_CITY,
                        TABLE_CURRENT_WEATHER
                );
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(CITY_SELECT_QUERY, null);
        if (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(DatabaseConstant.NAME));
            String fullName = cursor.getString(cursor.getColumnIndex(DatabaseConstant.FULLNAME));
            String weather = cursor.getString(cursor.getColumnIndex(DatabaseConstant.WEATHER));
            int temp = cursor.getInt(cursor.getColumnIndex(DatabaseConstant.TEMP_C));
            String lat = cursor.getString(cursor.getColumnIndex(DatabaseConstant.LATITUDE));
            String lon = cursor.getString(cursor.getColumnIndex(DatabaseConstant.LONGITUDE));
            int id = cursor.getInt(cursor.getColumnIndex(DatabaseConstant.ID_CITY));
            String icon = cursor.getString(cursor.getColumnIndex(DatabaseConstant.ICON));

            city = new CityPlus(id, name, temp, weather, lat + "," + lon, true,fullName, icon);
        }
        cursor.close();
        return city;
    }

    public synchronized ArrayList<City> getAllCities() {
        ArrayList<City> cities = new ArrayList<>();
        String CITY_SELECT_QUERY =
                String.format("Select %s.%s,%s,%s,%s.%s,%s.%s,%s,%s from %s,%s where "
                        + TABLE_CITY + "." + DatabaseConstant.ID_CITY + "=" + TABLE_CURRENT_WEATHER + "." + DatabaseConstant.ID_CITY,
                        TABLE_CITY, DatabaseConstant.ID_CITY,
                        DatabaseConstant.NAME,
                        DatabaseConstant.FULLNAME,
                        TABLE_CURRENT_WEATHER, DatabaseConstant.WEATHER,
                        TABLE_CURRENT_WEATHER, DatabaseConstant.TEMP_C,
                        DatabaseConstant.LATITUDE,
                        DatabaseConstant.LONGITUDE,
                        TABLE_CITY,
                        TABLE_CURRENT_WEATHER
                        );
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(CITY_SELECT_QUERY, null);
        try {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(DatabaseConstant.NAME));
                String fullName = cursor.getString(cursor.getColumnIndex(DatabaseConstant.FULLNAME));
                String weather = cursor.getString(cursor.getColumnIndex(DatabaseConstant.WEATHER));
                int temp = cursor.getInt(cursor.getColumnIndex(DatabaseConstant.TEMP_C));
                String lat = cursor.getString(cursor.getColumnIndex(DatabaseConstant.LATITUDE));
                String lon = cursor.getString(cursor.getColumnIndex(DatabaseConstant.LONGITUDE));
                int id = cursor.getInt(cursor.getColumnIndex(DatabaseConstant.ID_CITY));

                City city = new City(id, name, temp, weather, lat + "," + lon, true, fullName);
                cities.add(city);
            }
        } catch (Exception e) {
            Log.d("Error", "Error while trying to get all cities");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return cities;
    }

    public ArrayList<WeatherHour> getAllWeatherHours(int id) {
        ArrayList<WeatherHour> weatherHours = new ArrayList<>();
        String HOUR_SELECT_QUERY =
                String.format("Select %s,%s,%s,%s from %s where "
                                + TABLE_HOUR + "." + DatabaseConstant.ID_CITY + "=" +id,
                        DatabaseConstant.HOUR,
                        DatabaseConstant.ICON,
                        DatabaseConstant.TEMP_C,
                        DatabaseConstant.CHANCE_RAIN,
                        TABLE_HOUR
                        );
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(HOUR_SELECT_QUERY, null);
        try {
            while (cursor.moveToNext()){
                String hour = cursor.getString(cursor.getColumnIndex(DatabaseConstant.HOUR));
                String icon = cursor.getString(cursor.getColumnIndex(DatabaseConstant.ICON));
                String rain = cursor.getInt(cursor.getColumnIndex(DatabaseConstant.CHANCE_RAIN)) + "%";
                int temp = cursor.getInt(cursor.getColumnIndex(DatabaseConstant.TEMP_C));

                WeatherHour weatherHour = new WeatherHour(hour, rain, temp, icon);
                weatherHours.add(weatherHour);
            }
        } catch (Exception e) {
            Log.d("Error", "Error while trying to get weather hours");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return weatherHours;
    }

    public CurrentWeather getCurrentWeather(int id) {
        CurrentWeather currentWeather = new CurrentWeather();
        String CURRENT_WEATHER_SELECT_QUERY =
                String.format("Select %s,%s,%s,%s,%s,%s,%s,%s from %s where "
                                + TABLE_CURRENT_WEATHER + "." + DatabaseConstant.ID_CITY + "=" +id,
                        DatabaseConstant.ICON,
                        DatabaseConstant.TEMP_C,
                        DatabaseConstant.WEATHER,
                        DatabaseConstant.HUMIDITY,
                        DatabaseConstant.WIND,
                        DatabaseConstant.TIME,
                        DatabaseConstant.UV,
                        DatabaseConstant.FEELS_LIKE,
                        TABLE_CURRENT_WEATHER
                );
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(CURRENT_WEATHER_SELECT_QUERY, null);
        try {
            while (cursor.moveToNext()){
                currentWeather.setIcon(cursor.getString(cursor.getColumnIndex(DatabaseConstant.ICON)));
                currentWeather.setTemp(cursor.getInt(cursor.getColumnIndex(DatabaseConstant.TEMP_C)));
                currentWeather.setWeather(cursor.getString(cursor.getColumnIndex(DatabaseConstant.WEATHER)));
                currentWeather.setHumidity(cursor.getString(cursor.getColumnIndex(DatabaseConstant.HUMIDITY)));
                currentWeather.setWind(cursor.getInt(cursor.getColumnIndex(DatabaseConstant.WIND)));
                currentWeather.setUV(cursor.getInt(cursor.getColumnIndex(DatabaseConstant.UV)));
                currentWeather.setFeelslike(cursor.getInt(cursor.getColumnIndex(DatabaseConstant.FEELS_LIKE)));
                currentWeather.setTime(cursor.getString(cursor.getColumnIndex(DatabaseConstant.TIME)));
            }
        } catch (Exception e) {
            Log.d("Error", "Error while trying to get current weather ");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return currentWeather;
    }

    public ArrayList<WeatherDay> getAllWeatherDays(int id) {
        ArrayList<WeatherDay> weatherDays = new ArrayList<>();
        String DAY_SELECT_QUERY =
                String.format("Select %s,%s,%s,%s,%s from %s where "
                                + TABLE_DAY + "." + DatabaseConstant.ID_CITY + "=" +id,
                        DatabaseConstant.DAY,
                        DatabaseConstant.ICON,
                        DatabaseConstant.WEATHER,
                        DatabaseConstant.HIGH_TEMP,
                        DatabaseConstant.LOW_TEMP,
                        TABLE_DAY
                );
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(DAY_SELECT_QUERY, null);
        try {
            while (cursor.moveToNext()) {
                String icon = cursor.getString(cursor.getColumnIndex(DatabaseConstant.ICON));
                String day = cursor.getString(cursor.getColumnIndex(DatabaseConstant.DAY));
                String weather = cursor.getString(cursor.getColumnIndex(DatabaseConstant.WEATHER));
                int highTemp = cursor.getInt(cursor.getColumnIndex(DatabaseConstant.HIGH_TEMP));
                int lowTemp = cursor.getInt(cursor.getColumnIndex(DatabaseConstant.LOW_TEMP));

                WeatherDay weatherDay = new WeatherDay(day, weather, highTemp,lowTemp, icon);
                weatherDays.add(weatherDay);
            }
        } catch (Exception e) {
            Log.d("Error", "Error while trying to get weather days");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return weatherDays;
    }

    public long insertCity(City city) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        long id = 0;
        try {

            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseConstant.NAME, city.getName());
            contentValues.put(DatabaseConstant.FULLNAME, city.getFullName());
            String[] temp = city.getCoordinate().split(",");
            contentValues.put(DatabaseConstant.LATITUDE, temp[0]);
            contentValues.put(DatabaseConstant.LONGITUDE, temp[1]);

            id = db.insertOrThrow(TABLE_CITY, null, contentValues);
            Log.d("id", String.valueOf(id));
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("ERROR: ", "error while trying insert a city");
        } finally {
            db.endTransaction();
        }
        return id;
    }

    public void insertWeatherHour(WeatherHour weatherHour, int idCity, int order) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseConstant.HOUR, weatherHour.getHour());
            contentValues.put(DatabaseConstant.ICON, weatherHour.getIcon());
            contentValues.put(DatabaseConstant.TEMP_C, weatherHour.getTemp());
            contentValues.put(DatabaseConstant.CHANCE_RAIN, Integer.parseInt(weatherHour.getRain().replace('%', ' ').trim()));
            contentValues.put(DatabaseConstant.ID_CITY, idCity);
            contentValues.put(DatabaseConstant.ORDER, order);

            long result = db.insertOrThrow(TABLE_HOUR, null, contentValues);
            Log.d("result insert hour", String.valueOf(result));
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("error", "error while trying insert weather hour");
        } finally {
            db.endTransaction();
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
            contentValues.put(DatabaseConstant.FEELS_LIKE, currentWeather.getFeelslike());
            contentValues.put(DatabaseConstant.TIME, currentWeather.getTime());
            contentValues.put(DatabaseConstant.ID_CITY, idCity);

            long result = db.insertOrThrow(TABLE_CURRENT_WEATHER, null, contentValues);
            Log.d("result insert cWeather", String.valueOf(result));
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("error", "error while trying insert current weather");
        } finally {
            db.endTransaction();
        }
    }

    public void insertWeatherDay(WeatherDay weatherDay, int idCity, int order) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseConstant.DAY, weatherDay.getDay());
            contentValues.put(DatabaseConstant.WEATHER, weatherDay.getWeather());
            contentValues.put(DatabaseConstant.HIGH_TEMP, weatherDay.getHighTemp());
            contentValues.put(DatabaseConstant.LOW_TEMP, weatherDay.getLowTemp());
            contentValues.put(DatabaseConstant.ID_CITY, idCity);
            contentValues.put(DatabaseConstant.ICON, weatherDay.getIcon());
            contentValues.put(DatabaseConstant.ORDER, order);

            long result = db.insertOrThrow(TABLE_DAY, null, contentValues);
            Log.d("result insert day", String.valueOf(result));
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("error", "error while trying insert weather day");
        } finally {
            db.endTransaction();
        }
    }

    public synchronized void updateCurrentWeather(CurrentWeather currentWeather, int idCity) {
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
            contentValues.put(DatabaseConstant.FEELS_LIKE, currentWeather.getFeelslike());
            contentValues.put(DatabaseConstant.TIME, currentWeather.getTime());
            contentValues.put(DatabaseConstant.ID_CITY, idCity);

            long result = db.update(TABLE_CURRENT_WEATHER,contentValues, DatabaseConstant.ID_CITY + "=" + idCity, null);
            Log.d("result update cWeather", String.valueOf(result));
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("error", "error while trying update current weather");
        } finally {
            db.endTransaction();
        }
    }

    public synchronized void  updateWeatherDay(WeatherDay weatherDay, int idCity, int order) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseConstant.DAY, weatherDay.getDay());
            contentValues.put(DatabaseConstant.WEATHER, weatherDay.getWeather());
            contentValues.put(DatabaseConstant.HIGH_TEMP, weatherDay.getHighTemp());
            contentValues.put(DatabaseConstant.LOW_TEMP, weatherDay.getLowTemp());
            contentValues.put(DatabaseConstant.ICON, weatherDay.getIcon());

            long result = db.update(TABLE_DAY, contentValues, DatabaseConstant.ID_CITY + "=" + idCity + " AND " + DatabaseConstant.ORDER + "=" + order, null);
            Log.d("result update day", String.valueOf(result));
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("error", "error while trying update weather day");
        } finally {
            db.endTransaction();
        }
    }

    public synchronized void updateWeatherHour(WeatherHour weatherHour, int idCity, int order) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseConstant.HOUR, weatherHour.getHour());
            contentValues.put(DatabaseConstant.ICON, weatherHour.getIcon());
            contentValues.put(DatabaseConstant.TEMP_C, weatherHour.getTemp());
            contentValues.put(DatabaseConstant.CHANCE_RAIN, Integer.parseInt(weatherHour.getRain().replace('%', ' ').trim()));

            long result = db.update(TABLE_HOUR, contentValues, DatabaseConstant.ID_CITY + "=" + idCity + " AND " + DatabaseConstant.ORDER + "=" + order, null);
            Log.d("result update hour", String.valueOf(result));
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("error", "error while trying update weather hour");
        } finally {
            db.endTransaction();
        }
    }

    public void deleteCity(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_CURRENT_WEATHER, DatabaseConstant.ID_CITY + "=" + id, null);
            db.delete(TABLE_DAY, DatabaseConstant.ID_CITY + "=" + id, null);
            db.delete(TABLE_HOUR, DatabaseConstant.ID_CITY + "=" + id, null);
            db.delete(TABLE_CITY, DatabaseConstant.ID_CITY + "=" + id, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("Error: ", "error while trying delete a city");
        } finally {
            db.endTransaction();
        }
    }

    public City getFirstCity() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = String.format("Select %s,%s,%s,%s,%s from %s",
                DatabaseConstant.ID_CITY,
                DatabaseConstant.NAME,
                DatabaseConstant.FULLNAME,
                DatabaseConstant.LATITUDE,
                DatabaseConstant.LONGITUDE,
                TABLE_CITY
                );
        Cursor cursor = db.rawQuery(sql, null);
        City city = new City();
        if (cursor.moveToNext()) {
            city.setId(cursor.getInt(cursor.getColumnIndex(DatabaseConstant.ID_CITY)));
            city.setFullName(cursor.getString(cursor.getColumnIndex(DatabaseConstant.FULLNAME)));
            city.setName(cursor.getString(cursor.getColumnIndex(DatabaseConstant.NAME)));
            String latitude = cursor.getString(cursor.getColumnIndex(DatabaseConstant.LATITUDE));
            String longitude = cursor.getString(cursor.getColumnIndex(DatabaseConstant.LONGITUDE));
            city.setCoordinate(latitude + "," + longitude);
        }
        cursor.close();
        return city;
    }
}


