package com.qtd.weatherforecast.database;

import com.qtd.weatherforecast.constant.ApiConstant;
import com.qtd.weatherforecast.model.CurrentWeather;
import com.qtd.weatherforecast.model.Location;
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
	public static ArrayList<WeatherHour> getAllWeatherHours(String json) throws JSONException {
		JSONObject jsonObject = new JSONObject(json);
		return getAllWeatherHours(jsonObject);
	}
	
	public static ArrayList<WeatherHour> getAllWeatherHours(JSONObject response) {
		ArrayList<WeatherHour> arrHour = new ArrayList<>();
		try {
			JSONArray currentObservation = response.getJSONArray(ApiConstant.HOURLY_FORECAST);
			for (int i = 0; i < 24; i++) {
				JSONObject hour = currentObservation.getJSONObject(i);
				JSONObject fctime = hour.getJSONObject(ApiConstant.FCTIME);
				JSONObject temp = hour.getJSONObject(ApiConstant.TEMP);
				String icon = hour.getString(ApiConstant.ICON_URL);
				
				arrHour.add(new WeatherHour(fctime.getString(ApiConstant.HOUR) + ":00", hour.getString(ApiConstant.POP) + "%", temp.getInt(ApiConstant.METRIC), icon));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return arrHour;
	}
	
	public static ArrayList<WeatherDay> getAllWeatherDays(String json) throws JSONException {
		JSONObject jsonObject = new JSONObject(json);
		return getAllWeatherDays(jsonObject);
	}
	
	public static ArrayList<WeatherDay> getAllWeatherDays(JSONObject response) {
		ArrayList<WeatherDay> arrDays = new ArrayList<>();
		try {
			JSONObject forecast = response.getJSONObject(ApiConstant.FORECAST);
			JSONObject simpleForecast = forecast.getJSONObject(ApiConstant.SIMPLE_FORECAST);
			JSONArray forecastDay = simpleForecast.getJSONArray(ApiConstant.FORECAST_DAY);
			for (int i = 1; i < 7; i++) {
				JSONObject object = forecastDay.getJSONObject(i);
				JSONObject date = object.getJSONObject(ApiConstant.DATE);
				
				String weekday = date.getString(ApiConstant.WEEKDAY);
				weekday = weekday.substring(5);
				int highTemp = object.getJSONObject(ApiConstant.HIGH).getInt(ApiConstant.CELCIUS);
				int lowTemp = object.getJSONObject(ApiConstant.LOW).getInt(ApiConstant.CELCIUS);
				String weather = object.getString(ApiConstant.CONDITIONS);
				String icon = object.getString(ApiConstant.ICON_URL);
				
				arrDays.add(new WeatherDay(weekday, weather, highTemp, lowTemp, icon));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return arrDays;
	}
	
	public static CurrentWeather getCurrentWeather(String json) throws JSONException {
		JSONObject jsonObject = new JSONObject(json);
		return getCurrentWeather(jsonObject);
	}
	
	public static CurrentWeather getCurrentWeather(JSONObject response) {
		CurrentWeather currentWeather = new CurrentWeather();
		try {
			JSONObject currentObservation = response.getJSONObject(ApiConstant.CURRENT_OBSERVATION);
			
			String timeUpdate = currentObservation.getString(ApiConstant.LOCAL_TZ_OFFSET);
			int wind = currentObservation.getInt(ApiConstant.WIND_GUST);
			String humid = currentObservation.getString(ApiConstant.RELATIVE_HUMIDITY);
			String weather = currentObservation.getString(ApiConstant.WEATHER);
			int tempc = currentObservation.getInt(ApiConstant.TEMP_C);
			int uv = currentObservation.getInt(ApiConstant.UV);
			int feelslike = currentObservation.getInt(ApiConstant.FEELS_LIKE_C);
			String icon = currentObservation.getString(ApiConstant.ICON_URL);
			
			currentWeather = new CurrentWeather(icon, tempc, weather, humid, wind, uv, feelslike, timeUpdate, System.currentTimeMillis());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return currentWeather;
	}
	
	public static ArrayList<Location> getLocationAutocomplete(JSONObject response) {
		ArrayList<Location> arrayList = new ArrayList<>();
		try {
			JSONArray array = response.getJSONArray(ApiConstant.RESULTS);
			JSONObject object;
			for (int i = 0; i < array.length(); i++) {
				object = array.getJSONObject(i);
				if ((object.length() == 10 || object.length() == 9) && object.getString(ApiConstant.TYPE).equals(ApiConstant.CITY)) {
					arrayList.add(new Location(object.getString("name"), object.getString("lat") + "," + object.getString("lon")));
				}
			}
		} catch (JSONException je) {
			je.printStackTrace();
		}
		return arrayList;
	}
}
