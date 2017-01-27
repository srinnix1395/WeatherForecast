package com.qtd.weatherforecast.request;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qtd.weatherforecast.AppController;
import com.qtd.weatherforecast.callback.AutoCompleteRequestCallback;
import com.qtd.weatherforecast.constant.ApiConstant;
import com.qtd.weatherforecast.database.ProcessJson;
import com.qtd.weatherforecast.model.Location;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 12/19/2016.
 */

public class AutoCompleteRequest {
	private AutoCompleteRequestCallback callback;
	private String keyWord;
	
	public AutoCompleteRequest(Builder builder) {
		keyWord = builder.keyWord;
		callback = builder.callback;
	}
	
	public void request() {
		String url = ApiConstant.AUTOCOMPLETE_API;
		
		JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url + keyWord,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.d("fassd", "onResponse: " + response.toString());
						filterLocation(response);
					}
				}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.d("Error autocomplete", error.toString());
				if (callback != null) {
					callback.onFail(error.toString());
				}
			}
		});
		AppController.getInstance().addToRequestQueue(objectRequest);
	}
	
	private void filterLocation(final JSONObject response) {
		Single.fromCallable(new Callable<ArrayList<Location>>() {
			@Override
			public ArrayList<Location> call() throws Exception {
				return ProcessJson.getLocationAutocomplete(response);
			}
		}).subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new SingleObserver<ArrayList<Location>>() {
					@Override
					public void onSubscribe(Disposable d) {
						
					}
					
					@Override
					public void onSuccess(ArrayList<Location> value) {
						if (callback != null) {
							callback.onSuccess(value);
						}
					}
					
					@Override
					public void onError(Throwable e) {
						if (callback != null) {
							callback.onFail(e.toString());
						}
					}
				});
	}
	
	public static class Builder {
		private AutoCompleteRequestCallback callback;
		private String keyWord;
		
		public Builder withKeyword(String keyWord) {
			this.keyWord = keyWord;
			return this;
		}
		
		public Builder withCallback(AutoCompleteRequestCallback callback) {
			this.callback = callback;
			return this;
		}
		
		public AutoCompleteRequest build() {
			return new AutoCompleteRequest(this);
		}
	}
}
