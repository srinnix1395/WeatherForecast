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

    public void updateAllData(int idCity, CurrentWeather currentWeather, ArrayList<WeatherHour> arrHour, ArrayList<WeatherDay> arrDay) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            //update current weather
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

            //update weather hour
            int i = 0;
            for (WeatherHour mWeatherHour : arrHour) {
                ContentValues valuesHour = new ContentValues();
                valuesHour.put(DatabaseConstant.HOUR, mWeatherHour.getHour());
                valuesHour.put(DatabaseConstant.ICON, mWeatherHour.getIcon());
                valuesHour.put(DatabaseConstant.TEMP_C, mWeatherHour.getTemp());
                valuesHour.put(DatabaseConstant.CHANCE_RAIN, Integer.parseInt(mWeatherHour.getRain().replace('%', ' ').trim()));

                long resultHour = db.update(DatabaseConstant.TABLE_HOUR, valuesHour, DatabaseConstant._ID_CITY + "=" + idCity + " AND " + DatabaseConstant.ORDER + "=" + i, null);
                Log.d("result update hour", String.valueOf(resultHour));
                i++;
            }

            //update weather day
            i = 0;
            for (WeatherDay weatherDay : arrDay) {
                ContentValues valuesDay = new ContentValues();
                valuesDay.put(DatabaseConstant.DAY, weatherDay.getDay());
                valuesDay.put(DatabaseConstant.WEATHER, weatherDay.getWeather());
                valuesDay.put(DatabaseConstant.HIGH_TEMP, weatherDay.getHighTemp());
                valuesDay.put(DatabaseConstant.LOW_TEMP, weatherDay.getLowTemp());
                valuesDay.put(DatabaseConstant.ICON, weatherDay.getIcon());

                long resultDay = db.update(DatabaseConstant.TABLE_DAY, valuesDay, DatabaseConstant._ID_CITY + "=" + idCity + " AND " + DatabaseConstant.ORDER + "=" + i, null);
                Log.d("result update day", String.valueOf(resultDay));
                i++;
            }
            db.setTransactionSuccessful();

        } catch (Exception e) {
            Log.d("error", "error while trying update current weather");
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public void insertData(CurrentWeather currentWeather, ArrayList<WeatherHour> arrHour, ArrayList<WeatherDay> arrDay) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            //update current weather
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

            //update weather hour
            int i = 0;
            for (WeatherHour mWeatherHour : arrHour) {
                ContentValues valuesHour = new ContentValues();
                valuesHour.put(DatabaseConstant.HOUR, mWeatherHour.getHour());
                valuesHour.put(DatabaseConstant.ICON, mWeatherHour.getIcon());
                valuesHour.put(DatabaseConstant.TEMP_C, mWeatherHour.getTemp());
                valuesHour.put(DatabaseConstant.CHANCE_RAIN, Integer.parseInt(mWeatherHour.getRain().replace('%', ' ').trim()));

                long resultHour = db.update(DatabaseConstant.TABLE_HOUR, valuesHour, DatabaseConstant._ID_CITY + "=" + idCity + " AND " + DatabaseConstant.ORDER + "=" + i, null);
                Log.d("result update hour", String.valueOf(resultHour));
                i++;
            }

            //update weather day
            i = 0;
            for (WeatherDay weatherDay : arrDay) {
                ContentValues valuesDay = new ContentValues();
                valuesDay.put(DatabaseConstant.DAY, weatherDay.getDay());
                valuesDay.put(DatabaseConstant.WEATHER, weatherDay.getWeather());
                valuesDay.put(DatabaseConstant.HIGH_TEMP, weatherDay.getHighTemp());
                valuesDay.put(DatabaseConstant.LOW_TEMP, weatherDay.getLowTemp());
                valuesDay.put(DatabaseConstant.ICON, weatherDay.getIcon());

                long resultDay = db.update(DatabaseConstant.TABLE_DAY, valuesDay, DatabaseConstant._ID_CITY + "=" + idCity + " AND " + DatabaseConstant.ORDER + "=" + i, null);
                Log.d("result update day", String.valueOf(resultDay));
                i++;
            }
            db.setTransactionSuccessful();

        } catch (Exception e) {
            Log.d("error", "error while trying update current weather");
        } finally {
            db.endTransaction();
            db.close();
        }
    }
}


