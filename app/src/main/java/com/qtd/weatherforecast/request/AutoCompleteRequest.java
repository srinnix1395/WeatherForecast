package com.qtd.weatherforecast.request;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qtd.weatherforecast.AppController;
import com.qtd.weatherforecast.callback.AutoCompleteRequestCallback;
import com.qtd.weatherforecast.constant.ApiConstant;

import org.json.JSONObject;

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
	
	private void filterLocation(JSONObject response) {
		Single.fromCallable(new Callable<String>() {
			@Override
			public String call() throws Exception {
				
				return null;
			}
		}).subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new SingleObserver<String>() {
					@Override
					public void onSubscribe(Disposable d) {
						
					}
					
					@Override
					public void onSuccess(String value) {
						if (callback != null) {
							Bundle bundle = new Bundle();
							bundle.putString(ApiConstant.RESULTS, value.toString());
							
							callback.onSuccess(bundle);
						}
					}
					
					@Override
					public void onError(Throwable e) {
						
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
