package com.qtd.weatherforecast.database;

import com.qtd.weatherforecast.model.CurrentWeather;
import com.qtd.weatherforecast.model.WeatherDay;
import com.qtd.weatherforecast.model.WeatherHour;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Dell on 5/6/2016.
 */
public class ProcessJson {
    public static ArrayList<WeatherHour> getAllWeatherHours(JSONObject response) {
        ArrayList<WeatherHour> arrHour = new ArrayList<>();
        try {
            JSONArray currentObservation = response.getJSONArray("hourly_forecast");
            for (int i = 0; i < 24; i++) {
                JSONObject hour = currentObservation.getJSONObject(i);
                JSONObject fctime = hour.getJSONObject("FCTTIME");
                JSONObject temp = hour.getJSONObject("temp");
                String icon = hour.getString("icon_url");

                WeatherHour weatherHour = new WeatherHour(fctime.getString("hour") + ":00", hour.getString("pop") + "%", temp.getInt("metric"), icon);
                arrHour.add(weatherHour);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrHour;
    }

    public static ArrayList<WeatherDay> getAllWeatherDays(JSONObject response) {
        ArrayList<WeatherDay> arrDays = new ArrayList<>();
        try {
            JSONObject forecast = response.getJSONObject("forecast");
            JSONObject simpleForecast = forecast.getJSONObject("simpleforecast");
            JSONArray forecastDay = simpleForecast.getJSONArray("forecastday");
            for (int i = 1; i < 7; i++) {
                JSONObject object = forecastDay.getJSONObject(i);
                JSONObject date = object.getJSONObject("date");
                String weekday = date.getString("weekday");
                weekday = weekday.substring(5);
                int highTemp = object.getJSONObject("high").getInt("celsius");
                int lowTemp = object.getJSONObject("low").getInt("celsius");
                String weather = object.getString("conditions");
                String icon = object.getString("icon_url");

                WeatherDay weatherDay = new WeatherDay(weekday, weather, highTemp, lowTemp, icon);
                arrDays.add(weatherDay);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrDays;
    }

    public static CurrentWeather getCurrentWeather(JSONObject response) {
        CurrentWeather currentWeather = new CurrentWeather();
        try {
            JSONObject currentObservation = response.getJSONObject("current_observation");
            String timeUpdate = currentObservation.getString("local_tz_offset");
            int wind = currentObservation.getInt("wind_gust_kph");
            String humid = currentObservation.getString("relative_humidity");
            String weather = currentObservation.getString("weather");
            int tempc = currentObservation.getInt("temp_c");
            int uv = currentObservation.getInt("UV");
            int feelslike = currentObservation.getInt("feelslike_c");
            String icon = currentObservation.getString("icon_url");

            currentWeather = new CurrentWeather(icon, tempc, weather, humid, wind, uv, feelslike, timeUpdate);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return currentWeather;
    }
}
