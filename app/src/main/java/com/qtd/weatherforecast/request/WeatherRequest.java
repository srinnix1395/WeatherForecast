package com.qtd.weatherforecast.request;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.qtd.weatherforecast.AppController;
import com.qtd.weatherforecast.callback.RequestCallback;
import com.qtd.weatherforecast.constant.ApiConstant;
import com.qtd.weatherforecast.constant.AppConstant;
import com.qtd.weatherforecast.constant.DatabaseConstant;
import com.qtd.weatherforecast.database.MyDatabaseHelper;
import com.qtd.weatherforecast.database.ProcessJson;
import com.qtd.weatherforecast.model.CurrentWeather;
import com.qtd.weatherforecast.model.WeatherDay;
import com.qtd.weatherforecast.model.WeatherHour;
import com.qtd.weatherforecast.utils.SharedPreUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by DELL on 12/19/2016.
 */

public class WeatherRequest {
    public static final int RESULT_OK = 0;
    public static final int RESULT_NG = 1;

    private Context context;
    private String urlConditions;
    private String urlHourly;
    private String urlForecast10day;
    private RequestCallback requestCallback;
    private Bundle bundle;
    private int id;

    private WeatherRequest(Builder builder) {
        context = builder.context;
        id = builder.id;
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
                    requestCallback.onFail(error.toString());
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
                    requestCallback.onFail(error.toString());
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
                updateDatabase(bundle);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
                if (requestCallback != null) {
                    requestCallback.onFail(error.toString());
                }
            }
        });
        AppController.getInstance().addToRequestQueue(request2);
    }

    private void updateDatabase(final Bundle bundle) {
        Single.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                try {
                    MyDatabaseHelper databaseHelper = MyDatabaseHelper.getInstance(context);
                    CurrentWeather currentWeather = ProcessJson.getCurrentWeather(bundle.getString(ApiConstant.CONDITIONS));
                    ArrayList<WeatherHour> arrHour = ProcessJson.getAllWeatherHours(bundle.getString(ApiConstant.HOURLY));
                    ArrayList<WeatherDay> arrDay = ProcessJson.getAllWeatherDays(bundle.getString(ApiConstant.FORECAST10DAY));

                    if (id == AppConstant.ERROR_ID) {
                        databaseHelper.insertData(currentWeather, arrHour, arrDay);
                    } else {
                        databaseHelper.updateAllData(id, currentWeather, arrHour, arrDay);
                    }

                    if (SharedPreUtils.getInt(AppConstant._ID, -1) == id) {
                        SharedPreUtils.putLong(DatabaseConstant.LAST_UPDATE, System.currentTimeMillis());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    return RESULT_NG;
                }
                return RESULT_OK;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Integer value) {
                        if (requestCallback != null) {
                            requestCallback.onSuccess(value);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (requestCallback != null) {
                            requestCallback.onFail(e.getMessage());
                        }
                    }
                });
    }

    public void request() {
        requestCurrentWeather();
    }

    public static class Builder {
        private Context context;
        private String urlCurrentWeather;
        private String urlHourly;
        private String urlForecast10day;
        private int id = -1;
        private RequestCallback requestCallback;

        public Builder(Context context, int id) {
            this.context = context;
            this.id = id;
        }

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
