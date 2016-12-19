package com.qtd.weatherforecast.request;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qtd.weatherforecast.AppController;
import com.qtd.weatherforecast.callback.RequestCallback;
import com.qtd.weatherforecast.constant.ApiConstant;

import org.json.JSONObject;

/**
 * Created by DELL on 12/19/2016.
 */

public class WeatherRequest {
	private String urlConditions;
	private String urlHourly;
	private String urlForecast10day;
	private RequestCallback requestCallback;
	private Bundle bundle;
	
	private WeatherRequest(Builder builder) {
		urlConditions = builder.urlCurrentWeather;
		urlHourly = builder.urlHourly;
		urlForecast10day = builder.urlForecast10day;
		requestCallback = builder.requestCallback;
		bundle = new Bundle();
	}
	
	private void requestCurrentWeather() {
		final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlConditions, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				Log.d("conditions", response.toString());
				bundle.putString(ApiConstant.CONDITIONS, response.toString());
				requestHourly();
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.d("error", error.toString());
				if (requestCallback != null) {
					requestCallback.onFail(error);
				}
			}
		});
		AppController.getInstance().addToRequestQueue(request);
	}
	
	private void requestHourly() {
		JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, urlHourly, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				Log.d("hourly", response.toString());
				bundle.putString(ApiConstant.HOURLY, response.toString());
				requestForecast10day();
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.d("error", error.toString());
				if (requestCallback != null) {
					requestCallback.onFail(error);
				}
			}
		});
		AppController.getInstance().addToRequestQueue(request1);
	}
	
	private void requestForecast10day() {
		JsonObjectRequest request2 = new JsonObjectRequest(Request.Method.GET, urlForecast10day, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				Log.d("forecast10day", response.toString());
				bundle.putString(ApiConstant.FORECAST10DAY, response.toString());
				if (requestCallback != null) {
					requestCallback.onSuccess(bundle);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.d("error", error.toString());
				if (requestCallback != null) {
					requestCallback.onFail(error);
				}
			}
		});
		AppController.getInstance().addToRequestQueue(request2);
	}
	
	public void request() {
		requestCurrentWeather();
	}
	
	public static class Builder {
		private String urlCurrentWeather;
		private String urlHourly;
		private String urlForecast10day;
		private RequestCallback requestCallback;
		
		public Builder withUrlCurrentWeather(String urlCurrentWeather) {
			this.urlCurrentWeather = urlCurrentWeather;
			return this;
		}
		
		public Builder withUrlHourly(String urlHourly) {
			this.urlHourly = urlHourly;
			return this;
		}
		
		public Builder withUrlForecast10Days(String urlForecast10day) {
			this.urlForecast10day = urlForecast10day;
			return this;
		}
		
		public Builder withCallback(RequestCallback requestCallback) {
			this.requestCallback = requestCallback;
			return this;
		}
		
		public WeatherRequest build() {
			return new WeatherRequest(this);
		}
	}
}
